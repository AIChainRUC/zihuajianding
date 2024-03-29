package com.lingyun_chain.zihua.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lingyun_chain.zihua.constants.URLConstants;
import com.lingyun_chain.zihua.util.LogUtils;

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

    private String TAG;//表示哪个方法需要调用AsyTask
    private String URL;
    private String dialogInfo;
    protected static OkHttpClient okHttpClient;
    protected static ProgressDialog pDialog = null;
    protected static FormBody.Builder builder = new FormBody.Builder();
    protected static Request request = null;
    protected static RequestBody fileBody = null;
    protected static Response response = null;

    protected String string;//响应体
    protected JSONObject jsonObject;
    protected final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/jpeg");//上传图片
    protected final MediaType MEDIA_TYPE_VIDEO = MediaType.parse("video/mp4");//上传视频

    private String imagePath;//图片的路径
    private String faceFeature;//人脸特征

    private String userName;//生成证书所需要的
    private String userGender;
    private String userDate;

    private File fileTemp;//由于需要压缩图片，所以需要定义俩个File
    private File file;

    private String desc = null;//书画基本信息
    private String delcare = null;//制式声明
    private String featureSeal = null;//印章的特征值
    private String picHash;//画全图的哈希值
    private String sig_r; //ecbsa签名_r
    private String sig_s;//ecbsa签名_s
    private String authorPUBKEY;//作家公钥
    private String defaultGrain = "0";//印章图片是否考虑纹理，值为0或1
    private String assetId;//资产ID

    private String generateCertificate;//证书
    private String jsondata;

    private String fileName;//文件名字

    public BaseAsyTask() {

    }

    public BaseAsyTask(Context context, String TAG) {
        this.context = context;
        this.TAG = TAG;
    }

    public BaseAsyTask(Context context, String TAG, String... params) {
        this.context = context;
        this.TAG = TAG;
        switch (TAG) {
            case "AsyGenerateFaceTask"://人脸特征提取
                imagePath = params[0];//图片的内容
                //builder.add("im1", imagePath);
                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.FaceURL;
                dialogInfo = "人脸识别中，请稍候...";
                break;
            case "AsyCreateCertificateTask"://生成数字证书
                faceFeature = params[0];
                userName = params[1];
                userDate = params[2];
                userGender = params[3];
                URL = URLConstants.ServerURL + URLConstants.BlockPort + URLConstants.CreateCertificateURL;
                dialogInfo = "数字证书生成中，请稍候...";
                desc = userName + " " + userGender + " "  + userDate;
                builder = new FormBody.Builder();
                builder.add("feature", faceFeature);
                builder.add("desc", desc);
                builder.add("name", userName);
                break;
            case "AsyCreateAssetTask"://字画存链
                jsondata = params[0];
//                imagePath = params[1];
//                assetID = params[2];
//                desc = params[0];
//                authorPUBKEY = params[1];
//                delcare = params[2];
//                featureSeal = params[3];
//                picHash = params[4];
//                sig_r = params[5];
//                sig_s = params[6];
                URL = URLConstants.ServerURL + URLConstants.BlockPort + URLConstants.CreateAssetURL;
                builder = new FormBody.Builder();
                builder.add("DATA", jsondata);
                dialogInfo = "字画存链中，请稍候...";
//                builder.add("desc", desc);
//                builder.add("authorPUBKEY", authorPUBKEY);
//                builder.add("delcare", delcare);
//                builder.add("feature", featureSeal);
//                builder.add("picHash", picHash);
//                builder.add("sig_r", sig_r);
//                builder.add("sig_s", sig_s);
                break;
            case "AsySaveTask"://印章特征提取
                imagePath = params[0];//图片的内容
                //builder.add("im1", imagePath);
                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.SaveURL;
                dialogInfo = "图片识别中，请稍候...";
                break;
            case "AsyRetrieveFeatureTask"://根据assetID获取链上字画印章特征
                assetId = params[0];
                URL = URLConstants.ServerURL + URLConstants.BlockPort + URLConstants.RetrieveFeatureURL;
                dialogInfo = "字画键值识别中，请稍候...";
                builder = new FormBody.Builder();
                builder.add("assetID", assetId);
                break;
            case "AsyHashTask"://字画鉴定
                imagePath = params[0];//图片的内容
                featureSeal = params[1];
                //builder.add("im1", imagePath);
                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.HashURL;
                dialogInfo = "字画鉴定中，请稍候...";
                break;
//            case "AsyCheckFaceTask"://活体验证
//                imagePath = params[0];//视频的内容
//                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.CheckURL;
//                dialogInfo = "照片上传中，请稍候...";
//                break;
            case "AsyUserFeatureTask"://使用证书
                generateCertificate = params[0];
                URL = URLConstants.ServerURL + URLConstants.BlockPort + URLConstants.RetrieveUserFeature;
                dialogInfo = "加载中，请稍候...";
                builder = new FormBody.Builder();
                builder.add("cert", generateCertificate);
                break;
            case "AsyUploadImageTask"://上传图片
                dialogInfo = "加载中，请稍候...";
                URL = URLConstants.ServerURL + URLConstants.UploadPort + URLConstants.UploadPicture;
                imagePath = params[0];
                fileName = params[1];
                break;
            case "AsyFaceVerTask"://活体验证
                URL = URLConstants.ServerURL + URLConstants.AIPort + URLConstants.CheckURL;
                imagePath = params[0];
                generateCertificate = params[1];
                dialogInfo = "视频验证中，请稍候...";
                break;
            default:
                break;
        }
        okHttpClient = new OkHttpClient.Builder()//实例化okhttp
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
        pDialog = new ProgressDialog(context);//实例化ProgressDialog
        pDialog.setMessage(dialogInfo);
        pDialog.setCancelable(false);
        pDialog.setIndeterminate(false);
        pDialog.show();

        switch (TAG) {
            case "AsyGenerateFaceTask"://证书生成部分，人脸特征的提取
                try {
                    fileTemp = new File(imagePath);
                    file = new Compressor(context).compressToFile(fileTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Log.d("hjsfile", file.getAbsolutePath());
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
            case "AsySaveTask"://字画存链,字画印章特征值提取
                try {
                    fileTemp = new File(imagePath);
                    file = new Compressor(context).compressToFile(fileTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //LogUtils.d("file", imagePath);
                //LogUtils.d("file.name", file.getName());
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                        .addFormDataPart("grain", defaultGrain)
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
            case "AsyHashTask"://字画鉴定
                try {
                    fileTemp = new File(imagePath);
                    file = new Compressor(context).compressToFile(fileTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                        .addFormDataPart("im2feature", featureSeal)
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
            case "AsyCheckFaceTask"://活体验证，人脸验证
                try {
                    fileTemp = new File(imagePath);
                    file = new Compressor(context).compressToFile(fileTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
//            case "AsyCreateAssetTask"://字画存链
//                fileBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
////                        .addFormDataPart("img", assetID, RequestBody.create(MEDIA_TYPE_JPG, file))
//                        .addFormDataPart("DATA", "null", RequestBody.create(MEDIA_TYPE_JSON, jsondata))
//                        .build();
//                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
//                break;
            case "AsyUploadImageTask":
                try {
                    fileTemp = new File(imagePath);
                    file = new Compressor(context).compressToFile(fileTemp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("img", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
                        .addFormDataPart("name", fileName)
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
            case "AsyFaceVerTask"://活体验证
                fileTemp = new File(imagePath);
                fileBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("video", fileTemp.getName(), RequestBody.create(MEDIA_TYPE_VIDEO, fileTemp))//视频
                        .addFormDataPart("videoName", fileTemp.getName())
                        .addFormDataPart("feature", generateCertificate)//人脸特征
                        .build();
                request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
                break;
            case "AsyCreateCertificateTask"://证书生成
            case "AsyRetrieveFeatureTask"://获取链上字画印章特征
            case "AsyUserFeatureTask"://活体验证中，查找链上的人脸特征
            case "AsyCreateAssetTask":
                request = new Request.Builder()
                        .url(URL)
                        .post(builder.build())
                        .addHeader("Connection", "close")
                        .build();
                break;
            default:
                break;
        }
//        if (TextUtils.equals(TAG, "GenerateFace") || TextUtils.equals(TAG, "StoreCalligSave") || TextUtils.equals(TAG, "AsyCheckTask")) {
//            try {
//                fileTemp = new File(imagePath);
//                file = new Compressor(context).compressToFile(fileTemp);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            if (TextUtils.equals(TAG, "StoreCalligSave")) {
//                fileBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
//                        .addFormDataPart("grain", defaultGrain)
//                        .build();
//            } else if (TextUtils.equals(TAG, "AsyCheckTask")) {
//                fileBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
//                        .addFormDataPart("im2feature", featureSeal)
//                        .build();
//            } else {
//                fileBody = new MultipartBody.Builder()
//                        .setType(MultipartBody.FORM)
//                        .addFormDataPart("im1", file.getName(), RequestBody.create(MEDIA_TYPE_JPG, file))
//                        .build();
//            }
//            request = new Request.Builder().url(URL).post(fileBody).addHeader("Connection", "close").build();
//        } else {
//            request = new Request.Builder()
//                    .url(URL)
//                    .post(builder.build())
//                    .addHeader("Connection", "close")
//                    .build();
//        }
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
