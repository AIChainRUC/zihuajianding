package com.lingyun_chain.traceGoodsSeller.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.lingyun_chain.traceGoodsSeller.R;
import com.lingyun_chain.traceGoodsSeller.base.BaseActivity;

/**
 * 用户帮助功能
 */
public class UserHelp extends BaseActivity {
    //Toolbar相关
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_help);
        initToolbar();
    }
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_userHelp);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }
}
