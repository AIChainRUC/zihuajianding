package com.lingyun_chain.zihua.util;

import android.os.Environment;

/**
 * author: anapodoton
 * created on: 2018/3/26 9:38
 * description: 系统工具类
 */
public class OSutil {
    //判断sd卡是否出在存在,防止SD卡不存在的情况下，APP崩溃
    public static boolean isSdExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
