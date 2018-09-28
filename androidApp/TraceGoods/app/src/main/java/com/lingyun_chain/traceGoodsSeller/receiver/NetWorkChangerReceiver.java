package com.lingyun_chain.traceGoodsSeller.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.lingyun_chain.traceGoodsSeller.util.UiUtils;

/**
 * author: anapodoton
 * created on: 2018/3/26 11:33
 * description:监听网络状态的改变
 */
public class NetWorkChangerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager=(ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
        if(info==null||!info.isAvailable()){
            UiUtils.show("当前网络不可用");
        }
    }
}
