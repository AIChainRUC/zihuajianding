package com.lingyun_chain.zihua.interfaceMy;

import java.util.List;

/**
 * 运行时权限监听的窗口
 */

public interface PermissionListener {
    void onGranted();//已授予权限
    void onDenied(List<String> deniedPermission);//被拒绝的权限
}
