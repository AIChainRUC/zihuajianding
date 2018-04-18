package com.lingyun_chain.zihua.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;

/**
 * 用于实现用户登录的功能
 */
public class LoginActivity extends BaseActivity{
    String number;
    String password;
    TextView registerbutton;
    TextView forget;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final EditText phonenumber = (EditText) findViewById(R.id.phone);
        final EditText loginpassword = (EditText) findViewById(R.id.password);
        final Button loginbutton = (Button) findViewById(R.id.loginbutton);
        final ProgressBar pbLoading = (ProgressBar) findViewById(R.id.customProgressBar);
        registerbutton = (TextView) findViewById(R.id.registerbutton);
        forget = (TextView) findViewById(R.id.forget);
        //用户登录
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoading.setVisibility(View.VISIBLE);
                number = phonenumber.getText().toString();
                password = loginpassword.getText().toString();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //跳转到用户注册界面
        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        //跳转到重置密码界面
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}
