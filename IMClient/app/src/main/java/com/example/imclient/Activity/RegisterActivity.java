package com.example.imclient.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.ExitApplication;
import com.example.imclient.Utils.HttpRequest;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 用户注册界面
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    Button registerBtn;
    Button backBtn;
    EditText phoneText;
    EditText nameText;
    EditText passwordText;
    EditText confirmText;
    EditText ageText;
    RadioGroup sexRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ExitApplication.getInstance().addActivity(this);
        Toast.makeText(this,"进入注册页面",Toast.LENGTH_SHORT).show();

        phoneText = (EditText) findViewById(R.id.register_phone);
        nameText = (EditText) findViewById(R.id.register_name);
        passwordText = (EditText) findViewById(R.id.register_password);
        confirmText = (EditText) findViewById(R.id.register_confirm);
        ageText = (EditText) findViewById(R.id.register_age);
        sexRadioGroup = (RadioGroup)findViewById(R.id.register_sexGroup);
        backBtn = findViewById(R.id.register_cancel);
        backBtn.setOnClickListener(this);
        registerBtn = findViewById(R.id.register_btn);
        registerBtn.setOnClickListener(this);
    }

    /**
     * 检查注册的填写信息是否合法
     * @return
     */
    public boolean checkRegister(){
        if(TextUtils.isEmpty(phoneText.getText())
                ||TextUtils.isEmpty(nameText.getText())
                ||TextUtils.isEmpty(passwordText.getText())
                ||TextUtils.isEmpty(confirmText.getText())
                ||TextUtils.isEmpty(ageText.getText())){
            Toast.makeText(this,"请填写完整信息",Toast.LENGTH_SHORT).show();
            return false;
        }else if(!passwordText.getText().toString().equals(confirmText.getText().toString())){
            Toast.makeText(this,"两次密码不一致",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 注册按钮、取消按钮的监听事件
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.register_btn: {
                if(checkRegister()){
                    String phone = phoneText.getText().toString();
                    String name = nameText.getText().toString();
                    String password = passwordText.getText().toString();
                    String age = ageText.getText().toString();
                    String sex = ((RadioButton)findViewById(sexRadioGroup.getCheckedRadioButtonId())).getText().toString();
                    Register(phone,name,password,age,sex);
                }
                break;
            }
            case R.id.register_cancel:{
                finish();
            }
        }
    }

    /**
     * HttpRequest与服务器交互 插入mysql数据库新的用户信息
     * @param phone
     * @param name
     * @param password
     * @param age
     * @param sex
     */
    private void Register(String phone,String name,String password,String age,String sex){
        HttpRequest.register(phone, name, password, age, sex, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String result = response.body().string();
                    if(result.equals(Constant.NAMEREGISTERED)||result.equals(Constant.PHONEREGISTERED)){
                        registerBtn.post(()->Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show());
                    }else{
                        registerBtn.post(()->Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show());
                        finish();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
