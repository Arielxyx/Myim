package com.example.imclient.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Intent;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.imclient.Fragment.HomePageFragment;
import com.example.imclient.Fragment.NewsFragment;
import com.example.imclient.Fragment.OnlineListFragment;
import com.example.imclient.Fragment.PostListFragment;
import com.example.imclient.R;
import com.example.imclient.Utils.ExitApplication;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import static com.example.imclient.Activity.MainActivity.head;

/**
 * 底层调用Fragment的Activity
 */
public class FragmentActivity extends AppCompatActivity {

    private BottomBar bottomBar;
    private FragmentTransaction transaction;
    private OnlineListFragment onlineListFragment;
    private HomePageFragment homePageFragment;
    private NewsFragment newsFragment;

    private Button toPublishBtn;
    private PostListFragment postFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);
        ExitApplication.getInstance().addActivity(this);

        head = (TextView) findViewById(R.id.head);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        toPublishBtn = (Button) findViewById(R.id.to_publish_btn);
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();

        toPublishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComponentName componentname = new ComponentName(FragmentActivity.this, "com.example.imclient.Activity.PublishAboutActivity");
                Intent intent = new Intent();
                intent.setComponent(componentname);
                startActivity(intent);
            }
        });
    }

    /**
     * 选中的tab的icon+title的颜色是  colorPrimary
     */
    private void initViews() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        //更新说说
        if(bundle!=null && bundle.getString("fragmentActivity").equals("tab4")){
            bottomBar.selectTabWithId(R.id.tab3);
        }
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(int tabId) {
                switch (tabId) {
                    case R.id.tab1:
                        if (onlineListFragment == null) {
                            onlineListFragment = new OnlineListFragment();
                        }
                        head.setText("在线用户");
                        setFragment(onlineListFragment);
                        break;
                    case R.id.tab2:
                        if (homePageFragment == null) {
                            homePageFragment = new HomePageFragment();
                        }
                        head.setText("主 页");
                        setFragment(homePageFragment);
                        break;
                    case R.id.tab3:
                        if (postFragment == null) {
                            postFragment = new PostListFragment();
                        }
                        head.setText("好友动态");
                        setFragment(postFragment);
//                        if (newsFragment == null) {
//                            newsFragment = new NewsFragment();
//                        }
//                        head.setText("科大快讯");
//                        setFragment(newsFragment);
//                        break;
//                    case R.id.tab4:
//                        if (postFragment == null) {
//                            postFragment = new PostListFragment();
//                        }
//                        head.setText("好友动态");
//                        setFragment(postFragment);
                }
            }
        });


        //当前的tab是tab1，而你又点击了tab1，会调用这个方法
        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.tab1:
                        if (onlineListFragment == null) {
                            onlineListFragment = new OnlineListFragment();
                        }
                        head.setText("在线用户");
                        setFragment(onlineListFragment);
                        break;
                    case R.id.tab2:
                        if (homePageFragment == null) {
                            homePageFragment = new HomePageFragment();
                        }
                        head.setText("主 页");
                        setFragment(homePageFragment);
                        break;
                    case R.id.tab3:
                        if (postFragment == null) {
                            postFragment = new PostListFragment();
                        }
                        head.setText("好友动态");
                        setFragment(postFragment);
//                        if (newsFragment == null) {
//                            newsFragment = new NewsFragment();
//                        }
//                        head.setText("科大快讯");
//                        setFragment(newsFragment);
//                        break;
//                    case R.id.tab4:
//                        if (postFragment == null) {
//                            postFragment = new PostListFragment();
//                        }
//                        head.setText("好友动态");
//                        setFragment(postFragment);
                }
            }
        });
    }

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

    /**
     * 第一种方法：打击弹出对话框，提示是否退出，可以自己设置提示语言，以及图片
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                ExitApplication.getInstance().exit(getApplicationContext());
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
