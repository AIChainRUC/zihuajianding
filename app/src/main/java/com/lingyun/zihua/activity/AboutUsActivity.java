package com.lingyun.zihua.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lingyun.zihua.R;
import com.lingyun.zihua.base.BaseActivity;

/**
 * AboutUs实现的关于我们的功能
 */
public class AboutUsActivity extends BaseActivity {
    //Toolbar相关
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);
        initToolbar();
    }
    //初始化导航栏
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_aboutUs);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}
