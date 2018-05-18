package com.lingyun_chain.zihua.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Toast工具类
 * Created by AFrogInAWell on 2016/12/13.
 */

public class ToasUtils {
    public static Toast myToast;

    public static void showToast(Context context, String string) {
        if (myToast == null) {
            myToast = Toast.makeText(context,
                    string, Toast.LENGTH_SHORT);
        }else{
            myToast.setText(string);
        }
        myToast.show();
    }
}
