package com.example.imclient.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imclient.Model.MessageItem;
import com.example.imclient.Adapter.ChatAdapter;
import com.example.imclient.Adapter.AdapterBean;
import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.ExitApplication;
import com.example.imclient.Utils.HttpRequest;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.imclient.Activity.MainActivity.headChat;
import static com.example.imclient.Activity.MainActivity.message;
import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Fragment.OnlineListFragment.target;
import static com.example.imclient.Fragment.OnlineListFragment.targetId;

/**
 * 在线用户聊天界面
 */
public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerViewChat;
    private ChatAdapter adapterChat;
    private ArrayList<AdapterBean> list = new ArrayList<>();
    private EditText content;
    private Button btnSend;

    /**
     * 接收到广播后处理消息内容（在聊天列表中显示）
     */
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("MessageItem")) {
                //消息发送者或接收者与当前会话框匹配
                if (message.getRevId()==user.getId()&&message.getSendId()==targetId
                        || message.getRevId()==targetId&&message.getSendId()==user.getId()
                        || (message.getStatus().equals(Constant.GROUPCHAT)&&target.equals(Constant.GROUPCHAT))) {
                    if (message.getSendId()==user.getId()) { //发送方
                        AdapterBean bean = new AdapterBean(message.getContent(), 2, message.getTime(), "");
                        list.add(bean);
                    } else if(message.getStatus().equals(Constant.GROUPCHAT)){ //群聊
                        AdapterBean bean = new AdapterBean(message.getContent(), 1, message.getTime(), ("(from:" + message.getSendName()+")"));
                        list.add(bean);
                    } else{ //接收方
                        AdapterBean bean = new AdapterBean(message.getContent(), 1, message.getTime(), "");
                        list.add(bean);
                    }

                    // 向适配器set数据
                    adapterChat.setData(list);
                    recyclerViewChat.setAdapter(adapterChat);
                    LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
                    recyclerViewChat.setLayoutManager(manager);
                    Toast.makeText(ChatActivity.this, "消息来啦", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    /**
     * 创建ChatActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ExitApplication.getInstance().addActivity(this);

        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("MessageItem");
        registerReceiver(broadcastReceiver, intentFilter);

        list = new ArrayList<>();
        content = (EditText) findViewById(R.id.content);
        btnSend = (Button) findViewById(R.id.btn_send);
        headChat = (TextView) findViewById(R.id.head_chat);
        headChat.setText(target);

        //先加载历史聊天记录
        initHistoryMessage();

        recyclerViewChat = (RecyclerView) findViewById(R.id.recyclerView_chat);
        adapterChat = new ChatAdapter(this);
        LinearLayoutManager manager = new LinearLayoutManager(ChatActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerViewChat.setLayoutManager(manager);
        adapterChat.setData(list);
        recyclerViewChat.setAdapter(adapterChat);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!TextUtils.isEmpty(content.getText())){
                    //设置日期格式
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String sendMessage;
                    MessageItem messageItem;
                    if (target.equals(Constant.GROUPCHAT)) {
                        messageItem = new MessageItem(content.getText().toString(), df.format(new Date()),user.getName(),"",user.getId(),-1,Constant.GROUPCHAT);
                        Toast.makeText(getApplicationContext(), "群聊...", Toast.LENGTH_SHORT).show();
                    } else if (target.equals(user.getName())) {
                        messageItem = new MessageItem(content.getText().toString(), df.format(new Date()),user.getName(),target,user.getId(), targetId, Constant.MYSELF);
                        Toast.makeText(getApplicationContext(), "和自己聊...", Toast.LENGTH_SHORT).show();
                    } else {
                        messageItem = new MessageItem(content.getText().toString(), df.format(new Date()),user.getName(),target,user.getId(), targetId, Constant.OTHERS);
                        Toast.makeText(getApplicationContext(), "和别人聊...", Toast.LENGTH_SHORT).show();
                    }
                    sendMessage = (new Gson().toJson(messageItem)) + "\n";

                    insertChat(messageItem.getSendId(), messageItem.getRevId(), messageItem.getContent(), messageItem.getTime());
                    if (MainActivity.client != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d("test", "客户端发送：" + sendMessage);
                                    MainActivity.client.getOutputStream().write(sendMessage.getBytes("utf-8"));// 获取从客户端得到的数据}
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                    content.setText("");
                }
            }
        });
    }

    /**
     * HttpRequest与服务器交互 插入聊天记录
     * @param sendId
     * @param revId
     * @param content
     * @param time
     */
    public void insertChat(int sendId, int revId, String content, String time) {
        HttpRequest.insertChat(sendId, revId, content, time, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Litepal数据库加载对应聊天记录
     */
    public void initHistoryMessage() {
        List<MessageItem> messageItemList;
        if(target.equals(Constant.GROUPCHAT)){ //进入群聊，显示所有群聊的记录
            messageItemList  = DataSupport
                    .where("status = ?", Constant.GROUPCHAT).find(MessageItem.class);
        }else{
            messageItemList  = DataSupport
                    .where("(sendId = ? and revId = ?) or (revId = ? and sendId = ?)",targetId+"",user.getId()+"",targetId+"",user.getId()+"").find(MessageItem.class);
        }
        if(!messageItemList.isEmpty()){
            list.clear();
            for (MessageItem messageItem : messageItemList){
                if (messageItem.getSendId()==user.getId()) { //我是发送方
                    AdapterBean bean = new AdapterBean(messageItem.getContent(), 2, messageItem.getTime(), "");
                    list.add(bean);
                } else if(messageItem.getStatus().equals(Constant.GROUPCHAT)) { //我是接收方
                    AdapterBean bean = new AdapterBean(messageItem.getContent(), 1, messageItem.getTime(), ("(from:" + messageItem.getSendName()+")"));
                    list.add(bean);
                } else{
                    AdapterBean bean = new AdapterBean(messageItem.getContent(), 1, messageItem.getTime(), (""));
                    list.add(bean);
                }
            }
        }
    }

    /**
     * 销毁广播
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    /**
     * 返回键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                //进入在线用户界面
                Intent intent = new Intent(ChatActivity.this, FragmentActivity.class);
                startActivity(intent);
//                finish();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
