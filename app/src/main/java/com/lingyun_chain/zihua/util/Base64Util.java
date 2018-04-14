package com.lingyun_chain.zihua.util;




import org.apache.commons.codec.binary.Base64;
import java.io.UnsupportedEncodingException;



public class Base64Util {

    /**
     * 解码字符串
     * @param target
     * @return
     */
    public static byte[] decode(String target){
        try {
            byte[] targetBs = target.getBytes("UTF-8");
            byte[] sourceBs = Base64.decodeBase64(targetBs);
            return sourceBs;
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private Base64Util() {
    }
}