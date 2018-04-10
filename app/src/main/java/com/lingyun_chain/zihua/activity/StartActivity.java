package com.lingyun_chain.zihua.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * 开始界面
 */
public class StartActivity extends Activity {
    private TextView startText;
    private int countTime = 3;
    private Animation mAnimation;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        //CrashReport.initCrashReport(getApplicationContext(), "8d5b7b67b2", false);
        Bugly.init(getApplicationContext(), "8d5b7b67b2", false);
        startText=(TextView)findViewById(R.id.start_text);
        //加载一个动画
        mAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_text);
        //发送一个value为0的空消息，并且延时1秒
        handler.sendEmptyMessageDelayed(0, 1000);
    }
    private Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                startText.setText(getCount() + "");
                handler.sendEmptyMessageDelayed(0, 1000);
                mAnimation.reset();
                startText.startAnimation(mAnimation);
            }
        }
    };

    private int getCount() {
        countTime--;
        //当count等于0时，进入主界面
        if (countTime == 0) {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return countTime;
    }
}
