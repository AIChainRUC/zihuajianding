package com.lingyun.zihua.fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.lingyun.zihua.R;
import com.lingyun.zihua.activity.AboutUsActivity;
import com.lingyun.zihua.activity.AdvicesActivity;
import com.lingyun.zihua.activity.LoginActivity;
import com.lingyun.zihua.activity.UserHelp;
import com.lingyun.zihua.base.BaseFragement;

/**
 * 实现清除缓存，意见反馈，关于我们，帮助和退出登陆的功能
 */
public class MyFragement extends BaseFragement implements View.OnClickListener {
    private RelativeLayout tv_advice;
    private RelativeLayout contact_our;
    private RelativeLayout helpRelative;
    private Button logOut;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragement_my, null, false);
        initView(mView);

        return mView;
    }

    private void initView(View mView) {
        tv_advice=(RelativeLayout)mView.findViewById(R.id.tv_advice);
        tv_advice.setOnClickListener(this);
        contact_our=(RelativeLayout)mView.findViewById(R.id.contact_our);
        contact_our.setOnClickListener(this);
        helpRelative=(RelativeLayout)mView.findViewById(R.id.help);
        helpRelative.setOnClickListener(this);
        logOut=(Button)mView.findViewById(R.id.logOut);
        logOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_advice:
                startActivity(new Intent(getActivity(), AdvicesActivity.class));
                break;
            case R.id.contact_our:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(getActivity(), UserHelp.class));
                break;
            case R.id.logOut:
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
                default:
                    break;
        }
    }
}
