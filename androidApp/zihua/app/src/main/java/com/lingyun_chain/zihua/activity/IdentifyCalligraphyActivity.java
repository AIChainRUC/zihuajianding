package com.lingyun_chain.zihua.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.constants.IntentConstants;
import com.lingyun_chain.zihua.interfaceMy.PermissionListener;
import com.lingyun_chain.zihua.util.FileProvider7Util;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.lingyun_chain.zihua.constants.URLConstants.DownLoadURL;
import static com.lingyun_chain.zihua.constants.URLConstants.ServerURL;
import static com.lingyun_chain.zihua.constants.URLConstants.UploadPort;

/**
 * 实现字画鉴定功能
 */
public class IdentifyCalligraphyActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;

    private TextView identify_seal_text;//印章值
    private String generateSealFeature;

    private TextView identify_image_text;//图片
    protected String picPath = "default";

    private EditText identify_seal_keyValue;//字画键值
    private String sealKeyValue = "default";

    private EditText identify_seal_decribal;//字画描述
    private String sealDecribal = "default";

    private Button identify_seal_btn;

    private Boolean isHaveSeal = false;//印章是否上传

    private ImageView downloadImage;//下载的图片

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_calligraphy);
        initToolbar();
        initView();
    }

    private void initView() {
        identify_seal_text = (TextView) findViewById(R.id.identify_seal_text);
        identify_image_text = (TextView) findViewById(R.id.identify_image_text);
        identify_seal_text.setOnClickListener(this);
        identify_image_text.setOnClickListener(this);
        if (isHaveSeal) {
            identify_seal_text.setEnabled(false);
            identify_seal_text.setText("印章已上传");
        }
        identify_seal_keyValue = (EditText) findViewById(R.id.identify_seal_keyValue);
        //identify_seal_keyValue.setOnClickListener(this);
        identify_seal_decribal = (EditText) findViewById(R.id.identify_seal_decribal);
        //identify_seal_decribal.setOnClickListener(this);
        identify_seal_btn = (Button) findViewById(R.id.identify_seal_btn);
        identify_seal_btn.setOnClickListener(this);
        downloadImage = (ImageView) findViewById(R.id.downloadImage);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_Identify_call);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.identify_seal_text) {//上传印章
            if (isHaveSeal) {
                UiUtils.show("已上传，请不要重复提交");
            } else {
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        takePictures(IntentConstants.SELECT_PIC_BY_TACK_PHOTO_SEAL);//打开相机拍照
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        dialog(IdentifyCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                    }
                });
            }
        } else if (v.getId() == R.id.identify_seal_btn) {//字画鉴定
            sealKeyValue = identify_seal_keyValue.getText().toString().trim();
            sealDecribal = identify_seal_decribal.getText().toString().trim();
            if (!TextUtils.equals(sealKeyValue, "default")
                    && !TextUtils.equals(sealDecribal, "default")
                    && !TextUtils.equals(picPath, "default")) {
                new AsyRetrieveFeatureTask(IdentifyCalligraphyActivity.this, "AsyRetrieveFeatureTask", sealKeyValue).execute();
                //Glide.with(IdentifyCalligraphyActivity.this).load(ServerURL+UploadPort+DownLoadURL+sealKeyValue+".jpg").into(downloadImage);
            } else {
                UiUtils.show("请补充完整所有信息！！");
                //Glide.with(IdentifyCalligraphyActivity.this).load(ServerURL+UploadPort+DownLoadURL+sealKeyValue+".jpg").into(downloadImage);
            }
        } else if (v.getId() == R.id.identify_image_text) {//上传字画图片
            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void onGranted() {
                    takePictures(IntentConstants.SELECT_PIC_BY_TACK_PHOTO_IMAGE);//打开相机拍照
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    dialog(IdentifyCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                }
            });

        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentConstants.SELECT_PIC_BY_TACK_PHOTO_SEAL) {//印章
                //picPath = FileUtil.getPath() + "identifyPhoto" + "/seal.jpg";
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    UiUtils.show("印章已拍摄");
                    identify_seal_text.setText("印章已拍摄");
                    identify_seal_text.setEnabled(false);
                    identify_seal_text.setTextColor(R.color.colorAccent);
                    //new AsySaveTask(this, "StoreCalligSave", picPath).execute();
                    // new AsyRetrieveFeatureTask(IdentifyCalligraphyActivity.this, "AsyRetrieveFeatureTask", picPath).execute();

                    //                    File fileTemp = new File(picPath);
//                    File file;
//                    try {
//                        file = new Compressor(this).compressToFile(fileTemp);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            } else if (requestCode == IntentConstants.SELECT_PIC_BY_TACK_PHOTO_IMAGE) {//图片
                identify_image_text.setText("图片已拍摄");
                identify_image_text.setEnabled(false);
            } else {
                //错误提示
                UiUtils.show("拍照失败");
            }
        } else {
            UiUtils.show("拍照失败");
        }
    }


    //打开相机拍照
    private void takePictures(int TAG) {
        //执行拍照前，应该先判断SD卡是否存在
        if (OSutil.isSdExist()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (intent.resolveActivity(getPackageManager()) != null) {
                String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                        .format(new Date()) + ".jpg";
                File temp = new File(Environment.getExternalStorageDirectory() + "/Lingyun_chain", filename);
                if (!temp.getParentFile().exists()) {
                    temp.getParentFile().mkdir();
                }
                if (temp.exists())
                    temp.delete();
                photoUri = FileProvider7Util.getUriForFile(this, temp);
                if (TAG == IntentConstants.SELECT_PIC_BY_TACK_PHOTO_SEAL) {
                    picPath = temp.getAbsolutePath();//印章才需要保存
                }
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
                startActivityForResult(intent, TAG);
            }
        } else {
            UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
        }
    }

    public class AsyRetrieveFeatureTask extends BaseAsyTask {//根据键值获取字画特征值
        private String status = "-1";

        public AsyRetrieveFeatureTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
            } else if (TextUtils.equals(s, "200")) {
                new AsyHashTask(IdentifyCalligraphyActivity.this, "AsyHashTask", picPath, generateSealFeature).execute();//字画鉴定
                //new AsyHashTask(IdentifyCalligraphyActivity.this, "AsyHashTask", picPath, generateSealFeature).execute();
            } else {
                UiUtils.show("字画键值输入有误");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                generateSealFeature = jsonObject.optString("feature");
                //LogUtils.d("status", status);
                //LogUtils.d("status", generateFaceFeature);
            } catch (IOException e) {
                e.printStackTrace();
                //LogUtils.d("hjs",e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }

    public class AsyHashTask extends BaseAsyTask {//字画鉴定
        private String status = "-1";
        private String pass = "No";

        public AsyHashTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
            }
//            } else if (TextUtils.equals(s, "200")) {
//
//                UiUtils.show("恭喜您，字画鉴定成功！！！");
//                finish();
//            }
             else {
                if(TextUtils.equals(pass, "Yes")){
                    UiUtils.show("恭喜您，字画鉴定成功！！！");
                    finish();
                }else {
                    UiUtils.show("对不起，字画鉴定失败！！！请您核对后重新提交");
                    identify_seal_text.setText("请重新上传照片");
                    identify_seal_text.setEnabled(true);
                    identify_seal_keyValue.setText("");
                    identify_seal_decribal.setText("");
                }
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                pass = jsonObject.optString("pass");
            } catch (IOException e) {
                e.printStackTrace();
                //LogUtils.d("hjs",e.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }
}
