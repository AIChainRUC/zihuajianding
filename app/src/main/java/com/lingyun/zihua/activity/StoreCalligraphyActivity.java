package com.lingyun.zihua.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lingyun.zihua.R;
import com.lingyun.zihua.base.BaseActivity;
import com.lingyun.zihua.base.BaseAsyTask;
import com.lingyun.zihua.interfaceMy.PermissionListener;
import com.lingyun.zihua.util.MD5Util;
import com.lingyun.zihua.util.OSutil;
import com.lingyun.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import id.zelory.compressor.Compressor;

import static com.lingyun.zihua.activity.GenerateCertificateActivity.SELECT_PIC_BY_TACK_PHOTO;

/**
 * 存链功能
 */
public class StoreCalligraphyActivity extends BaseActivity implements View.OnClickListener {
    private TextView store_text_key;
    private EditText store_workName_edt;
    private EditText store_workSize_edt;
    private EditText creationYear_edt;
    private EditText classificationWork_edt;
    private EditText materialWork_edt;
    private EditText subjectWork_edt;
    private Button storeSubmit_btn;
    private TextView store_seal_submit;
    private TextView store_image_submit;
    //Toolbar相关
    private Toolbar toolbar;
    private String generatePublicKey = null;//公钥
    private String generatePrivateKey = null;//公钥
    private String store_workName = null;
    private String store_workSize = null;
    private String creationYear = null;
    private String classificationWork = null;
    private String materialWork = null;
    private String subjectWork = null;
    private String desc = "default";//书画基本信息
    private String delcare = "default";//制式声明
    private String featureSeal = "default";//印章的特征值
    private String picHash = "default";//画全图的哈希值
    private String sig_r = "default"; //ecbsa签名_r
    private String sig_s = "default";//ecbsa签名_s
    public static final int SELECT_PIC_BY_TACK_PHOTO_IMAGE = 3;
    public static final int SELECT_PIC_BY_TACK_PHOTO_SEAL = 4;
    public static final int GO_TO_KEY = 5;
    private String generateSealFeature;//印章特征
    private TextView store_assetId;
    private TextView assetId_help;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_calligraphy);
        initToolbar();
        inintView();
    }

    private void inintView() {
        store_text_key = (TextView) findViewById(R.id.store_text_key);
        if (sharedPreference != null) {
            generatePublicKey = sharedPreference.getString("generatePublicKey", "default");
            generatePrivateKey = sharedPreference.getString("generatePrivateKey", "default");
            if (!TextUtils.equals(generatePublicKey, "default") && !TextUtils.equals(generatePrivateKey, "default")) {
                store_text_key.setText("密钥文件已上传");
                store_text_key.setEnabled(false);
            }
        }
        store_text_key.setOnClickListener(this);
        store_workName_edt = (EditText) findViewById(R.id.store_workName_edt);
        store_workName_edt.setOnClickListener(this);
        store_workSize_edt = (EditText) findViewById(R.id.store_workSize_edt);
        store_workSize_edt.setOnClickListener(this);
        creationYear_edt = (EditText) findViewById(R.id.creationYear_edt);
        creationYear_edt.setOnClickListener(this);
        classificationWork_edt = (EditText) findViewById(R.id.classificationWork_edt);
        classificationWork_edt.setOnClickListener(this);
        materialWork_edt = (EditText) findViewById(R.id.materialWork_edt);
        materialWork_edt.setOnClickListener(this);
        subjectWork_edt = (EditText) findViewById(R.id.subjectWork_edt);
        subjectWork_edt.setOnClickListener(this);
        storeSubmit_btn = (Button) findViewById(R.id.store_submit);
        storeSubmit_btn.setOnClickListener(this);
        store_seal_submit = (TextView) findViewById(R.id.store_seal_submit);
        store_seal_submit.setOnClickListener(this);
        store_image_submit = (TextView) findViewById(R.id.store_image_submit);
        store_image_submit.setOnClickListener(this);
        store_assetId = (TextView) findViewById(R.id.store_assetId);
        assetId_help = (TextView) findViewById(R.id.assetId_help);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_store_calligraphy);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.store_text_key:
                startActivityForResult(new Intent(StoreCalligraphyActivity.this, GenerateCertificateActivity.class),GO_TO_KEY);
                break;
            case R.id.store_submit:
                store_workName = store_workName_edt.getText().toString().trim();
                store_workSize = subjectWork_edt.getText().toString().trim();
                creationYear = creationYear_edt.getText().toString().trim();
                classificationWork = classificationWork_edt.getText().toString().trim();
                materialWork = materialWork_edt.getText().toString().trim();
                subjectWork = subjectWork_edt.getText().toString().trim();
                if (!TextUtils.equals(generatePublicKey, "default")
                        && !TextUtils.equals(generatePrivateKey, "default")
                        && !store_workName.isEmpty()
                        && !store_workSize.isEmpty()
                        && !creationYear.isEmpty()
                        && !classificationWork.isEmpty()
                        && !materialWork.isEmpty()
                        && !subjectWork.isEmpty()
                        && !TextUtils.equals(featureSeal, "default")
                        && !TextUtils.equals(picHash, "default")) {
                    desc = store_workName + " " + store_workSize + " " + " " + creationYear + " " + classificationWork + " " + materialWork + " " + subjectWork;
                    //delcare = "default";
                    //featureSeal = "default";
                    //picHash = "default";
                    //sig_r = "default";
                    //sig_s = "default";
                    new AsyCreateAsset(StoreCalligraphyActivity.this,
                            "StoreCalligraphy",
                            desc, generatePublicKey, delcare, featureSeal, picHash, sig_r, sig_s).execute();
                } else {
                    UiUtils.show("请补充完整全部信息");
                }
                break;
            case R.id.store_image_submit:
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        takePictures(SELECT_PIC_BY_TACK_PHOTO_IMAGE);//打开相机拍照
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        dialog(StoreCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                    }
                });
                break;
            case R.id.store_seal_submit:
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        takePictures(SELECT_PIC_BY_TACK_PHOTO_SEAL);//打开相机拍照
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        dialog(StoreCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                    }
                });
                break;
            default:
                break;
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

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PIC_BY_TACK_PHOTO_IMAGE) {
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
                    File fileTemp = new File(picPath);
                    File file;
                    try {
                        file = new Compressor(this).compressToFile(fileTemp);
                        picHash = MD5Util.getFileMD5String(file);
                        if (!TextUtils.equals(picHash, "default")) {
                            store_image_submit.setText("图片成功提交");
                            store_image_submit.setTextColor(R.color.colorAccent);
                            store_image_submit.setEnabled(false);
                        } else {
                            UiUtils.show("图片提交失败，请重试");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == SELECT_PIC_BY_TACK_PHOTO_SEAL) {
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
                    new AsySaveTask(this, "StoreCalligSave", picPath).execute();
                }
            } else if(requestCode==GO_TO_KEY){
                store_text_key.setText("密钥文件已上传");
                store_text_key.setEnabled(false);
            }else {
                //错误提示
                UiUtils.show("拍照失败");
            }
        }
    }

    public class AsySaveTask extends BaseAsyTask {
        private String status = "-1";

        public AsySaveTask(Context context, String string, String... params) {
            super(context, string, params);
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

        @SuppressLint("ResourceAsColor")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
                store_seal_submit.setText("重新上传");
                //new AsySaveTask(StoreCalligraphyActivity.this, "StoreCalligSave", picPath).execute();
            } else if (TextUtils.equals(s, "200")) {
                UiUtils.show("印章上传成功");
                store_seal_submit.setText("印章上传成功");
                store_seal_submit.setEnabled(false);
                store_seal_submit.setTextColor(R.color.colorAccent);
                featureSeal = generateSealFeature;
                editor.putString("generateSealFeature", generateSealFeature);//存入本地，人脸特征
                editor.apply();
            } else {
                UiUtils.show("印章上传失败失败，请重试");
            }
        }
    }

    public class AsyCreateAsset extends BaseAsyTask {
        private String status = "-1";
        private String assetID = "default";//数字资产在区块链上的键值

        public AsyCreateAsset(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络有问题，请稍候再试");
            } else if (TextUtils.equals(s, "200")) {
                UiUtils.show("恭喜您，存链成功");
                editor.putString("assetID", assetID);
                editor.apply();
                store_assetId.setText(assetID);
                store_assetId.setVisibility(View.VISIBLE);
                assetId_help.setVisibility(View.VISIBLE);
                storeSubmit_btn.setVisibility(View.GONE);
                //finish();
            } else {
                UiUtils.show("存链失败，请重试");
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (okHttpClient != null) {
                    response = okHttpClient.newCall(request).execute();
                    string = response.body().string();
                    jsonObject = new JSONObject(string);
                    status = jsonObject.optString("code");
                    assetID = jsonObject.optString("assetID");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }
}
