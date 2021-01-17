package com.example.imclient.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.imclient.Activity.ChatActivity;
import com.example.imclient.Adapter.RecyclerViewAdapter;
import com.example.imclient.Model.Chat;
import com.example.imclient.Model.MessageItem;
import com.example.imclient.Model.User;
import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.HttpRequest;

import org.litepal.crud.DataSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Activity.MainActivity.userNameList;

/**
 * 在线用户界面
 */
public class OnlineListFragment extends Fragment {
    private View view;
    public List<User> users = new ArrayList<>();
    public static int targetId;
    public static String target="聊天室";

    public RecyclerViewAdapter adapterOnline; //自定义recyclerveiw的适配器adapterOnline
    public RecyclerView recyclerViewOnline; //定义RecyclerView
    public MyHandler myHandler = new MyHandler(); //定义主线程处理myHandler

    String str;
    public static List<Chat> chatList;

    //广播接收器
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals("UpdateList")){
                //根据MainActivity中的userNameList(String用空格拼接)
                str = "";
                for (String s : userNameList) {
                    str = str + s + " ";
                }
                //与服务器交互 查询真正的userList
                getUserListByNameList(str);
            }
        }
    };


    /**
     * 初始化OnlineListFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_online_list, container, false);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("UpdateList");
        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        //根据MainActivity中的userNameList(String用空格拼接)
        str = "";
        for (String s : userNameList) {
            str = str + s + " ";
        }
        System.out.println("onCreateView----------------"+str);
        //与服务器交互 查询真正的userList
        getUserListByNameList(str);

        return view;
    }

    /**
     * HttpRequest与服务器进行交互 根据用户名nameListString在mysql数据库中查询userList
     * @param nameListString
     */
    private void getUserListByNameList(String nameListString) {
        HttpRequest.getUserListByNameList(nameListString, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String str= response.body().string();
                    System.out.println("getUserListByNameList接收到的: " + str);
                    //将接受到的userList传入主线程中处理
                    Message message = myHandler.obtainMessage();
                    message.obj = JSONObject.parseArray(str, User.class); //TODO?? com.alibaba.fastjson.JSONException: exepct '[', but {, pos 1, json : {"timestamp":"2020-12-07T14:22:52.708+0000","status":500,"error":"Internal Server Error","message":"No message available","path":"/usercontroller/getUserListByNameList"}
                    if(response.code()==200){//后台正常处理，没有发生异常的情况
                        message.what = 1;
                    }else{                  //后台发生了异常
                        message.what = 0;
                    }
                    myHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 使用Handler，将数据在主线程返回
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int w = msg.what;
            //成功获得userList列表
            if (w == 1) {
                users.clear();
                users.add(new User("聊天室"));
                users.addAll((List<User>) msg.obj);
                if(!(str==null))
                    getChatList(user.getId(),str);
            }
            else if(w == 2){
                //处理回调的数据
                chatList = (List<Chat>) msg.obj;
                //获取RecyclerView
                recyclerViewOnline = (RecyclerView) view.findViewById(R.id.rv);
                //创建adapter
                adapterOnline = new RecyclerViewAdapter(getActivity(), users);
                //给RecyclerView设置adapter
                recyclerViewOnline.setAdapter(adapterOnline);
                //刷新在线用户列表
                adapterOnline.notifyDataSetChanged();
                // 设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局 参数是：上下文、列表方向（横向还是纵向）、是否倒叙
                recyclerViewOnline.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                //监听单击事件
                adapterOnline.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(final View view, final String userName) {
                        //跳转到ChatActivity中进行聊天
                        if (!userName.equals("聊天室")) {
                            target = userName;
                            SelectTargetId(target);
                        } else {
                            target = "聊天室";
                        }
                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                        startActivity(intent);
                    }
                });
                //监听长按事件
                adapterOnline.setOnItemLongClickListener(new RecyclerViewAdapter.OnItemLongClickListener() {
                    @Override
                    public void OnItemLongClick(final View view, final String user) {
                    }
                });
            }
        }
    }

    /**
     * 根据当前用户名和在线用户列表获得最近的聊天记录
     * @param userId
     * @param userNameList
     */
    public void getChatList(int userId, String userNameList) {
        HttpRequest.getChatList(userId, userNameList, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Message message = myHandler.obtainMessage();
                    message.obj = JSONObject.parseArray(response.body().string(), Chat.class);
                    //后台正常处理，没有发生异常的情况
                    if(response.code()==200){
                        message.what = 2;
                    }else{
                        //后台发生了异常
                        message.what = 0;
                    }
                    //由主线程处理抛出的数据、更新UI
                    myHandler.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据进入聊天的用户名查询id
     * @param name
     */
    private void SelectTargetId(String name) {
        HttpRequest.SelectTargetId(name, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    if (!result.equals(Constant.FAILED))
                        targetId = Integer.parseInt(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 销毁广播
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }
}