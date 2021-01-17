package com.example.imclient.Utils;

import android.util.Log;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpRequest {
    //聊天----------------------------------------------------------------------------------
    public static void insertChat(int sendId, int revId, String content, String time, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("send_id", sendId+"")
                .add("rev_id", revId+"")
                .add("content", content)
                .add("time", time)
                .build();

        Request request = new Request.Builder()
                .url(Constant.INSERTCHAT)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    public static void getChatList(int userId, String userNameList, Callback callback){
        //创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //异步post请求，使用FormBody添加多个键值对
        FormBody formBody = new FormBody.Builder()
                .add("userId", userId+"")
                .add("userNameList", userNameList)
                .build();
        //使用builder模式创建request对象（按照服务器端的映射地址填写url）
        Request request = new Request.Builder()
                .url(Constant.GETCHATLIST)
                .post(formBody)
                .build();
        //加入请求调度
        okHttpClient.newCall(request).enqueue(callback);
    }

    //动态----------------------------------------------------------------------------------
    public static void publish(int userId, String publishContent, String publishImage, Callback callback){
        System.out.println("publish userId："+userId);
        System.out.println("publish content："+publishContent);
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("user_id", userId+"")
                .add("content", publishContent)
                .add("image", publishImage)
                .build();

        Request request = new Request.Builder()
                .url(Constant.PUBLISH)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getPostList(Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(Constant.GETPOSTLIST)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void likePost(int user_id, int post_id, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("user_id", user_id+"")
                .add("post_id", post_id+"")
                .build();

        Request request = new Request.Builder()
                .url(Constant.LIKEPOST)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void dontlikePost(int user_id, int post_id, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("user_id", user_id+"")
                .add("post_id", post_id+"")
                .build();

        Request request = new Request.Builder()
                .url(Constant.DONTLIKEPOST)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getLikesListByPostId(int post_id, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("post_id", post_id+"")
                .build();

        Request request = new Request.Builder()
                .url(Constant.GETLIKESLISTBYPOSTID)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void deletePost(int post_id, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("post_id", post_id+"")
                .build();

        Request request = new Request.Builder()
                .url(Constant.DELETEPOST)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void update(int postId, String publishContent, String publishImage, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("post_id",postId+"")
                .add("content", publishContent)
                .add("image", publishImage)
                .build();

        Request request = new Request.Builder()
                .url(Constant.UPDATEPOST)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    //用户----------------------------------------------------------------------------------
    public static void register(String phone,String name, String password, String age, String sex, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("phone", phone)
                .add("name", name)
                .add("password", password)
                .add("sex", sex)
                .add("age", age).build();
        Request request = new Request.Builder()
                .url(Constant.REGISTER)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void login(String phone,String password,Callback callback){
        //创建okHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();
        //异步post请求，使用FormBody添加多个键值对
        FormBody formBody = new FormBody.Builder()
                .add("phone",phone)
                .add("password",password).build();



        //使用builder模式创建request对象（按照服务器端的映射地址填写url）
        Request request = new Request.Builder()
                .url(Constant.LOGIN)
                .post(formBody)
                .build();

        Log.d("test","httpRequest-----------------login");
        //加入请求调度
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void updatePsd(int id,String password,Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id",id+"")
                .add("password",password).build();
        Request request = new Request.Builder()
                .url(Constant.UPDATEPSD)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void SelectTargetId(String name,Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("name",name).build();
        Request request = new Request.Builder()
                .url(Constant.SELECTTARGETID)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void updateInfo(int id,String phone,String name, int age, String sex, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id", id+"")
                .add("phone", phone)
                .add("name", name)
                .add("age", age+"")
                .add("sex", sex).build();
        Request request = new Request.Builder()
                .url(Constant.UPDATEINFO)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void updateHead(int id, String head, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("id", id+"")
                .add("head", head).build();
        Request request = new Request.Builder()
                .url(Constant.UPDATEHEAD)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void getUserListByNameList(String nameListString, Callback callback) {
        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("nameListString", nameListString).build();
        Request request = new Request.Builder()
                .url(Constant.GETUSERLISTBYNAMELIST)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

}
