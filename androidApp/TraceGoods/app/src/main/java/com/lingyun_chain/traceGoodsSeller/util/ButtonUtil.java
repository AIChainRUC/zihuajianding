package com.lingyun_chain.traceGoodsSeller.util;

/**
 * author: anapodoton
 * created on: 2018/3/28 20:57
 * description:控制button不能被连续点击
 */
public class ButtonUtil {
    private static long lastClickTime = 0;
    private static long DIFF = 3000;
    private static int lastButtonId = -1;
    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     * @return
     */
    public static boolean isFastDoubleClick()
    {
        return isFastDoubleClick(-1,DIFF);
    }
    /**
     * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId)
    {
        return isFastDoubleClick(buttonId,DIFF);
    }
    /**
     * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
     * @param diff
     * @return
     */
    public static boolean isFastDoubleClick(int buttonId,long diff)
    {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (lastButtonId == buttonId && lastClickTime>0 && timeD < diff)
        {
            return true;
        }

        lastClickTime = time;
        lastButtonId = buttonId;
        return false;
    }
}
