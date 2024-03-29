package com.lingyun_chain.zihua.constants;

/**
 * author: anapodoton
 * created on: 2018/3/26 19:16
 * description:
 */
public class URLConstants {
    public static final String ServerURL = "http://114.242.26.112";//服务器的地址
    public static final String AIPort = ":6175";
    public static final String BlockPort = ":6174";
    public static final String UploadPort = ":5000";
    public static final String FaceURL = "/face";//人脸特征提取
    public static final String CreateCertificateURL = "/channels/mychannel/chaincodes/urcc/createCertificate";//生成数字证书
    public static final String CreateAssetURL = "/channels/mychannel/chaincodes/urcc/createAsset";//字画存链
    public static final String SaveURL = "/save";//印章特征提取
    public static final String RetrieveFeatureURL = "/channels/mychannel/chaincodes/urcc/retrieveFeature";//根据assetID获取链上字画印章特征
    public static final String CheckURL = "/check";//短视频验证，活体验证
    public static final String RetrieveUserFeature = "/channels/mychannel/chaincodes/urcc/retrieveUserFeature";//证书使用，查找链上人脸特征
    public static final String UploadPicture = "/upload/";//上传图片
    public static final String HashURL = "/hash";//字画鉴定
    public static final String DownLoadURL="/download/";//下载图片
}
