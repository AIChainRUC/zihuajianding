package com.lingyun_chain.zihua.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.lingyun_chain.zihua.R;
import com.lingyun_chain.zihua.base.BaseActivity;
import com.lingyun_chain.zihua.base.BaseAsyTask;
import com.lingyun_chain.zihua.constants.IntentConstants;
import com.lingyun_chain.zihua.interfaceMy.PermissionListener;
import com.lingyun_chain.zihua.receiver.NetWorkChangerReceiver;
import com.lingyun_chain.zihua.util.ButtonUtil;
import com.lingyun_chain.zihua.util.FileProvider7Util;
import com.lingyun_chain.zihua.util.OSutil;
import com.lingyun_chain.zihua.util.RegularUtil;
import com.lingyun_chain.zihua.util.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * GenerateCertificateActivity 实现生成证书的功能
 */
public class GenerateCertificateActivity extends BaseActivity implements View.OnClickListener {
    //Toolbar相关
    private Toolbar toolbar;
    private EditText generate_name_edt;//姓名
    private String generate_name_string;

    private Spinner generate_gender_spinner;//性别
    private String generate_gender_string;

    private EditText generate_date_edt;//出生日期
    private String generate_date_string;

    private Button generate_submit_btn;//生成证书
    private Button generate_camera;//拍摄人脸照片

    private TextView generate_text;//人脸照片上传成功后，对用户的提示

    private String generateFaceFeature="123456";//人脸特征
    private String generatePublicKey = null;//公钥
    private String generatePrivateKey = null;//私钥
    private String generateCertificate = null;//证书
    private Uri photoUri;//拍摄照片的URI

    //private String picContent;//图片转为字符数组后的内容
    //private ImageView generate_ima;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_certificate);
        initToolbar();
        initView();
        //用于检测当前网络是否可用
        if (mReceiver != null && mFilter != null) {
            registerReceiver(mReceiver, mFilter);
        } else {
            mReceiver = new NetWorkChangerReceiver();
            mFilter = new IntentFilter();
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mReceiver, mFilter);
        }
    }

    //取消广播
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
        } else {
            mReceiver = new NetWorkChangerReceiver();
            unregisterReceiver(mReceiver);
        }
    }

    //初始化view
    private void initView() {
        generate_name_edt = (EditText) findViewById(R.id.generate_name_edt);

        generate_gender_spinner = (Spinner) findViewById(R.id.generate_gender_spinner);//选择性别
        generate_gender_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //generate_gender_string = generate_gender_spinner.getItemAtPosition(position).toString();
                generate_gender_string = "male";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                generate_gender_string = "female";
            }
        });

        generate_date_edt = (EditText) findViewById(R.id.generate_date_edt);
        generate_submit_btn = (Button) findViewById(R.id.generate_submit_btn);
        generate_submit_btn.setOnClickListener(this);
        generate_camera = (Button) findViewById(R.id.generate_camera);
        generate_camera.setOnClickListener(this);
        generate_text = (TextView) findViewById(R.id.generate_text);
        //generate_ima = (ImageView) findViewById(R.id.generate_ima);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_generate);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.generate_submit_btn) {//生成证书
            generate_name_string = generate_name_edt.getText().toString().trim();//获取姓名
            generate_date_string = generate_date_edt.getText().toString();//获取日期
            if (ButtonUtil.isFastDoubleClick()) {
                UiUtils.show("您点击过快，请稍候再试");
            } else {
                if (TextUtils.isEmpty(generate_name_string)) {
                    UiUtils.show("对不起，请输入姓名");
                } else {
                    if (!RegularUtil.isValidDate(generate_date_string)) {
                        UiUtils.show("对不起，您输入的日期非法，请重新输入");
                        generate_date_edt.setText("");
                    } else {
                        if (TextUtils.isEmpty(generateFaceFeature)) {
                            UiUtils.show("请先上传人脸照片");
                        } else {//生成证书,传入人脸特征值，姓名，出生年月，性别
                            new AsyCreateCertificateTask(GenerateCertificateActivity.this,
                                    "AsyCreateCertificateTask",
                                    generateFaceFeature,
                                    generate_name_string,
                                    generate_date_string,
                                    generate_gender_string).execute();
                        }
                    }
                }
            }
        } else if (v.getId() == R.id.generate_camera) {//拍照上传，获取人脸特征值
            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void onGranted() {//获取动态权限
                    UiUtils.hideInput(GenerateCertificateActivity.this, generate_date_edt);//隐藏键盘
                    takePictures();//打开相机拍照
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    dialog(GenerateCertificateActivity.this, "上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                }
            });
        }
    }

    //Activity的回调方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IntentConstants.SELECT_PIC_BY_TACK_PHOTO) {
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
                //picPath = FileUtil.getPath() + "img/generatePhoto" + "/face.jpg";
                if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
                    new AsyGenerateFace(GenerateCertificateActivity.this, "GenerateFace", picPath).execute();

                } else {
                    //错误提示
                    UiUtils.show("拍照失败");
                }
            }
//            if (requestCode == CROP_PICTURE) {
//                if (photoUri != null) {
//                    Bitmap bitmap = BitmapFactory.decodeFile(picPath);
//                    if (bitmap != null) {
//                        //photo_iv.setImageBitmap(bitmap);
//                    }
//                }
//            }
        } else {
            UiUtils.show("拍照失败");
        }
    }

    /**
     * 图片裁剪，参数根据自己需要设置
     *
     * @param uri
     * @param REQUE_CODE_CROP
     */
    private void startPhotoZoom(Uri uri,
                                int REQUE_CODE_CROP) {
        int dp = 500;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 4);//输出是X方向的比例
        intent.putExtra("aspectY", 3);
        intent.putExtra("outputX", 600);//输出X方向的像素
        intent.putExtra("outputY", 450);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", false);//设置为不返回数据
        startActivityForResult(intent, REQUE_CODE_CROP);
    }

    /**
     * 7.0以上版本图片裁剪操作
     *
     * @param imagePath
     * @param REQUE_CODE_CROP
     */
    private void cropForN(String imagePath, int REQUE_CODE_CROP) {
        Uri cropUri = getImageContentUri(new File(imagePath));
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(cropUri, "image/*");
        intent.putExtra("crop", "true");
        //输出是X方向的比例
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 600);
        intent.putExtra("outputY", 450);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUE_CODE_CROP);
    }

    private Uri getImageContentUri(File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    //打开相机拍照
    private void takePictures() {
        //执行拍照前，应该先判断SD卡是否存在
        if (OSutil.isSdExist()) {
//            File file = new File(Environment.getExternalStorageDirectory(), "/generatePhoto/face.jpg");
//            if (!file.getParentFile().exists()) file.getParentFile().mkdirs();
//            photoUri = FileProvider.getUriForFile(GenerateCertificateActivity.this, "com.lingyun_chain.zihua.fileProvider", file);//通过FileProvider创建一个content类型的Uri
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
                startActivityForResult(intent, IntentConstants.SELECT_PIC_BY_TACK_PHOTO);
            }

//            if (Build.VERSION.SDK_INT >= 24) {
//                photoUri = FileProvider.getUriForFile(GenerateCertificateActivity.this, "com.lingyun_chain.zihua.fileProvider", temp);
//            } else {
//                photoUri = Uri.fromFile(temp);
//            }
//           //处理授权问题
//            List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//            for (ResolveInfo resolveInfo : resInfoList) {
//                String packageName = resolveInfo.activityInfo.packageName;
//                grantUriPermission(packageName, photoUri,
//                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//            }


//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            ContentValues values = new ContentValues();
//            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            //startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        } else {
            UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
        }
    }

//    public class AsyImageToStr extends AsyncTask<String, String, String> {
//        File file;
//        ProgressDialog pDialog;
//
//        @Override
//        protected String doInBackground(String... strings) {
//            picContent = getToString(getFileByte(file));
//            return picContent;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            pDialog = new ProgressDialog(GenerateCertificateActivity.this);
//            pDialog.setMessage("图片正在处理中");
//            pDialog.setCancelable(false);
//            pDialog.setIndeterminate(false);
//            pDialog.show();
//            file = new File(picPath);
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            if (!TextUtils.isEmpty(s)) {
//                pDialog.dismiss();
//
//            }
//        }
//    }

    public class AsyGenerateFace extends BaseAsyTask {
        private String status = "-1";
        private File mFile;

        public AsyGenerateFace(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                generateFaceFeature = jsonObject.optString("feature");
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
            if (TextUtils.equals(s, "200")) {//200说明人脸识别成功
                UiUtils.show("人脸识别成功");
                //generate_ima.setVisibility(View.VISIBLE);
                //generate_ima.setImageBitmap(bitmap);
                generate_camera.setVisibility(View.GONE);
                generate_text.setText("照片上传成功，可生成证书");
                generate_text.setVisibility(View.VISIBLE);
                editor.putString("generateFaceFeature", generateFaceFeature);//存入本地，人脸特征
                editor.apply();
                mFile = new File(picPath);
                mFile.delete();
            } else if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
//                generate_camera.setText("重新上传");
//                generate_camera.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        if (ButtonUtil.isFastDoubleClick()) {
//                            UiUtils.show("您的操作过于频繁，请稍候再试");
//                        } else {
//                            new AsyGenerateFace(GenerateCertificateActivity.this, "GenerateFace", picPath).execute();
//                        }
//                    }
//                });
            } else {
                UiUtils.show("人脸识别失败，请重试");
                generate_camera.setText("拍照");
                generate_camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ButtonUtil.isFastDoubleClick()) {
                            UiUtils.show("您的操作过于频繁，请稍候再试");
                        } else {
                            //new AsyGenerateFace(GenerateCertificateActivity.this, "GenerateFace", picPath).execute();
                            takePictures();
                        }
                    }
                });
            }
        }
    }

    public class AsyCreateCertificateTask extends BaseAsyTask {
        private String status = "-1";

        public AsyCreateCertificateTask(Context context, String string, String... params) {
            super(context, string, params);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (TextUtils.equals(s, "200")) {//200说明证书生成成功
                UiUtils.show("证书生成成功");
                editor.putString("generatePublicKey", generatePublicKey);
                editor.putString("generatePrivateKey", generatePrivateKey);
                editor.putString("generateCertificate", generateCertificate);
                editor.apply();
                Intent intent = new Intent();
                intent.putExtra("generatePublicKey", generatePublicKey);
                intent.putExtra("generatePrivateKey", generatePrivateKey);
                intent.putExtra("generateCertificate", generateCertificate);
                intent.putExtra("generateFaceFeature", generateFaceFeature);
                setResult(RESULT_OK, intent);
                finish();
            } else if (TextUtils.equals(s, "-1")) {
                UiUtils.show("网络超时，请重试");
            } else {
                UiUtils.show("证书生成失败，请重试");
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                if (okHttpClient != null) {
                    response = okHttpClient.newCall(request).execute();
                }
                response = okHttpClient.newCall(request).execute();
                string = response.body().string();
                jsonObject = new JSONObject(string);
                status = jsonObject.optString("code");
                generatePublicKey = jsonObject.optString("publicKey");
                generatePrivateKey = jsonObject.optString("privateKey");
                generateCertificate = jsonObject.getString("certificate");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            okHttpClient.dispatcher().cancelAll();
            okHttpClient.connectionPool().evictAll();
            return status;
        }
    }

    byte[] byt;

    //图片转为字节流
    public byte[] getFileByte(File file) {
        try {
            File compressedImageFile = new Compressor(this).compressToFile(file);
            FileInputStream fis = new FileInputStream(compressedImageFile);
            byt = new byte[(int) compressedImageFile.length()];
            fis.read(byt, 0, byt.length);
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return byt;
    }

    //字符数组转化为字符串
    public String getToString(byte[] arrayData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < arrayData.length; i++) {
            stringBuilder.append(arrayData[i]);
            if (i < arrayData.length - 1) {
                stringBuilder.append(",");
            }
        }
        return stringBuilder.toString();
    }
}
