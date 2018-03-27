package com.lingyun.zihua.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.lingyun.zihua.constants.URLConstants;
import com.lingyun.zihua.util.LogUtils;

import org.json.JSONObject;

import java.io.File;
import java.lang.annotation.Target;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
    protected File file;
    //人脸
    private String generateFace;
    private String faceFeature;
    private String userName;
    private String userGender;
    private String userDate;
    public BaseAsyTask(){

    }
    public BaseAsyTask(Context context, String TAG, String... params) {
        this.context = context;
        this.TAG = TAG;
        switch (TAG) {
            case "GenerateFace":
                generateFace = params[0];//文件的路径
                URL= URLConstants.ServerURL+URLConstants.AIPort+URLConstants.FaceURL;
                dialogInfo="人脸识别中，请稍候...";
                break;
            case "CreateCertificate":
                faceFeature=params[0];
                userName=params[1];
                userDate=params[2];
                userGender=params[3];
                URL= URLConstants.ServerURL+URLConstants.BlockPort+URLConstants.CreateCertificateURL;
                dialogInfo="数字证书生成中，请稍候...";
                String desc=userName+" "+userGender+" "+" "+userDate;
                //LogUtils.d("hjs",desc);
                builder.add("feature",faceFeature);
                builder.add("desc",desc);
                break;
            default:
                break;
        }
        okHttpClient=new OkHttpClient();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(dialogInfo);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();
        if(TextUtils.equals(TAG,"GenerateFace")){
            file=new File(generateFace);
            fileBody=new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("im1",generateFace,RequestBody.create(MEDIA_TYPE_JPG,file))
                    .build();
            request=new Request.Builder().url(URL).post(fileBody).build();
        }else if(TextUtils.equals(TAG,"CreateCertificate")){
            request = new Request.Builder()
                    .url(URL)
                    .post(builder.build())
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
