package com.lingyun.zihua.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lingyun.zihua.constants.URLConstants;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import id.zelory.compressor.Compressor;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * author: anapodoton
 * created on: 2018/3/27 9:38
 * description: AsyncTask的基类
 */
public class BaseAsyTask extends AsyncTask<String, String, String> {
    private Context context = null;
    //表示哪个方法需要调用AsyTask
    private String TAG;
    private String URL;
    private String dialogInfo;
    protected static OkHttpClient okHttpClient;
    protected static ProgressDialog pDialog = null;
    protected static FormBody.Builder builder = new FormBody.Builder();
    protected static Request request = null;
    protected static RequestBody fileBody = null;
    protected static Response response = null;
    protected String string;
    protected JSONObject jsonObject;
    protected final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");
    //人脸
    private String generateFace;
    private String faceFeature;
    private String userName;
    private String userGender;
    private String userDate;
    private File fileTemp;
    private File file;

    public BaseAsyTask() {

    }

    public BaseAsyTask(Context context, String TAG, String... params) {
        this.context = context;
        this.TAG = TAG;
        switch (TAG) {
            case "GenerateFace":
                generateFace = params[0];//图片的内容
                //builder.add("im1", generateFace);
                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.FaceURL;
                dialogInfo = "人脸识别中，请稍候...";
                break;
            case "CreateCertificate":
                faceFeature = params[0];
                userName = params[1];
                userDate = params[2];
                userGender = params[3];
                URL = URLConstants.ServerURL + URLConstants.BlockPort + URLConstants.CreateCertificateURL;
                dialogInfo = "数字证书生成中，请稍候...";
                String desc = userName + " " + userGender + " " + " " + userDate;
                //LogUtils.d("hjs",desc);
                builder.add("feature", faceFeature);
                builder.add("desc", desc);
                break;
            default:
                break;
        }
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5 * 1000, TimeUnit.MILLISECONDS)//链接超时;
                .readTimeout(10 * 1000, TimeUnit.MILLISECONDS) //读取超时
                .writeTimeout(10 * 1000, TimeUnit.MILLISECONDS) //写入超时
                .addInterceptor(new HttpLoggingInterceptor())
                .retryOnConnectionFailure(false)
                .build();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(dialogInfo);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
//        request = new Request.Builder()
//                .url(URL)
//                .post(builder.build())
//                .addHeader("Connection", "close")
//                .build();
        if (TextUtils.equals(TAG, "GenerateFace")) {
            try {
                fileTemp = new File(generateFace);
                file = new Compressor(context).compressToFile(fileTemp);
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                    .build();
            request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
        } else if (TextUtils.equals(TAG, "CreateCertificate")) {
            request = new Request.Builder()
                    .url(URL)
                    .post(builder.build())
                    .addHeader("Connection", "close")
                    .build();
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pDialog.dismiss();
    }
}
