package com.lingyun_chain.zihua.other;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于Activity的管理
 */

public class ActivityCollector {
    private static List<Activity> activityList=new ArrayList<>();
    //添加Activity
    public static void addActivity(Activity activity){
        activityList.add(activity);
    }
    //移除Activity
    public static void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public static void removeAllActivity(){
        for(Activity activity1:activityList){
                activity1.finish();
        }
    }
    //取出顶部Activity
    public static Activity getTopActivity(){
        if(activityList.isEmpty()){
            return null;
        }else {
            return activityList.get(activityList.size()-1);
        }
    }
}
