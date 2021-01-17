package com.example.imclient.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.imclient.Fragment.HomePageFragment;
import com.example.imclient.Model.MessageItem;
import com.example.imclient.Model.User;
import com.example.imclient.R;
import com.example.imclient.Utils.Constant;
import com.example.imclient.Utils.ExitApplication;
import com.example.imclient.Utils.HttpRequest;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.imclient.Activity.MainActivity.message;
import static com.example.imclient.Activity.MainActivity.preferences;
import static com.example.imclient.Activity.MainActivity.user;
import static com.example.imclient.Fragment.OnlineListFragment.target;

/**
 * 修改用户基本信息
 */
public class UpdateInfoActivity extends AppCompatActivity{

    //声明定义控件
    Button updateInfoBtn;
    EditText phoneText;
    EditText nameText;
    EditText ageText;
    RadioGroup sexRadioGroup;
    RadioButton rb;

    public String phone;
    public String name;
    public int age;
    public String sex;

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        ExitApplication.getInstance().addActivity(this);

        phoneText = (EditText)findViewById(R.id.update_phone);
        nameText = (EditText)findViewById(R.id.update_name);
        sexRadioGroup = (RadioGroup)findViewById(R.id.sexGroup_update);
        ageText = (EditText)findViewById(R.id.update_age);
        updateInfoBtn = (Button)findViewById(R.id.update_info_btn);

        nameText.setText(user.getName());
        ageText.setText(user.getAge()+"");
        phoneText.setText(user.getPhone());

        //更新用户信息
        updateInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkInfo()){ //检查用户信息是否填写有效
                    phone = phoneText.getText().toString();
                    name = nameText.getText().toString();
                    rb = (RadioButton)findViewById(sexRadioGroup.getCheckedRadioButtonId());
                    sex = rb.getText().toString();
                    age = Integer.parseInt(ageText.getText().toString());

                    //暂存修改后的用户信息
                    User tempUser = new User(user.getId(),phone,name,user.getPassword(),sex,age);
                    tempUser.setHead(user.getHead());
                    //原用户名长度-原用户名-新用户名
                    name = user.getName().length()+"-"+user.getName()+"-"+name;

                    HttpRequest.updateInfo(user.getId(),phone, name, age, sex, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            try {
                                String result = response.body().string();
                                Log.d("test","updateInfoBtn："+result);
                                if(result.equals(Constant.INFOLACK)||result.equals(Constant.FAILED)){
                                    view.post(()->Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show());
                                }else{
                                    view.post(()->Toast.makeText(getApplicationContext(), "信息更新成功", Toast.LENGTH_SHORT).show());
                                    //修改当前用户信息
                                    user = tempUser;
                                    //修改本地缓存的用户信息
                                    SharedPreferences.Editor editor = preferences.edit(); //用户信息存入SharedPreferences
                                    editor.putString("userinfo", new Gson().toJson(user));
                                    editor.apply();
                                    Log.d("test","更新用户："+new Gson().toJson(user));

                                    Intent intent = new Intent(getApplicationContext(), FragmentActivity.class);
                                    startActivity(intent);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 检查新填写的用户信息是否合法
     * @return
     */
    public boolean checkInfo(){
        if(TextUtils.isEmpty(phoneText.getText())
                ||TextUtils.isEmpty(nameText.getText())
                ||TextUtils.isEmpty(ageText.getText())){
            Toast.makeText(this,"请填写完整信息",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
