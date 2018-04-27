package com.lingyun_chain.zihua.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cjt2325.cameralibrary.JCameraView;
import com.cjt2325.cameralibrary.listener.ErrorListener;
import com.cjt2325.cameralibrary.listener.JCameraListener;
import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.util.FileUtil;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 证书使用
 */
public class SignCertificateActivity extends BaseActivity {
    //Toolbar相关
    private Toolbar toolbar;
    private Button sign_certi_btn;
    //private Button sign_using_btn;

    private String generatePublicKey = null;//公钥
    private String generatePrivateKey = null;//私钥
    private String generateCertificate = null;//证书
    //private TextView sign_certi_text;
    private String featureFace = "1111111";
    private final int GET_PERMISSION_REQUEST = 100; //权限申请自定义码
    private JCameraView jCameraView;
    private boolean granted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_certificate);
        //initToolbar();
        initView();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        //sign_certi_text = (TextView) findViewById(R.id.sign_certi_text);
//        sign_certi_btn = (Button) findViewById(R.id.sign_certi_btn);
//        sign_certi_btn.setOnClickListener(this);
        jCameraView = (JCameraView) findViewById(R.id.videoFace);
        if (sharedPreference != null) {
            generateCertificate = sharedPreference.getString("generateCertificate", "default");
        }
        jCameraView.setSaveVideoPath(FileUtil.getPath());
        jCameraView.setFeatures(JCameraView.BUTTON_STATE_ONLY_RECORDER);
        getPermissions();
        jCameraView.setJCameraLisenter(new JCameraListener() {

            @Override
            public void captureSuccess(Bitmap bitmap) {

            }

            @Override
            public void recordSuccess(String videoUrl, Bitmap firstFrame) {
                new AsyFaceVerTask(SignCertificateActivity.this, "AsyFaceVerTask", videoUrl, generateCertificate).execute();
//                Intent intent = new Intent();
//                intent.putExtra("videoPath", videoUrl);
//                setResult(RESULT_OK, intent);
//                finish();
            }
        });

        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                UiUtils.show("出现错误，请重试");
            }

            @Override
            public void AudioPermissionError() {

            }
        });
        //sign_using_btn = (Button) findViewById(R.id.sign_using_btn);
        //sign_using_btn.setOnClickListener(this);
//        if (sharedPreference != null) {
//            generatePublicKey = sharedPreference.getString("generatePublicKey", "default");
//            generatePrivateKey = sharedPreference.getString("generatePrivateKey", "default");
//            generateCertificate = sharedPreference.getString("generateCertificate", "default");
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
        //      }
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //具有权限
                granted = true;
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.CAMERA}, GET_PERMISSION_REQUEST);
                granted = false;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GET_PERMISSION_REQUEST) {
            int size = 0;
            if (grantResults.length >= 1) {
                int writeResult = grantResults[0];
                //读写内存权限
                boolean writeGranted = writeResult == PackageManager.PERMISSION_GRANTED;//读写内存权限
                if (!writeGranted) {
                    size++;
                }
                //录音权限
                int recordPermissionResult = grantResults[1];
                boolean recordPermissionGranted = recordPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!recordPermissionGranted) {
                    size++;
                }
                //相机权限
                int cameraPermissionResult = grantResults[2];
                boolean cameraPermissionGranted = cameraPermissionResult == PackageManager.PERMISSION_GRANTED;
                if (!cameraPermissionGranted) {
                    size++;
                }
                if (size == 0) {
                    granted = true;
                    jCameraView.onResume();
                } else {
                    Toast.makeText(this, "请到设置-权限管理中开启", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


//    private void initToolbar() {
//        toolbar = (Toolbar) findViewById(R.id.toolbar_sign_certificate);
//        setSupportActionBar(toolbar);
//        actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setHomeButtonEnabled(true);
//    }

    @Override
    protected void onStart() {
        super.onStart();

        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (granted) {
            jCameraView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        jCameraView.onPause();
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
                UiUtils.show("人脸验证成功");
                startActivity(new Intent(SignCertificateActivity.this, StoreCalligraphyActivity.class));
                //setResult(RESULT_OK);
                finish();
            } else {
                UiUtils.show("验证失败，请重试");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                //featureFace = jsonObject.optString("feature");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }
//    @Override
//    public void onClick(View v) {
//        if (v.getId() == R.id.sign_certi_btn) {
//            // takePictures();
//            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO}, new PermissionListener() {
//                @Override
//                public void onGranted() {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(SignCertificateActivity.this);
//                    builder.setTitle("温馨提示");
//                    builder.setMessage("为了防止其他人假冒您的身份，我们需要进行活体验证，请您录制包含眨眼动作的视频");
//                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
////                            Uri fileUri;
////                            if (Build.VERSION.SDK_INT >= 24) {
////                                fileUri = FileProvider.getUriForFile(SignCertificateActivity.this, "com.lingyun_chain.zihua.provider", getOutputMediaFile());
////                            } else {
////                                fileUri = Uri.fromFile(getOutputMediaFile());
////                            }
////                            //Uri.fromFile(getOutputMediaFile());
////                            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
////                            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
////                            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 3); //限制的录制时长 以秒为单位
////                            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
////                            startActivityForResult(intent, IntentConstants.RECORD_SYSTEM_VIDEO);
//                        }
//                    });
//                    builder.setCancelable(false);
//                    builder.create().show();
//                }
//
//                @Override
//                public void onDenied(List<String> deniedPermission) {
//                    dialog(SignCertificateActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
//                }
//            });
//
////        } else if (v.getId() == R.id.sign_using_btn) {
////           // new AsyUserFeatureTask(SignCertificateActivity.this, "AsyUserFeatureTask", generateCertificate).execute();
//////            setResult(RESULT_OK);
//////            finish();
////        }
//        }
//    }

//    private File getOutputMediaFile() {
//        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
//            Toast.makeText(this, "请检查SDCard！", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//
//        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
//                (Environment.DIRECTORY_DCIM), "MyCameraApp");
//        if (!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdirs();
//        }
//        // Create a media file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
//        return mediaFile;
//    }

    //打开相机拍照
//    private void takePictures() {
//        //执行拍照前，应该先判断SD卡是否存在
//        if (OSutil.isSdExist()) {
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (intent.resolveActivity(getPackageManager()) != null) {
//                String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
//                        .format(new Date()) + ".jpg";
//                File temp = new File(Environment.getExternalStorageDirectory() + "/Lingyun_chain", filename);
//                if (!temp.getParentFile().exists()) {
//                    temp.getParentFile().mkdir();
//                }
//                if (temp.exists())
//                    temp.delete();
//                photoUri = FileProvider7Util.getUriForFile(this, temp);
//                picPath = temp.getAbsolutePath();
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
//                startActivityForResult(intent, IntentConstants.SELECT_PIC_BY_TACK_PHOTO);
//            } else {
//                UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
//            }
//        }
//
//        //    @Override
////    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
////        if (resultCode == Activity.RESULT_OK) {
////            if (requestCode == SELECT_PIC_BY_TACK_PHOTO) {
////                String[] pojo = {MediaStore.Images.Media.DATA};
////                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
////                if (cursor != null) {
////                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
////                    cursor.moveToFirst();
////                    picPath = cursor.getString(columnIndex);
////                    if (Build.VERSION.SDK_INT < 14) {
////                        cursor.close();
////                    }
////                }
////                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
////                    new AsyFaceVerTask(SignCertificateActivity.this, "AsyCheckFaceTask", picPath).execute();
////                }
////            }
////        }
////    }
//    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == RESULT_OK) {
//            if (requestCode == IntentConstants.RECORD_SYSTEM_VIDEO) {
//                stopRecord();
//                if (mCamera != null) {
//                    mCamera.lock();
//                }
//                stopCamera();
//                currentVideoFilePath = data.getDataString();
//                LogUtils.d("currentVideoFilePath", currentVideoFilePath);
//                //new AsyDealVideo(SignCertificateActivity.this).execute();
//                //new AsyVideoTask(SignCertificateActivity.this, "AsyCheckFaceTask", currentVideoFilePath).execute();
//            } else if (requestCode == IntentConstants.GO_TO_KEY) {
//                //sign_certi_text.setVisibility(View.GONE);
//                //sign_using_btn.setClickable(true);
//            }
//        } else {
//            if (requestCode == IntentConstants.RECORD_SYSTEM_VIDEO) {
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


}