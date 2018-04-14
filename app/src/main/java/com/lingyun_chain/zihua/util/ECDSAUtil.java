package com.lingyun_chain.zihua.util;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * author: anapodoton
 * created on: 2018/4/2 17:39
 * description:ECDSA(椭圆曲线签名算法)的加密算法
 * https://segmentfault.com/a/1190000012288285
 */
public class ECDSAUtil {
    static String strResult = null;

    public static String jdkECDSA(String str, String myPrivateKey) {
        try {
            byte[] result = null;
            //实例化PrivateKey
            PrivateKey privateKey = loadECPrivateKey(myPrivateKey,"RSA");
//            PKCS8EncodedKeySpec priPKCS8;
//            priPKCS8 = new PKCS8EncodedKeySpec(myPrivateKey.getBytes());
//            KeyFactory keyFactory = KeyFactory.getInstance("EC");
//            privateKey = keyFactory.generatePrivate(priPKCS8);
            Signature signature = Signature.getInstance("SHA1withRSA");
            signature.initSign(privateKey);
            signature.update(str.getBytes());
            result = signature.sign();
            strResult=bytesToHexFun(result);
            LogUtils.d("hjs",strResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResult;
    }
    public static PrivateKey loadECPrivateKey(String content,  String algorithm) throws Exception {
        String privateKeyPEM = content.replace("-----BEGIN PRIVATE KEY-----\n", "")
                .replace("-----END PRIVATE KEY-----", "").replace("\n", "");
        //byte[] asBytes = Base64Util.decode(privateKeyPEM);
        byte[] asBytes=Base64.decode(privateKeyPEM,Base64.DEFAULT);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(asBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(spec);
    }
    public static String bytesToHexFun(byte[] bytes) {
        StringBuilder buf = new StringBuilder(bytes.length * 2);
        for(byte b : bytes) { // 使用String的format方法进行转换
            buf.append(String.format("%02x", new Integer(b & 0xff)));
        }

        return buf.toString();
    }
}
