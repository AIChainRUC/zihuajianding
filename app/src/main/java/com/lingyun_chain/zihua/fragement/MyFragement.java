package com.lingyun_chain.zihua.fragement;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.activity.AboutUsActivity;
import com.lingyun_chain.zihua.activity.AdvicesActivity;
import com.lingyun_chain.zihua.activity.UserHelp;
import com.lingyun_chain.zihua.base.BaseFragement;
import com.lingyun_chain.zihua.util.FileUtil;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.UiUtils;

import java.io.File;

/**
 * 实现清除缓存，意见反馈，关于我们，帮助和退出登陆的功能
 */
public class MyFragement extends BaseFragement implements View.OnClickListener {
    private RelativeLayout tv_advice;
    private RelativeLayout contact_our;
    private RelativeLayout helpRelative;
    private RelativeLayout layout_catch;
    private TextView cahchSize;
    //private Button logOut;
    String fizeSize = "0B";
    private Boolean isSdExist;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragement_my, null, false);
        initView(mView);

        return mView;
    }

    private void initView(View mView) {
        tv_advice = (RelativeLayout) mView.findViewById(R.id.tv_advice);
        tv_advice.setOnClickListener(this);
        contact_our = (RelativeLayout) mView.findViewById(R.id.contact_our);
        contact_our.setOnClickListener(this);
        helpRelative = (RelativeLayout) mView.findViewById(R.id.help);
        helpRelative.setOnClickListener(this);
        //logOut = (Button) mView.findViewById(R.id.logOut);
        //logOut.setOnClickListener(this);
        layout_catch = (RelativeLayout) mView.findViewById(R.id.layout_cache);
        layout_catch.setOnClickListener(this);
        cahchSize = (TextView) mView.findViewById(R.id.cache_size);
        //判断SD卡是否存在
        isSdExist = OSutil.isSdExist();
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果sd卡存在
        if (isSdExist) {
            fizeSize = FileUtil.getAutoFileOrFilesSize(FileUtil.getPath());
        } else {
            fizeSize = FileUtil.getAutoFileOrFilesSize(FileUtil.Cache);
        }
        cahchSize.setText(fizeSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_advice:
                startActivity(new Intent(getActivity(), AdvicesActivity.class));
                break;
            case R.id.contact_our:
                startActivity(new Intent(getActivity(), AboutUsActivity.class));
                break;
            case R.id.help:
                startActivity(new Intent(getActivity(), UserHelp.class));
                break;
//            case R.id.logOut:
//                startActivity(new Intent(getActivity(), LoginActivity.class));
//                getActivity().finish();
            case R.id.layout_cache:
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setMessage("要清除缓存吗？");
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file;
                        if (isSdExist) {
                            file = new File(FileUtil.getPath());
                        } else {
                            file = new File(FileUtil.Cache);
                        }
                        FileUtil.RecursionDeleteFile(file);//递归删除文件
                        if (isSdExist) {
                            fizeSize = FileUtil.getAutoFileOrFilesSize(FileUtil.getPath());
                        } else {
                            fizeSize = FileUtil.getAutoFileOrFilesSize(FileUtil.Cache);
                        }
                        cahchSize.setText(fizeSize);
                        dialog.dismiss();
                    }
                });
                if (cahchSize.getText().equals("0B")) {
                    UiUtils.show("您现在没有缓存哦");
                } else {
                    dialog.show();
                }
                break;
            default:
                break;
        }
    }
}
