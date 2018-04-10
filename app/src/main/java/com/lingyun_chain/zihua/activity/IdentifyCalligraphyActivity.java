package com.lingyun_chain.zihua.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.interfaceMy.PermissionListener;
import com.lingyun_chain.zihua.util.MD5Util;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

/**
 * 实现字画鉴定功能
 */
public class IdentifyCalligraphyActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;
    private TextView identify_seal_text;
    private EditText identify_seal_keyValue;
    private EditText identify_seal_decribal;
    private Button identify_seal_btn;
    private Boolean isHaveSeal = false;
    private String sealKeyValue = "default";
    private String sealDecribal = "default";
    public static final int SELECT_PIC_BY_TACK_PHOTO_SEAL = 5;
    private String generateSealFeature;
    protected String picPath = "default";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identify_calligraphy);
        initToolbar();
        initView();
    }

    private void initView() {
        identify_seal_text = (TextView) findViewById(R.id.identify_seal_text);
        identify_seal_text.setOnClickListener(this);
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
        if (v.getId() == R.id.identify_seal_text) {
            if (isHaveSeal) {
                UiUtils.show("已上传，请不要重复提交");
            } else {
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        takePictures(SELECT_PIC_BY_TACK_PHOTO_SEAL);//打开相机拍照
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        dialog(IdentifyCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                    }
                });
            }
        } else if (v.getId() == R.id.identify_seal_btn) {
            sealKeyValue = identify_seal_keyValue.getText().toString().trim();
            sealDecribal = identify_seal_decribal.getText().toString().trim();
            if (!TextUtils.equals(sealKeyValue, "default")
                    && !TextUtils.equals(sealDecribal, "default")
                    && !TextUtils.equals(picPath, "default")) {
                new AsyRetrieveFeatureTask(IdentifyCalligraphyActivity.this, "AsyRetrieveFeatureTask", sealKeyValue).execute();
            } else {
                UiUtils.show("请补充完整所有信息！！");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PIC_BY_TACK_PHOTO_SEAL) {
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
                    UiUtils.show("拍照成功");
                    identify_seal_text.setEnabled(false);
                    identify_seal_text.setText("印章已上传");
                    //                    File fileTemp = new File(picPath);
//                    File file;
//                    try {
//                        file = new Compressor(this).compressToFile(fileTemp);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            } else {
                //错误提示
                UiUtils.show("拍照失败");
            }
        }
    }

    //打开相机拍照
    private void takePictures(int TAG) {
        //执行拍照前，应该先判断SD卡是否存在
        if (OSutil.isSdExist()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, TAG);
        } else {
            UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
        }
    }

    public class AsyRetrieveFeatureTask extends BaseAsyTask {
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
                UiUtils.show("字画键值已成功识别");
                new AsyCheckTask(IdentifyCalligraphyActivity.this, "AsyCheckTask", picPath, generateSealFeature).execute();
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

    public class AsyCheckTask extends BaseAsyTask {
        private String status = "-1";

        public AsyCheckTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
            } else if (TextUtils.equals(s, "200")) {
                UiUtils.show("恭喜您，字画鉴定成功！！！");
                finish();
            } else {
                UiUtils.show("对不起，字画鉴定失败！！！请您核对后重新提交");
                identify_seal_text.setText("请重新上传照片");
                identify_seal_text.setEnabled(true);
                identify_seal_keyValue.setText("");
                identify_seal_decribal.setText("");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
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
}
