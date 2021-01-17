package com.example.imclient.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.imclient.Activity.ChatActivity;
import com.example.imclient.Activity.MainActivity;
import com.example.imclient.Activity.RegisterActivity;
import com.example.imclient.Activity.UpdateInfoActivity;
import com.example.imclient.Model.MessageItem;
import com.example.imclient.Model.PostVO;
import com.example.imclient.Model.User;
import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.HttpRequest;
import com.example.imclient.Utils.ItemView;
import com.example.imclient.Utils.PwdDialog;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
import static com.example.imclient.Activity.MainActivity.client;
import static com.example.imclient.Activity.MainActivity.preferences;
import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Fragment.OnlineListFragment.target;
import static com.example.imclient.Fragment.OnlineListFragment.targetId;

/**
 * 用户信息主页
 */
public class HomePageFragment extends Fragment {

    private ImageView mHBack;
    private ImageView mHHead;
    private TextView mUserName;
    private TextView mUserVal;

    private ItemView mSet;
    private ItemView mPassword;
    private ItemView mLogout;
    private ItemView mAbout;

    PwdDialog pwdDialog; //弹出密码输入框
    Button checkBtn; //更新密码

    View view;
    private FragmentTransaction transaction;
    private AboutUsFragment aboutUsFragment;

    private String head; //头像字符串
    private Uri headUri; //头像Uri
    Bitmap headBitmap; //头像Bitmap

    /**
     * 创建Fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home_page, container, false);
        Toast.makeText(getActivity(), "进入主页页面", Toast.LENGTH_SHORT).show();

        initView();
        listenView();
        return view;
    }

    /**
     * 初始化控件
     */
    private void initView() {
        //绑定控件
        mHBack = (ImageView) view.findViewById(R.id.h_back);
        mHHead = (ImageView) view.findViewById(R.id.h_head);
        mUserName = (TextView) view.findViewById(R.id.user_name);
        mUserVal = (TextView) view.findViewById(R.id.user_val);
        mSet = (ItemView) view.findViewById(R.id.update_set);
        mPassword = (ItemView) view.findViewById(R.id.update_password);
        mLogout = (ItemView) view.findViewById(R.id.update_logout);
        mAbout = (ItemView) view.findViewById(R.id.update_about);
        checkBtn = (Button)view.findViewById(R.id.update_pwd_btn);

        //填充具体数据
        mUserName.setText(user.getName());
        mUserVal.setText(user.getPhone());
        head = user.getHead(); //从user中获得编码后的head，再解码封装为Bitmap

        if(head==null||head.equals("")){
            mHHead.setImageResource(R.drawable.user);
        }else{
            byte[] bytes = Base64.decode(head, Base64.DEFAULT);
            headBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            mHHead.setImageBitmap(headBitmap);
        }
    }

    /**
     * 设置监听控件
     */
    private void listenView() {
        if(head==null||head.equals("")){
            //设置背景磨砂效果
            Glide.with(this).load(R.drawable.user)
                    .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                    .into(mHBack);
            //设置圆形图像
            Glide.with(this)
                    .load(R.drawable.user)
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(mHHead);
        }else{
            //设置背景磨砂效果
            Glide.with(this).load(Base64.decode(head, Base64.DEFAULT))
                    .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                    .into(mHBack);
            //设置圆形图像
            Glide.with(this)
                    .load(Base64.decode(head, Base64.DEFAULT))
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(mHHead);
        }


        //上传头像
        mHHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
            }
        });
        //修改基本信息
        mSet.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                startActivity(intent);
            }
        });
        //修改密码
        mPassword.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                showEditDialog(view);
            }
        });
        //关于我们
        mAbout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                if(aboutUsFragment==null){
                    aboutUsFragment = new AboutUsFragment();
                }
                setFragment(aboutUsFragment);
            }
        });
        //注销登录
        mLogout.setItemClickListener(new ItemView.itemClickListener() {
            @Override
            public void itemClick(String text) {
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                build.setTitle("系统提示").setMessage("确定要注销登录吗？");
                build.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    client.shutdownOutput();
                                    client.setSoTimeout(5000);
                                    client.close();
                                    preferences.edit().clear().commit();
                                    Intent logoutIntent = new Intent(getActivity(), MainActivity.class);
                                    logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(logoutIntent);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                build.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).show();
            }
        });
    }

    /**
     * 对图片进行处理，根据图片的url值加载图片
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //如果选择照片成功就把照片数据的uri设置过来
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                //更新uri
                headUri = data.getData();
                //更新头像
                mHHead.setImageURI(headUri);
                Cursor cursor = getActivity().getContentResolver().query(headUri,null,null,null,null);
                if(cursor != null){
                    cursor.moveToFirst();
                }
            }
            //更新user的头像字符串
            ByteArrayOutputStream baos = new ByteArrayOutputStream();//将Bitmap转成Byte[]
            Bitmap bitmap = ((BitmapDrawable)mHHead.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);//压缩
            head = Base64.encodeToString(baos.toByteArray(),Base64.DEFAULT);//加密转换成String

            //设置背景磨砂效果
            Glide.with(this).load(Base64.decode(head, Base64.DEFAULT))
                    .bitmapTransform(new BlurTransformation(getActivity(), 25), new CenterCrop(getActivity()))
                    .into(mHBack);
            //设置圆形图像
            Glide.with(this)
                    .load(Base64.decode(head, Base64.DEFAULT))
                    .bitmapTransform(new CropCircleTransformation(getActivity()))
                    .into(mHHead);

            //更新当前static user
            user.setHead(head);
            //更新SharedPreferences
            SharedPreferences.Editor editor = preferences.edit(); //用户信息存入SharedPreferences
            editor.putString("userinfo", new Gson().toJson(user));
            editor.apply();
            //更新数据库
            updateHead(user.getId(), head);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * HttpRequest与服务器进行交互 更新mysql数据库中的头像字符串
     * @param userId
     * @param head
     */
    private void updateHead(int userId, String head) {
        HttpRequest.updateHead(userId, head, new Callback() {
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
     * 检查密码是否符合修改格式
     * @return
     */
    public boolean checkPsd(){
        if(TextUtils.isEmpty(pwdDialog.pwd_one.getText()) ||TextUtils.isEmpty(pwdDialog.pwd_two.getText())){
            Toast.makeText(getActivity(),"请填写完整信息",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!pwdDialog.pwd_two.getText().toString().trim().equals(pwdDialog.pwd_one.getText().toString().trim())){
            Toast.makeText(getActivity(),"两次密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 弹出修改密码的输入框
     * @param view
     */
    public void showEditDialog(View view) {
        pwdDialog = new PwdDialog(getActivity(),R.style.loading_dialog,onClickListener);
        pwdDialog.show();
    }

    /**
     * 对点击更新密码控件进行实际监听
     */
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //更新密码
                case R.id.update_pwd_btn:
                    //检查密码输入格式是否正确
                    if(checkPsd()){
                        String pwd_one = pwdDialog.pwd_one.getText().toString().trim();
                        //HttpRequest与服务器进行交互 更新mysql数据库中的密码
                        HttpRequest.updatePsd(user.getId(), pwd_one, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                            }
                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                try {
                                    String result = response.body().string();
                                    //提示用户更新失败
                                    if(result.equals(Constant.INFOLACK)||result.equals(Constant.FAILED)){
                                        view.post(()->Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show());
                                    }else{
                                        //提示用户更新成功
                                        view.post(()->Toast.makeText(getActivity(), "密码更新成功", Toast.LENGTH_SHORT).show());
                                        //更新当前用户的密码
                                        user.setPassword(pwd_one);
                                        //用户信息存入SharedPreferences
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("userinfo", new Gson().toJson(user));
                                        editor.apply();
                                        //销毁密码输入框
                                        pwdDialog.cancel();
                                    }
                                    pwdDialog.pwd_one.setText("");
                                    pwdDialog.pwd_two.setText("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                    break;
            }
        }
    };

    /**
     * 设置fragment
     * @param fragment
     */
    private void setFragment(Fragment fragment) {
        transaction = getFragmentManager().beginTransaction();
        //初始化transaction
        transaction.replace(R.id.frame_layout, fragment);
        //绑定id
        transaction.commit();
    }

}
