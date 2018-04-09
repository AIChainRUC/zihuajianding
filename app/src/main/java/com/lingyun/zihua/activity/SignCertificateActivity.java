package com.lingyun.zihua.activity;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lingyun.zihua.BuildConfig;
import com.lingyun.zihua.R;
import com.lingyun.zihua.base.BaseActivity;
import com.lingyun.zihua.interfaceMy.PermissionListener;
import com.lingyun.zihua.util.UiUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 证书使用
 */
public class SignCertificateActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;
    private Button sign_certi_btn;
    private Button sign_using_btn;
    public static final int RECORD_SYSTEM_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_certificate);
        initToolbar();
        initView();
    }

    private void initView() {
        sign_certi_btn = (Button) findViewById(R.id.sign_certi_btn);
        sign_certi_btn.setOnClickListener(this);
        sign_using_btn = (Button) findViewById(R.id.sign_using_btn);
        sign_using_btn.setOnClickListener(this);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_sign_certificate);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_certi_btn) {
            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO}, new PermissionListener() {
                @Override
                public void onGranted() {
                    Intent intent = new Intent(SignCertificateActivity.this, CustomVideoActivity.class);
                    startActivityForResult(intent, RECORD_SYSTEM_VIDEO);
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    dialog(SignCertificateActivity.this, "录制视频需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                }
            });

        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RECORD_SYSTEM_VIDEO) {

            }
        } else {
            UiUtils.show("视频录制失败");
        }
    }
}
