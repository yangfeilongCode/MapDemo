package com.feicuiedu.treasure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.feicuiedu.treasure.commons.ActivityUtils;
import com.feicuiedu.treasure.treasure.home.HomeActivity;
import com.feicuiedu.treasure.user.UserPrefs;
import com.feicuiedu.treasure.user.login.LoginActivity;
import com.feicuiedu.treasure.user.register.RegisterActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 初次进入界面
 */
public class MainActivity extends AppCompatActivity {

    private ActivityUtils activityUtils; //activity工具类

    public static final String ACTION_ENTER_HOME = "action.enter.home";

    // 广播接收器(当登陆和注册成功后，将发送出广播)
    // 接收到后，关闭当前页面
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_main);

        // 判断是否登录过
        SharedPreferences preferences = getSharedPreferences("user_info", MODE_PRIVATE);
        if (preferences != null) {
            if (preferences.getInt("key_tokenid", 0)== UserPrefs.getInstance().getTokenid()) {
                activityUtils.startActivity(HomeActivity.class);
                finish();
            }
        }

        ButterKnife.bind(this);
        // 注册本地广播接收器
        IntentFilter intentFilter = new IntentFilter(ACTION_ENTER_HOME);
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);
    }

    @OnClick({R.id.btn_Login, R.id.btn_Register})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.btn_Login:  //登录按钮
                activityUtils.startActivity(LoginActivity.class);
                break;
            case R.id.btn_Register: //注册
                activityUtils.startActivity(RegisterActivity.class);
                break;
        }
    }
}