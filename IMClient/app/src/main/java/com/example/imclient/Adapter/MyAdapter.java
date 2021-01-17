package com.example.imclient.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.imclient.Model.Post;
import com.example.imclient.Model.PostVO;
import com.example.imclient.R;
import com.example.imclient.Utils.HttpRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import static com.example.imclient.Activity.MainActivity.user;

/**
 * 好友动态列表的适配器
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context context;
    private MyHandler myHandler = new MyHandler();

    private int position;
    private String likesNameString = "";
    private List<PostVO> postVOS;
    private List<String> likesNameList = new ArrayList<>();

    /**
     * 初始化适配器的构造函数
     * @param context
     * @param postVOS
     */
    public MyAdapter(Context context, List<PostVO> postVOS) {
        this.context = context;
        this.postVOS = postVOS;
    }

    /**
     * 创建视图
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * 绑定item 向holder中填充具体数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //发说说的用户名、时间、说说内容、说说配图
        holder.postUserNameText.setText(postVOS.get(position).getPost_name());
        holder.postTimeText.setText(postVOS.get(position).getPost().getTime());
        holder.postContentText.setText(postVOS.get(position).getPost().getContent());
        byte[] postImagebytes = Base64.decode(postVOS.get(position).getPost().getImage(), Base64.DEFAULT);
        holder.postImage.setImageBitmap(BitmapFactory.decodeByteArray(postImagebytes, 0, postImagebytes.length));

        //点赞的用户名列表String类型likesNameString
        likesNameString = "";
        likesNameList = JSONObject.parseArray(postVOS.get(position).getLikes_name(), String.class);
        int size = likesNameList.size();
        if(size>0){
            likesNameString = likesNameList.get(0);
            for (int i=1;i<size;i++){
                likesNameString+=("、"+likesNameList.get(i));
            }
            holder.likesNameTextView.setText(likesNameString+" 等 "+likesNameList.size()+" 人觉得很赞...");
        }else{
            holder.likesNameTextView.setText("");
        }

        if(postVOS.get(position).getPostUserHead()==null||postVOS.get(position).getPostUserHead().equals("")){
            holder.postUserHead.setImageResource(R.drawable.user);
            //设置圆形图像
            Glide.with(context)
                    .load(R.drawable.user)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.postUserHead);
        }else{
            //用户头像
            byte[] postUserHeadbytes = Base64.decode(postVOS.get(position).getPostUserHead(), Base64.DEFAULT);
            holder.postUserHead.setImageBitmap(BitmapFactory.decodeByteArray(postUserHeadbytes, 0, postUserHeadbytes.length));
            //设置圆形图像
            Glide.with(context)
                    .load(postUserHeadbytes)
                    .bitmapTransform(new CropCircleTransformation(context))
                    .into(holder.postUserHead);
        }


        //点赞显示文字的设置
        if(postVOS.get(position).isLiked())
            holder.toLikeTextView.setText("取消");
        else
            holder.toLikeTextView.setText("点赞");
    }

    /**
     * 用于填充每个item内容的MyViewHolder类
     */
    class MyViewHolder extends RecyclerView.ViewHolder {
        //声明空间为MyViewHolder的成员变量
        TextView postUserNameText;
        TextView postTimeText;
        TextView postContentText;
        ImageView postImage;
        ImageView postUserHead;

        ImageView likeImageView;
        TextView likesNameTextView;
        TextView toLikeTextView;
        ImageView commentImageView;

        ImageView toPopWindowImageView;

        //MyViewHolder的构造函数
        MyViewHolder(View itemView) {
            super(itemView);
            //绑定控件
            postContentText = itemView.findViewById(R.id.post_content);
            postUserNameText = itemView.findViewById(R.id.post_user_name);
            postTimeText = itemView.findViewById(R.id.post_time);
            postImage = itemView.findViewById(R.id.post_image);
            postUserHead = itemView.findViewById(R.id.post_user_head);

            likeImageView = (ImageView)itemView.findViewById(R.id.like_image_view);
            likesNameTextView = (TextView)itemView.findViewById(R.id.likes_name_tv);
            toLikeTextView = (TextView)itemView.findViewById(R.id.to_like_tv);
            commentImageView = (ImageView)itemView.findViewById(R.id.comment_image_view);

            toPopWindowImageView = (ImageView)itemView.findViewById(R.id.toPopWindow);

            //单击的监听事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if(onItemClickListener!=null){
                        //点赞 or 取消赞
                        likeImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                boolean isLiked = postVOS.get(position).isLiked();
                                if(isLiked) //取消赞：通知向服务器发送取消赞信息，删除mysql数据库的相关记录
                                    postVOS.get(position).setLiked(false);
                                else //点赞：通知向服务器发送点赞信息，插入mysql数据库
                                    postVOS.get(position).setLiked(true);
                                onItemClickListener.OnItemClick(v, postVOS.get(position).getPost(), isLiked, 0);
                            }
                        });

                        //编辑 or 删除
                        toPopWindowImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(postUserNameText.getText().toString().equals(user.getName()))
                                    onItemClickListener.OnItemClick(v, postVOS.get(position).getPost(), false, 1);
                            }
                        });
                    }
                }
            });
            //长按编辑、删除的监听事件
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    position = getAdapterPosition();
                    if(onItemClickListener!=null){
                        //是当前用户发的说说
                        if(postUserNameText.getText().toString().equals(user.getName())){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context); //实例化AlertDialog
                            alertDialogBuilder.setTitle("选择操作"); //设置弹窗标题
                            alertDialogBuilder.setIcon(R.drawable.select); //设置弹窗图片
                            alertDialogBuilder.setItems(R.array.itemOperation, new android.content.DialogInterface.OnClickListener() { //设置弹窗选项内容
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                // 编辑
                                                case 0:
                                                    onItemLongClickListener.OnItemLongClick(v,postVOS.get(getLayoutPosition()).getPost(), 0);
                                                    break;
                                                // 删除
                                                case 1:
                                                    onItemLongClickListener.OnItemLongClick(v,postVOS.get(getLayoutPosition()).getPost(), 1);
                                                    break;
                                            }
                                        }
                                    });
                            alertDialogBuilder.create();//创造弹窗
                            alertDialogBuilder.show();//显示弹窗
                        }
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 点赞 or 取消赞修改了数据
     * @param post_id
     */
    public void changeData(int post_id) {
        getLikesListByPostId(post_id);
    }

    /**
     * 删除说说
     */
    public void removeData() {
        //删除动画
        postVOS.remove(position);
        System.out.println("已删除说说");
        Message message = myHandler.obtainMessage();
        message.what = 2;
        myHandler.sendMessage(message);
    }

    /**
     * 根据postId获得点赞姓名列表
     * @param post_id
     */
    public void getLikesListByPostId(int post_id) {
        HttpRequest.getLikesListByPostId(post_id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    Message message = myHandler.obtainMessage();
                    String str = response.body().string();
                    System.out.println("getLikesListByPostId onResponse："+str);
                    message.obj = str;
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
    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int w = msg.what;
            //成功根据postId获得点赞姓名列表
            if(w==1){
                postVOS.get(position).setLikes_name((String) msg.obj);
                postVOS.set(position, postVOS.get(position));
                notifyItemChanged(position);
                notifyDataSetChanged();
            }else if(w==2){ //删除说说
                notifyItemRemoved(position);
                notifyDataSetChanged();
            }
        }
    }

    /**
     * 获得item数目
     * @return
     */
    @Override
    public int getItemCount() {
        if(postVOS == null)
            return 0;
        return postVOS.size();
    }

    /**
     * 声明监听器：长按、点击
     */
    private MyAdapter.OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        public void OnItemClick(View view, Post post, boolean isLiked, int tag);
    }
    public void setOnItemClickListener(MyAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    private MyAdapter.OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener {
        public void OnItemLongClick(View view, Post post, int tag);
    }
    public void setOnItemLongClickListener(MyAdapter.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
