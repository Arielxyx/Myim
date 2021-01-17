package com.example.imclient.Adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.imclient.Model.User;
import com.example.imclient.R;
import com.google.gson.Gson;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Fragment.OnlineListFragment.chatList;

/**
 * 在线用户列表的适配器
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    int position;
    private View view;
    private Context context;
    private User user;
    private List<User> userList;

    /**
     * OnlineListFragment适配器的构造函数
     * @param context
     * @param userList
     */
    public RecyclerViewAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    /**
     * 创建适配器ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_online, parent, false);
        return new ViewHolder(view);
    }

    /**
     * 绑定控件
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        System.out.println("TEST!!!");
        user = userList.get(position);
        if(user!=null){
            holder.userNameText.setText(user.getName());
            if(user.getHead()==null||user.getHead().equals("")){
                holder.userHeadImageView.setImageResource(R.drawable.user);
            }else{
                byte[] bytes = Base64.decode(user.getHead(), Base64.DEFAULT);
                holder.userHeadImageView.setImageBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                //设置圆形图像
                Glide.with(context)
                        .load(bytes)
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.userHeadImageView);
            }

            String content = chatList.get(position).getContent();
            String time = chatList.get(position).getTime();
            if(content==null||content.equals("")){
                holder.userChatText.setText("还没聊过呢，快来聊了ba...");
            }else{
                holder.userChatText.setText(content);
            }
            if(time==null||time.equals("")){
                holder.userTimeText.setText("one day in the future...");
            }else{
                holder.userTimeText.setText(time);
            }
        }
    }

    /**
     * 定义声明控件的内部类
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView userNameText;
        ImageView userHeadImageView;
        TextView userChatText;
        TextView userTimeText;

        public ViewHolder(View view) {
            super(view);
            userNameText = (TextView)view.findViewById(R.id.online_name);
            userHeadImageView = (ImageView)view.findViewById(R.id.online_head);
            userChatText = (TextView)view.findViewById(R.id.online_chat);
            userTimeText = (TextView)view.findViewById(R.id.online_time);

            //监听单击事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    position = getAdapterPosition();
                    if(onItemClickListener!=null){
                        onItemClickListener.OnItemClick(v, userList.get(getLayoutPosition()).getName());
                    }
                }
            });
            //监听长按事件
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    position = getAdapterPosition();
                    if(onItemClickListener!=null){
                        onItemLongClickListener.OnItemLongClick(v,userList.get(getAdapterPosition()).getName());
                    }
                    return true;
                }
            });
        }
    }

    /**
     * 获得item数目
     * @return
     */
    @Override
    public int getItemCount() {
        if(userList==null)
            return 0;
        return userList.size();
    }

    /**
     * 设置点击item的监听事件的接口
     */
    private OnItemClickListener onItemClickListener;
    public interface OnItemClickListener {
        /**
         * 当RecyclerView某个被点击的时候回调
         * @param view 点击item的视图
         * @param data 点击得到的数据
         */
        public void OnItemClick(View view, String data);
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    /**
     * 设置长按item的监听事件接口
     */
    private OnItemLongClickListener onItemLongClickListener;
    public interface OnItemLongClickListener {
        public void OnItemLongClick(View view, String data);
    }
    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

}

