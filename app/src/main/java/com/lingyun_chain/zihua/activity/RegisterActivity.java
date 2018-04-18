package com.lingyun_chain.zihua.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;

/**
 * 实现用户的注册
 */
public class RegisterActivity extends BaseActivity         {
    Button virifyCode;
    private TimeCount time;
    private EditText etusername;
    private EditText etpassword;
    private EditText etpassword2;
    private ProgressBar pbLoading;
    private Button goLogin;
    private Button registerButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        time=new TimeCount(60000,1000);
        etusername=(EditText)findViewById(R.id.et_userName);
        etpassword=(EditText)findViewById(R.id.et_password);
        etpassword2=(EditText)findViewById(R.id.et_password2);
        virifyCode = (Button) findViewById(R.id.verify);
        goLogin=(Button)findViewById(R.id.goLogin);
        registerButton=(Button)findViewById(R.id.register);
        //发送验证码
        virifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = etusername.getText().toString();

            }
        });
        //已经注册过账号直接登陆
        goLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        //注册
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoading.setVisibility(View.VISIBLE);
            }
        });
    }

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            virifyCode.setBackgroundColor(Color.parseColor("#B0A4AC"));
            virifyCode.setClickable(false);
            virifyCode.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        public void onFinish() {
            virifyCode.setText("重新获取验证码");
            virifyCode.setClickable(true);
            virifyCode.setBackgroundColor(Color.parseColor("#B0A4AC"));
        }
    }
}

