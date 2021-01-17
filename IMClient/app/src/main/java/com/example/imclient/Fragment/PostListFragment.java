package com.example.imclient.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;
import com.alibaba.fastjson.JSONObject;
import com.example.imclient.Activity.PublishAboutActivity;
import com.example.imclient.Adapter.MyAdapter;
import com.example.imclient.Model.Post;
import com.example.imclient.Model.PostVO;
import com.example.imclient.R;
import com.example.imclient.Utils.HttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import static com.example.imclient.Activity.MainActivity.user;

/**
 * 好友动态界面
 */
public class PostListFragment extends Fragment {
    private View view;

    private List<PostVO> postVOS = new ArrayList<>();
    private MyHandler myHandler = new MyHandler();
    private MyAdapter myAdapter;

    public static String imageString;

    PopupWindow popWindow;
    Button popWindowDeleteBtn;
    Button popWindowUpdateBtn;
    Button popWindowCancelBtn;

    /**
     * 创建PostListFragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_post_list, container, false);
        getPostList();
       
        return view;
    }

    /**
     * 获得说说列表
     */
    private void getPostList() {
        HttpRequest.getPostList(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Message message = myHandler.obtainMessage();
                    message.obj = JSONObject.parseArray(response.body().string(), PostVO.class);
                    if(response.code()==200){
                        message.what = 1;
                    }else{
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
     * 使用Handler，将数据在主线程返回，更新说说列表
     */
    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {//在这里进行UI界面的更新，这个方法是运行在UI主线程中的
            int w = msg.what;
            //成功获得说说列表
            if(w == 1){
                //封装PostVOS
                postVOS = (List<PostVO>) msg.obj;
                int size = postVOS.size();
                for (int i=0; i<size; i++){
                    postVOS.get(i).setLiked(false);
                    //根据PostVO中的likesName判断当前用户是否点赞了该条说说
                    String likesName = postVOS.get(i).getLikes_name();
                    List<String> likesNameList = JSONObject.parseArray(likesName, String.class);
                    for (String name : likesNameList){
                        if(name.equals(user.getName())){
                            postVOS.get(i).setLiked(true);
                            break;
                        }
                    }
                }

                //创建RecyclerView、myAdapter并适配上面封装好的postVOS
                RecyclerView mRv = (RecyclerView)view.findViewById(R.id.post_rv);
                myAdapter = new MyAdapter(getActivity(), postVOS);
                mRv.setAdapter(myAdapter);
                mRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
                //监听单击事件
                myAdapter.setOnItemClickListener(new MyAdapter.OnItemClickListener() {
                    @Override
                    public void OnItemClick(final View view, Post post, boolean isLiked, int tag) {
                        if(tag == 0){ //点赞 or 取消赞
                            if(!isLiked){
                                likePost(user.getId(), post.getPost_id());
                                Toast.makeText(getActivity(),"已点赞",Toast.LENGTH_SHORT).show();
                            }else {
                                dontLikePost(user.getId(), post.getPost_id());
                                Toast.makeText(getActivity(), "已取消点赞", Toast.LENGTH_SHORT).show();
                            }
                            myAdapter.changeData(post.getPost_id());
                        }else{
                            //显示底部菜单栏
                            showBottomMenu(view);
                            //编辑说说
                            popWindowUpdateBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //跳转到PublishAboutActivity 设置原有的imageString 传入原有的content postId
                                    Intent intent = new Intent(getActivity(), PublishAboutActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putCharSequence("publishOrUpdate","update");
                                    bundle.putCharSequence("content", post.getContent());
                                    bundle.putInt("postId", post.getPost_id());
                                    imageString = post.getImage();
                                    intent.putExtras(bundle);
                                    getActivity().startActivity(intent);
                                }
                            });
                            //删除说说
                            popWindowDeleteBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    deletePost(post.getPost_id());
                                    popWindow.dismiss();
                                }
                            });
                            //取消操作
                            popWindowCancelBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popWindow.dismiss();
                                }
                            });
                        }
                    }
                });
                //监听长按事件
                myAdapter.setOnItemLongClickListener(new MyAdapter.OnItemLongClickListener(){
                    @Override
                    public void OnItemLongClick(final View view, Post post, int tag) {
                        if(tag == 1){ //删除说说
                            deletePost(post.getPost_id());
                        }else if(tag == 0){ //编辑说说
                            Intent intent = new Intent(getActivity(), PublishAboutActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putCharSequence("publishOrUpdate","update");
                            bundle.putCharSequence("content", post.getContent());
                            bundle.putInt("postId", post.getPost_id());
                            imageString = post.getImage();
                            intent.putExtras(bundle);
                            getActivity().startActivity(intent);
                        }
                    }
                });
            }
        }
    }


    /**
     * 点赞说说 插入likes表一条点赞记录
     * @param user_id
     * @param post_id
     */
    public void likePost(int user_id, int post_id) {
        HttpRequest.likePost(user_id, post_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    myAdapter.changeData(post_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 取消赞说说 删除likes表一条取消赞记录
     * @param user_id
     * @param post_id
     */
    public void dontLikePost(int user_id, int post_id) {
        HttpRequest.dontlikePost(user_id, post_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    myAdapter.changeData(post_id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 删除说说 根据post_id删除post表的一条说说 根据post_id删除相关的点赞记录
     * @param post_id
     */
    public void deletePost(int post_id) {
        HttpRequest.deletePost(post_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    myAdapter.removeData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 显示底部菜单栏
     */
    private void showBottomMenu(View v) {
        // 加载PopupWindow的布局
        View view = View.inflate(getActivity(), R.layout.pop_window, null);
        popWindowUpdateBtn = (Button) view.findViewById(R.id.pop_update);
        System.out.println("popWindowUpdateBtn："+popWindowUpdateBtn);
        popWindowDeleteBtn = (Button) view.findViewById(R.id.pop_delete);
        popWindowCancelBtn = (Button) view.findViewById(R.id.pop_cancel);

        popWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setBackgroundDrawable(new ColorDrawable(0));
        //设置动画
        popWindow.setAnimationStyle(R.style.popwin_anim_style);
        //设置popupwindow的位置
        popWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //设置背景半透明
        backgroundAlpha(0.6f);
        //点击空白位置，popupwindow消失的事件监听，这时候让背景恢复正常
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                backgroundAlpha(1.0f);
            }
        });
    }


    /**
     * 设置屏幕的背景透明度
     * @param bgAlpha
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha; // 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }

}
