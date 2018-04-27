package com.lingyun_chain.zihua.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.util.UiUtils;

/**
 * AdvicesActivity实现用户反馈的功能
 */
public class AdvicesActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;
    //用户提交建议和联系方式
    private EditText edit_advice, edit_contact;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advices);
        initToolbar();
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        edit_advice = (EditText) findViewById(R.id.edit_advice);
        edit_contact = (EditText) findViewById(R.id.edit_contact);
        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_submit.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_advices);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        String advice = edit_advice.getText().toString().trim();
        String contact = edit_contact.getText().toString().trim();
        if (advice.length() > 5) {
        } else {
            UiUtils.show("发烦您描述的再清楚一点哦");
            return;
        }
        if (contact.length() > 5) {
        } else {
            UiUtils.show("请输入您的联系方式");
            return;
        }
        UiUtils.show("提交成功，感谢您的回复");
        edit_advice.setText("");
        edit_contact.setText("");
        finish();
    }
}
