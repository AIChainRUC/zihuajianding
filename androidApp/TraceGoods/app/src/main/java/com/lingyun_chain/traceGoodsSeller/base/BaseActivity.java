package com.lingyun_chain.traceGoodsSeller.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Chronometer;

import com.lingyun_chain.traceGoodsSeller.interfaceMy.PermissionListener;
import com.lingyun_chain.traceGoodsSeller.other.ActivityCollector;
import com.lingyun_chain.traceGoodsSeller.receiver.NetWorkChangerReceiver;
import com.lingyun_chain.traceGoodsSeller.util.ToasUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

/**
 * 基类
 * 包括顶部导航栏和运行时权限的管理
 * Activity的统一管理
 */
public class BaseActivity extends AppCompatActivity {
    public ActionBar actionBar;//标题栏

    //权限监听回调器
    private static PermissionListener mPermissionListener;

    protected NetWorkChangerReceiver mReceiver;//监听网络的变化

    protected OkHttpClient mOkHttpClient;//用于上传图片等

    protected IntentFilter mFilter;

    protected SharedPreferences sharedPreference;//存储数据到本地
    protected SharedPreferences.Editor editor;

    //图片对应Uri
    protected Uri photoUri;
    //图片文件路径
    protected String picPath;
    // 存储文件
    protected File mVecordFile;

    protected Camera mCamera;
    protected MediaRecorder mediaRecorder;

    protected String currentVideoFilePath;//视频的路径

    protected String saveVideoPath = "";

    protected Chronometer mRecordTime;
    protected long mPauseTime = 0;           //录制暂停时间间隔
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");//实例化
        mOkHttpClient = new OkHttpClient();
        mReceiver = new NetWorkChangerReceiver();
        mFilter = new IntentFilter();
        mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        ActivityCollector.addActivity(this);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreference.edit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    //动态获取权限所需
    public void dialog(final Activity activity, String info) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("提示");
        builder.setMessage(info);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                Uri packageURI = Uri.parse("package:" + activity.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                ToasUtils.showToast(activity, "您没有给予该权限，将不能该功能，" +
                        "如您想要使用该功能，可自行去设置中心开启");
            }
        });
        builder.create().show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
//    /**
//     * 释放摄像头资源
//     *
//     * @author liuzhongjun
//     * @date 2016-2-5
//     */
//    protected void stopCamera() {
//        if (mCamera != null) {
//            mCamera.setPreviewCallback(null);
//            mCamera.stopPreview();
//            mCamera.release();
//            mCamera = null;
//        }
//    }
//    /**
//     * 停止录制视频
//     */
//    public void stopRecord() {
//        if (mediaRecorder != null) {
//            // 设置后不会崩
//            mediaRecorder.setOnErrorListener(null);
//            mediaRecorder.setPreviewDisplay(null);
//            //停止录制
//            mediaRecorder.stop();
//            mediaRecorder.reset();
//            //释放资源
//            mediaRecorder.release();
//            mediaRecorder = null;
//
//            mRecordTime.stop();
///*            //设置开始按钮可点击，停止按钮不可点击
//            mRecordControl.setEnabled(true);
//           // mPauseRecord.setEnabled(false);*/
//            //isRecording = false;
//        }
//    }

    //处理权限被授予或者拒绝后，需要进行的操作
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<String> deniedPermission;
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    deniedPermission = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermission.add(permission);
                        }
                    }
                    if (deniedPermission.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        if (mPermissionListener != null)
                            mPermissionListener.onDenied(deniedPermission);
                    }
                }
                break;
            default:
                break;
        }
    }

    public static void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        //取出顶部Activity
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) {
            return;
        }
        mPermissionListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            //没有该权限
            if (ContextCompat.checkSelfPermission(topActivity, permission) !=
                    PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        //需要权限则请求授予权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), 1);
        } else {
            mPermissionListener.onGranted();
        }
    }
}
