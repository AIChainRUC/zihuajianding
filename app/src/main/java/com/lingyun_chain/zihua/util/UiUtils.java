package com.lingyun_chain.zihua.util;

import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.lingyun_chain.zihua.base.BaseApplication;

/**
 * UI工具类
 */
public class UiUtils {
    public static Toast myToast;
    public static Resources getResource() {
        return BaseApplication.getInstance().getResources();
    }

    public static Context getContext() {
        return BaseApplication.getInstance();
    }
    /**
     * 强制隐藏输入法键盘
     *
     * @param context Context
     * @param view    EditText
     */
    public static void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getResource().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * px转换dip
     */

    public static int px2dip(int px) {
        final float scale = getResource().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    /**
     * sp转化为px显示
     *
     * @param sp
     * @return
     */
    public static int sp2px(int sp) {
        final float scale = getResource().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    /**
     * 把Runnable 方法提交到主线程运行
     *
     * @param runnable
     */
    public static void runOnUiThread(Runnable runnable) {
        // 在主线程运行
        if (android.os.Process.myTid() == BaseApplication.getMainTid()) {
            runnable.run();
        } else {
            //获取handler
            BaseApplication.getHandler().post(runnable);
        }
    }

    /**
     * 延迟执行 任务
     *
     * @param run  任务
     * @param time 延迟的时间
     */
    public static void postDelayed(Runnable run, int time) {
        BaseApplication.getHandler().postDelayed(run, time); // 调用Runable里面的run方法
    }

    /**
     * 取消任务
     *
     * @param auToRunTask
     */
    public static void cancel(Runnable auToRunTask) {
        BaseApplication.getHandler().removeCallbacks(auToRunTask);
    }

    public static void show(String string) {
        if (myToast == null) {
            myToast = Toast.makeText(BaseApplication.getInstance(),
                    string, Toast.LENGTH_SHORT);
        }else{
            myToast.setText(string);
        }
        myToast.show();
    }
    public static void showInCenter(String content) {
        Toast toast = Toast.makeText(BaseApplication.getInstance(), content, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
