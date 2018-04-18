package com.lingyun_chain.zihua.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.util.UiUtils;

/**
 * 实现忘记密码重置的功能
 */
public class ForgetPasswordActivity extends BaseActivity {
    private TimeCount time;
    Button verify9;
    String telnum;
    String verifycode8;
    String newpassword;
    EditText et_verifycode8;
    EditText et_newpassword;
    EditText et_telnum;
    ProgressBar pbLoading;
    Button login5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        time = new TimeCount(60000, 1000);
        et_telnum = (EditText) findViewById(R.id.telnum);
        et_verifycode8 = (EditText) findViewById(R.id.verifycode8);
        et_newpassword = (EditText) findViewById(R.id.newpassword);
        pbLoading = (ProgressBar) findViewById(R.id.customProgressBar);
        login5=(Button)findViewById(R.id.login5);
        verify9 = (Button) findViewById(R.id.verify9);
        verify9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                telnum = et_telnum.getText().toString();
            }
        });
        login5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLoading.setVisibility(View.VISIBLE);
                verifycode8 = et_verifycode8.getText().toString();
                newpassword = et_newpassword.getText().toString();
                if (newpassword.equals("")) {
                    UiUtils.show("重置密码不能为空");
                    return;
                }
                if(newpassword.length()<6) {
                    UiUtils.show("密码至少为6位");
                    pbLoading.setVisibility(View.INVISIBLE);
                    return;
                }
            }
        });
    }
    /**
     * 实现倒计时的功能
     * CountDownTimer (long millisInFuture, long countDownInterval)
     *参数1，设置倒计时的总时间（毫秒）
     *参数2，设置每次减去多少毫秒
     * onTick()方法是定期间隔回调的方法
     * onFinish()就是结束时回调的方法
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        public void onTick(long millisUntilFinished) {
            verify9.setBackgroundColor(Color.parseColor("#B0A4AC"));
            verify9.setClickable(false);
            verify9.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        public void onFinish() {
            verify9.setText("重新获取验证码");
            verify9.setClickable(true);
            verify9.setBackgroundColor(Color.parseColor("#B0A4AC"));
        }
    }
}
