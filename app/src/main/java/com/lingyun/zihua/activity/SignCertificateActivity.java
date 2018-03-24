package com.lingyun.zihua.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lingyun.zihua.R;
import com.lingyun.zihua.base.BaseActivity;

/**
 * 证书使用
 */
public class SignCertificateActivity extends BaseActivity {
    //Toolbar相关
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_certificate);
        initToolbar();
    }
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_sign_certificate);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}
