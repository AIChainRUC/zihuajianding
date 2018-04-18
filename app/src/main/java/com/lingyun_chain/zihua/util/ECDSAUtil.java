package com.lingyun_chain.zihua.util;


import android.util.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;


/**
 * author: anapodoton
 * created on: 2018/4/2 17:39
 * description:ECDSA(椭圆曲线签名算法)的加密算法
 * https://segmentfault.com/a/1190000012288285
 */
public class ECDSAUtil {
    private static String ALG_SIGN = "SHA1withECDSA";

    public static String sign(String key, String message) {
        try {
            PrivateKey _pri_key = null;
            key = key.replaceAll("-----", "");
            key = key.replaceAll("END(.*)KEY", "");
            key = key.replaceAll("BEGIN(.*)KEY", "");
            key = key.replaceAll("\r", "");
            key = key.replaceAll("\n", "");
            //byte[] keyBytes = Base64.getDecoder().decode(key);
            byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
//            System.out.println(this._pri_key.toString());
//            System.out.println(this._pri_key.getFormat());
//            System.out.println(this._pri_key.getAlgorithm());
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            _pri_key = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance(ALG_SIGN);
            signature.initSign(_pri_key);
            signature.update(message.getBytes());
            byte[] res = signature.sign();
            //System.out.println("siganature:" + StringUtil.bytes2Hex(res));
            return StringUtil.bytes2Hex(res);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
            return null;
        }
    }
}
