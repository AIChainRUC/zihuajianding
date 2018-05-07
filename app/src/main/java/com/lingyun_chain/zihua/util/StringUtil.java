package com.lingyun_chain.zihua.util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * author: anapodoton
 * created on: 2018/4/15 19:39
 * description:字节数组数组和16进制进行转换
 */
public class StringUtil {
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    //将字节数组转换为16进制字符串
    public static String bytes2Hex(byte[] bytes) {
        char[] buf = new char[bytes.length * 2];
        int index = 0;
        for (byte b : bytes) { // 利用位运算进行转换
            buf[index++] = HEX_CHAR[b >>> 4 & 0xf];
            buf[index++] = HEX_CHAR[b & 0xf];
        }
        return new String(buf);
    }

    //将16进制字符串转换为字节数组
    public static byte[] HEX2Bytes(String hexStr) {
        byte[] bytes = new byte[hexStr.length() / 2];
        for (int i = 0; i < hexStr.length() / 2; i++) {
            String subStr = hexStr.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }
        return bytes;
    }

    //把书画基本信息转为json格式
    //分别代表作品名字，作品尺寸，创作年代，作品分类，作品材质，作品题材
    public static String stringDescToJson(String store_workName, String store_workSize, String creationYear, String classificationWork, String materialWork, String subjectWork) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("store_workName", store_workName);
            jsonObject.put("store_workSize", store_workSize);
            jsonObject.put("creationYear", creationYear);
            jsonObject.put("classificationWork", classificationWork);
            jsonObject.put("materialWork", materialWork);
            jsonObject.put("subjectWork", subjectWork);
            //LogUtils.d("logJson",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    //把需要发送的数据打包成json
    public static String stringDateToJson(String assetID, String desc, String generatePublicKey, String delcare, String featureSeal, String picHash, String signAsset) {
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonObject1;
        try {
            jsonObject1=new JSONObject(desc);
            jsonObject.put("ID", assetID);
            jsonObject.put("desc", jsonObject1);
            jsonObject.put("authorPUBKEY", generatePublicKey);
            jsonObject.put("delcare", delcare);
            jsonObject.put("feature", featureSeal);
            jsonObject.put("picHash", picHash);
            jsonObject.put("sig", signAsset);
            //LogUtils.d("logJson",jsonObject.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }
}
