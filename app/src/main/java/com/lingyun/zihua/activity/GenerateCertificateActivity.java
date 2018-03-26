package com.lingyun.zihua.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import com.lingyun.zihua.R;
import com.lingyun.zihua.base.BaseActivity;
import com.lingyun.zihua.interfaceMy.PermissionListener;
import com.lingyun.zihua.receiver.NetWorkChangerReceiver;
import com.lingyun.zihua.util.LogUtils;
import com.lingyun.zihua.util.OSutil;
import com.lingyun.zihua.util.RegularUtil;
import com.lingyun.zihua.util.UiUtils;

import java.io.File;
import java.util.List;

/**
 * GenerateCertificateActivity 实现生成证书的功能
 */
public class GenerateCertificateActivity extends BaseActivity implements View.OnClickListener{
    //Toolbar相关
    private Toolbar toolbar;
    private EditText generate_name_edt;
    private Spinner generate_gender_spinner;
    private EditText generate_date_edt;
    private String generate_gender_string;
    private String generate_name_string;
    private String generate_date_string;
    private Button generate_submit_btn;
    private Button generate_camera;
    private ImageView generate_ima;
    private static final int PERMISSIONS_FOR_TAKE_PHOTO = 10;
    //图片文件路径
    private String picPath;
    //图片对应Uri
    private Uri photoUri;
    //拍照对应RequestCode
    public static final int SELECT_PIC_BY_TACK_PHOTO = 1;
    //裁剪图片
    private static final int CROP_PICTURE = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_certificate);
        initToolbar();
        initView();
        //注册服务
        if (mReceiver != null && mFilter != null) {
            registerReceiver(mReceiver, mFilter);
        } else {
            mReceiver = new NetWorkChangerReceiver();
            mFilter = new IntentFilter();
            mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(mReceiver, mFilter);
        }
    }
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
    private void initView() {
        generate_name_edt=(EditText)findViewById(R.id.generate_name_edt);
        generate_gender_spinner=(Spinner)findViewById(R.id.generate_gender_spinner);
        generate_gender_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                generate_gender_string=generate_gender_spinner.getItemAtPosition(position).toString();
                //UiUtils.show(generate_gender_string);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                generate_gender_string="男";
            }
        });
        generate_date_edt =(EditText)findViewById(R.id.generate_date_edt);
        generate_submit_btn=(Button)findViewById(R.id.generate_submit_btn);
        generate_submit_btn.setOnClickListener(this);
        generate_camera=(Button)findViewById(R.id.generate_camera);
        generate_camera.setOnClickListener(this);
        generate_ima=(ImageView)findViewById(R.id.generate_ima);
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
        if(v.getId()==R.id.generate_submit_btn){
            generate_name_string=generate_name_edt.getText().toString().trim();//获取姓名
            generate_date_string=generate_date_edt.getText().toString();
            //Log.d("hjs",generate_date_string);
            if(!RegularUtil.isValidDate(generate_date_string)){
                UiUtils.show("对不起，您输入的日期非法，请重新输入");
                generate_date_edt.setText("");
            }
        }else if(v.getId()==R.id.generate_camera){//拍照上传
            BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                @Override
                public void onGranted() {
                    takePictures();//打开相机拍照
                }

                @Override
                public void onDenied(List<String> deniedPermission) {
                    dialog(GenerateCertificateActivity.this,"上传照片需要该权限，拒绝后将不能正常使用，是否重新开启此权限？");
                }
            });
        }
    }
    //Activity的回调方法
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
                    photoUri = Uri.fromFile(new File(picPath));
                    if (photoUri != null) {
                        Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                        if (bitmap != null) {
                            generate_ima.setVisibility(View.VISIBLE);
                            generate_ima.setImageBitmap(bitmap);
                        }
                    }
//                    if (Build.VERSION.SDK_INT > 23) {
//                        photoUri = FileProvider.getUriForFile(this, "com.lingyun.zihua.fileprovider", new File(picPath));
//                        cropForN(picPath, CROP_PICTURE);
//                    } else {
//                        startPhotoZoom(photoUri, CROP_PICTURE);
//                    }
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
        if(OSutil.isSdExist()){
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ContentValues values = new ContentValues();
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, SELECT_PIC_BY_TACK_PHOTO);
        }else {
            UiUtils.show("对不起，您的手机的SD卡未插入，不能使用该功能");
        }
    }
}
