package com.example.imclient.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.imclient.Model.MessageItem;
import com.example.imclient.R;
import com.example.imclient.Model.User;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.ExitApplication;
import com.example.imclient.Utils.HttpRequest;
import com.google.gson.Gson;

import org.litepal.LitePal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 刚进入APP的底层主界面
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginBtn;
    EditText phoneText;
    EditText passwordText;

    public static Socket client;
    public static MessageItem message;
    public static SharedPreferences preferences;

    public static TextView head;
    public static TextView headChat;

    public static User user;
    public static List<String> userNameList = new ArrayList<>();

    /**
     * 初始化 MainActivity
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ExitApplication.getInstance().addActivity(this);
        //自动创建EasyClub数据库
        LitePal.getDatabase();

        //绑定控件
        TextView registerText = (TextView) findViewById(R.id.login_register);
        registerText.setOnClickListener(this);
        loginBtn = findViewById(R.id.login_btn);
        phoneText = (EditText) findViewById(R.id.login_phone);
        passwordText = (EditText) findViewById(R.id.login_password);
        loginBtn.setOnClickListener(this);

        //判断用户是否已经登录过（可以从preference里面查找出来）
        preferences = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        String result = preferences.getString("userinfo", "");
        if (result != "") {
            user = new Gson().fromJson(result, User.class); //获取当前的用户信息
            connectServer(); //与服务器建立连接
            startActivity(new Intent(this, FragmentActivity.class)); //直接进入在线用户列表界面
        }
    }

    /**
     * 监听事件：点击登录 or 注册
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn: { //登录按钮
                if(checkLogin()){
                    String phone = phoneText.getText().toString();
                    String password = passwordText.getText().toString();
                    Login(phone, password);
                }
                break;
            }
            case R.id.login_register: { //注册按钮
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * 前端检查登录信息是否有效
     * @return
     */
    public boolean checkLogin(){
        if(TextUtils.isEmpty(phoneText.getText()) ||TextUtils.isEmpty(passwordText.getText())){
            Toast.makeText(this,"请填写完整信息",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * HttpRequest与服务器进行交互 从主界面输入用户名、密码登录 查询mysql判断用户登录信息是否正确
     * @param phone
     * @param password
     */
    private void Login(String phone, String password) {
        HttpRequest.login(phone, password, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) { }
            //回调函数 获得服务器的返回信息
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    if (!result.equals(Constant.LOGINALREADY)&&!result.equals(Constant.LOGINERROR)) {
                        loginBtn.post(() -> Toast.makeText(MainActivity.this, "登录成功", Toast.LENGTH_SHORT).show());

                        //用户信息存入SharedPreferences
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("userinfo", result);
                        editor.apply();
                        //实例化当前用户
                        user = new Gson().fromJson(result, User.class);
                        //用户登录成功后连接socket端
                        connectServer();

                        //进入在线用户界面
                        Intent intent = new Intent(MainActivity.this, FragmentActivity.class);
                        startActivity(intent);
                    } else {
                        //已有用户登录 或 登录信息填写错误
                        loginBtn.post(() -> Toast.makeText(MainActivity.this, result, Toast.LENGTH_SHORT).show());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Socket连接服务器 先根据IP地址和端口号请求连接，并启动子线程进行进一步的消息传输工作。
     */
    public void connectServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 网络访问最好放在线程中192.168.1.102
                    client = new Socket("192.168.43.221", 1234);
                    // 启动子线程
                    new ConnectThread(client).start();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 子线程 用于接收服务器的刷新用户列表信息、聊天记录
     */
    class ConnectThread extends Thread {
        String receiveMessage;
        Socket socket;
        BufferedReader br;
        PrintWriter pw;

        //线程的构造函数，只执行一次 构造函数内将用户信息传给服务器端 ，表示当前用户已上线
        public ConnectThread(Socket socket) throws IOException {
            super();
            this.socket = socket;
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream());
            pw.println(new Gson().toJson(user));
            pw.flush();
        }

        //实时接收服务器的列表记录、聊天记录
        @Override
        public void run() {
            try {
                while (isAlive()) {
                    //接收服务器端 发来的信息 在run函数内实时接受服务器端转发的消息，转换成MessageItem后判断消息类型
                    if ((receiveMessage = br.readLine()) != null) {
                        MessageItem messageItem = new Gson().fromJson(receiveMessage.replaceAll("�",""), MessageItem.class);

                        //如果是上线、下线、修改信息，则需要更新userList在线用户列表，向在线用户列表界面广播，接受到广播后则会刷新用户列表。
                        if (messageItem.getStatus().equals(Constant.ONLINEUPDARE)||messageItem.getStatus().equals(Constant.ONLINE) || messageItem.getStatus().equals(Constant.OFFLINE)) { //刷新在线用户列表
                            //更新userList在线用户列表
                            String names[] = messageItem.getContent().split(" ");
                            userNameList.clear();
                            userNameList.add("聊天室");
                            for (String name : names) {
                                userNameList.add(name);
                            }
                            //Toast提示用户
                            if(messageItem.getStatus().equals(Constant.OFFLINE))
                                loginBtn.post(()->Toast.makeText(getApplicationContext(),messageItem.getSendName()+"下线啦",Toast.LENGTH_SHORT).show());
                            if(messageItem.getStatus().equals(Constant.ONLINE))
                                loginBtn.post(()->Toast.makeText(getApplicationContext(),messageItem.getSendName()+"上线啦",Toast.LENGTH_SHORT).show());
                            //向在线用户列表界面广播，接受到广播后则会刷新用户列表
                            Log.d("test", "--（刷新列表）" + new Gson().toJson(userNameList));
                            sendBroadcast(new Intent("UpdateList"));
                        } else if(messageItem.getStatus().equals(Constant.HEADUPDARE)){
                            sendBroadcast(new Intent("UpdateList"));
                        } else{
                            loginBtn.post(()->Toast.makeText(getApplicationContext(),messageItem.getSendName()+"来消息啦，快看看吧~",Toast.LENGTH_LONG).show());
                            //如果是聊天信息，则向聊天界面广播，接收到广播后则会首先判断当前用户是否在会话界面中，是的才会刷新列表
                            Log.d("test", "--（聊天信息）" + receiveMessage);
                            //将收到的聊天记录存至本地数据库
                            message = messageItem;
                            message.save();message.save();
                            //向在线用户列表界面广播，接受到广播后则会刷新用户列表
                            sendBroadcast(new Intent("UpdateList"));
                            //向聊天界面广播，接收到广播后则会首先判断当前用户是否在会话界面中，是的才会刷新列表
                            sendBroadcast(new Intent("MessageItem"));
                        }
                    }else{
                        break;
                    }
                    sleep(200);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
