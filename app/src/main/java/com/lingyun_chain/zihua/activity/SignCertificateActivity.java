package com.lingyun_chain.zihua.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lingyun_chain.zihua.BuildConfig;
import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.interfaceMy.PermissionListener;
import com.lingyun_chain.zihua.util.MathUtil;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.UiUtils;
import com.lingyun_chain.zihua.util.VideoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.lingyun_chain.zihua.activity.CustomVideoActivity.getSDPath;

/**
 * 证书使用
 */
public class SignCertificateActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;
    private Button sign_certi_btn;
    private Button sign_using_btn;
    public static final int RECORD_SYSTEM_VIDEO = 1;
    public static final int SELECT_PIC_BY_TACK_PHOTO = 3;
    public static final int GO_TO_KEY = 2;
    private String generatePublicKey = null;//公钥
    private String generatePrivateKey = null;//私钥
    private String generateCertificate = null;//证书
    //private TextView sign_certi_text;
    private String featureFace = "1111111";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_certificate);
        initToolbar();
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        //sign_certi_text = (TextView) findViewById(R.id.sign_certi_text);
        sign_certi_btn = (Button) findViewById(R.id.sign_certi_btn);
        sign_certi_btn.setOnClickListener(this);
        sign_using_btn = (Button) findViewById(R.id.sign_using_btn);
        sign_using_btn.setOnClickListener(this);
        if (sharedPreference != null) {
            generatePublicKey = sharedPreference.getString("generatePublicKey", "default");
            generatePrivateKey = sharedPreference.getString("generatePrivateKey", "default");
            generateCertificate = sharedPreference.getString("generateCertificate", "default");
//            if (TextUtils.equals(generatePublicKey, "default")
//                    && TextUtils.equals(generatePrivateKey, "default")
//                    && TextUtils.equals(generateCertificate, "default")) {
            //sign_certi_text.setText("未找到私钥文件，点我去生成");
            // sign_certi_text.setVisibility(View.VISIBLE);
       //     sign_certi_text.setTextColor(R.color.main_hue);
            //sign_using_btn.setClickable(false);
//                sign_certi_text.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        startActivityForResult(new Intent(SignCertificateActivity.this, GenerateCertificateActivity.class), GO_TO_KEY);
//                    }
//                });
            //       }
        }
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
            takePictures();
//            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new PermissionListener() {
//                @Override
//                public void onGranted() {
//
////                    Intent intent = new Intent(SignCertificateActivity.this, CustomVideoActivity.class);
////                    startActivityForResult(intent, RECORD_SYSTEM_VIDEO);
//                }
//
//                @Override
//                public void onDenied(List<String> deniedPermission) {
//                    dialog(SignCertificateActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
//                }
//            });

        } else if (v.getId() == R.id.sign_using_btn) {
            new AsyUserFeatureTask(SignCertificateActivity.this, "AsyUserFeatureTask", generateCertificate).execute();
//            setResult(RESULT_OK);
//            finish();
        }
    }

    //打开相机拍照
    private void takePictures() {
        //执行拍照前，应该先判断SD卡是否存在
        if (OSutil.isSdExist()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
                String[] pojo = {MediaStore.Images.Media.DATA};
                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
                if (cursor != null) {
                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
                    cursor.moveToFirst();
                    picPath = cursor.getString(columnIndex);
                    if (Build.VERSION.SDK_INT < 14) {
                        cursor.close();
                    }
                }
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    new AsyFaceVerTask(SignCertificateActivity.this, "AsyCheckFaceTask", picPath).execute();
                }
            }
        }
    }
    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == RECORD_SYSTEM_VIDEO) {
//                stopRecord();
//                if (mCamera != null) {
//                    mCamera.lock();
//                }
//                stopCamera();
//                currentVideoFilePath = data.getStringExtra("videoPath");
//                //new AsyDealVideo(SignCertificateActivity.this).execute();
//                new AsyVideoTask(SignCertificateActivity.this, "AsyCheckFaceTask", currentVideoFilePath).execute();
//            } else if (requestCode == GO_TO_KEY) {
//                sign_certi_text.setVisibility(View.GONE);
//                sign_using_btn.setClickable(true);
//            }
//        } else {
//            if (requestCode == RECORD_SYSTEM_VIDEO) {
//                UiUtils.show("视频录制失败");
//                stopRecord();
//                if (mCamera != null) {
//                    mCamera.lock();
//                }
//                stopCamera();
//
//            } else {
//                UiUtils.show("生成证书失败");
//            }
//        }
//    }

    public class AsyUserFeatureTask extends BaseAsyTask {//使用证书
        private String status = "-1";
        private String featureFace2 = "default";
        private double distance;
        public AsyUserFeatureTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                featureFace2 = jsonObject.optString("feature");
                distance=MathUtil.sim_distance(Double.parseDouble(featureFace2),Double.parseDouble(featureFace));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(TextUtils.equals(s,"-1")){
                UiUtils.show("网络错误，请重试");
            }else if(TextUtils.equals(s,"200")){
                if(distance<=0.6){
                    UiUtils.show("人脸验证成功");
                    setResult(RESULT_OK);
                    finish();
                }else {
                    UiUtils.show("您好像不是本人哦，请重新拍照");
                }
            }else {
                UiUtils.show("您好像不是本人哦，请重新拍照");
            }
//            setResult(RESULT_OK);
//            finish();
        }
    }

    public class AsyDealVideo extends AsyncTask<String, String, String> {
        private ProgressDialog pDialog = null;
        private Context context;

        public AsyDealVideo(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("视频正在处理中");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //pDialog.dismiss();

        }

        @Override
        protected String doInBackground(String... strings) {
            if ((saveVideoPath.equals(""))) {
                saveVideoPath = currentVideoFilePath;
                String[] str = new String[]{saveVideoPath, currentVideoFilePath};
                //合成视频
                try {
                    VideoUtil.appendVideo(SignCertificateActivity.this, getSDPath(SignCertificateActivity.this) + "append.mp4", str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                File reName = new File(saveVideoPath);
//                File f = new File(getSDPath(SignCertificateActivity.this) + "append.mp4");
//                f.renameTo(reName);//将合成的视频复制过来
//                if (reName.exists()) {
//                    f.delete();
//                    new File(currentVideoFilePath).delete();
//                }
            }
            return "ok";
        }
    }

    public class AsyFaceVerTask extends BaseAsyTask {
        private String status = "-1";

        public AsyFaceVerTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
            } else if (TextUtils.equals(s, "200")) {
                sign_certi_btn.setText("视频上传成功");
                sign_certi_btn.setBackgroundColor(R.color.text_color);
                sign_certi_btn.setClickable(false);
            } else {
                UiUtils.show("视频上传失败，请重试");
                sign_certi_btn.setText("视频上传失败");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                featureFace = jsonObject.optString("feature");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }
}
