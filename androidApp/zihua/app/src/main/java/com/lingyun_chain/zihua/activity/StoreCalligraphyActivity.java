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
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.constants.IntentConstants;
import com.lingyun_chain.zihua.interfaceMy.PermissionListener;
import com.lingyun_chain.zihua.util.ECDSAUtil;
import com.lingyun_chain.zihua.util.FileProvider7Util;
import com.lingyun_chain.zihua.util.LogUtils;
import com.lingyun_chain.zihua.util.MD5Util;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.SHAUtil;
import com.lingyun_chain.zihua.util.StringUtil;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.zelory.compressor.Compressor;


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
    private String generatePrivateKey = null;//私钥
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

    private String generateSealFeature;//印章特征
    private TextView store_assetId;
    private TextView assetId_help;
    //private boolean isFaceVer = false;//是否进行了活体验证
    private String assetID = "default";//数字资产在区块链上的键值
    private String assetFilePath;//存链后用于上传字画照片到服务器
    private boolean isStored = false;//存链是否成功

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
            case R.id.store_text_key://生成证书
//                ECDSAUtil.jdkECDSA("hello","-----BEGIN PRIVATE KEY-----\n" +
//                        "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQg2s37G4uKJImgCkuj\n" +
//                        "800/f/Z/475+NTqZAbslXVOdmhmhRANCAASOqTlpV9ABI/l5nqIqKtQhERcCJiXz\n" +
//                        "+7Va4SnlXzwGaLek/rqbp9bWjIU3GSU7ETk0dDwgYkR5xu2D8+wSSVGv\n" +
//                        "-----END PRIVATE KEY-----\n" +
//                        "\n");
                startActivityForResult(new Intent(StoreCalligraphyActivity.this, GenerateCertificateActivity.class), IntentConstants.GO_TO_KEY);
                break;
            case R.id.store_submit://字画存链
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
                    //desc = store_workName + " " + store_workSize + " " + " " + creationYear + " " + classificationWork + " " + materialWork + " " + subjectWork;
                    desc = StringUtil.stringDescToJson(store_workName, store_workSize, creationYear, classificationWork, materialWork, subjectWork);//书画基本信息转化为json格式
                    assetID = SHAUtil.getSHA256StrJava(desc + generatePublicKey + delcare + featureSeal + picHash);//资产唯一的键值,留给用户看的
                    String signAsset = ECDSAUtil.sign(generatePrivateKey, assetID);//对资产ID进行签名
                    String jsonData = StringUtil.stringDateToJson(assetID, desc, generatePublicKey, delcare, featureSeal, picHash, signAsset);//把需要发送的数据打包成json//stringToJson(assetID,desc,generatePublicKey,delcare,featureSeal,picHash,sig_r,sig_s);
                    if (isStored == true) {//存链成功，仅上传图片
                        new AsyUploadImageTask(StoreCalligraphyActivity.this, "AsyUploadImageTask", assetFilePath, assetID).execute();//图片上传失败，需要再次上传
                    } else {//否则，先存链
                        new AsyCreateAssetTask(StoreCalligraphyActivity.this,
                                "AsyCreateAssetTask", jsonData).execute();//直接存链
                    }
//                    if (isFaceVer == true) {
//                        //String signAsset=ECDSAUtil.sign(assetID, generatePrivateKey);//对资产ID进行签名
//                        //StringUtil.stringDateToJson(assetID, desc, generatePublicKey, delcare, featureSeal, picHash, signAsset);//把需要发送的数据打包成json
//                    } else {
//                        AlertDialog.Builder builder = new AlertDialog.Builder(StoreCalligraphyActivity.this);
//                        builder.setTitle("温馨提示");
//                        builder.setMessage("为了保证您的安全，我们建议您进行人脸识别");
//                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                UiUtils.show("对不起，请您先进行人脸识别");
//                            }
//                        });
//                        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                startActivityForResult(new Intent(StoreCalligraphyActivity.this, SignCertificateActivity.class), GO_TO_FACE);
//                            }
//                        });
//                        builder.create().show();
                    // }
                } else {
                    UiUtils.show("请补充完整全部信息");
                }
                break;
            case R.id.store_image_submit://上传照片
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        UiUtils.hideInput(StoreCalligraphyActivity.this, store_image_submit);
                        takePictures(IntentConstants.SELECT_PIC_BY_TACK_PHOTO_IMAGE);//打开相机拍照
                    }

                    @Override
                    public void onDenied(List<String> deniedPermission) {
                        dialog(StoreCalligraphyActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                    }
                });
                break;
            case R.id.store_seal_submit://上传印章
                BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        takePictures(IntentConstants.SELECT_PIC_BY_TACK_PHOTO_SEAL);//打开相机拍照
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
                picPath = temp.getAbsolutePath();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);//将拍取的照片保存到指定URI
                startActivityForResult(intent, TAG);
            } else {
                UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
            }
        }
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentConstants.SELECT_PIC_BY_TACK_PHOTO_IMAGE) {//获取图片的hash
//                String[] pojo = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
//                if (cursor != null) {
//                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//                    cursor.moveToFirst();
//                    picPath = cursor.getString(columnIndex);
//                    if (Build.VERSION.SDK_INT < 14) {
//                        cursor.close();
//                    }
//                }
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    File fileTemp = new File(picPath);
                    File file;
                    try {
                        file = new Compressor(this).compressToFile(fileTemp);
                        picHash = MD5Util.getFileMD5String(file);
                        assetFilePath = picPath;//对图片重新命名
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
            } else if (requestCode == IntentConstants.SELECT_PIC_BY_TACK_PHOTO_SEAL) {//上传印章
//                String[] pojo = {MediaStore.Images.Media.DATA};
//                Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
//                if (cursor != null) {
//                    int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
//                    cursor.moveToFirst();
//                    picPath = cursor.getString(columnIndex);
//                    if (Build.VERSION.SDK_INT < 14) {
//                        cursor.close();
//                    }
//                }
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    new AsySaveTask(this, "AsySaveTask", picPath).execute();//提取印章特征值
                }
            } else if (requestCode == IntentConstants.GO_TO_KEY) {
                store_text_key.setText("密钥文件已上传");
                store_text_key.setEnabled(false);
            }
        } else {
            UiUtils.show("拍照失败");
        }
    }

    public class AsySaveTask extends BaseAsyTask {//提取印章特征值
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

    public class AsyCreateAssetTask extends BaseAsyTask {
        private String status = "-1";


        public AsyCreateAssetTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络有问题，请稍候再试");
            } else if (TextUtils.equals(s, "200")) {
                isStored = true;//存链成功
                new AsyUploadImageTask(StoreCalligraphyActivity.this, "AsyUploadImageTask", assetFilePath, assetID).execute();//上传图片
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
                    //assetID = jsonObject.optString("assetID");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return status;
        }
    }

    public class AsyUploadImageTask extends BaseAsyTask {//上传图片
        private String status = "-1";

        public AsyUploadImageTask(Context context, String string, String... params) {
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
