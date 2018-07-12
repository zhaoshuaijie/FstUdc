package com.lcsd.fstudc.permissions;

import java.util.ArrayList;

/**
 * 当前类注释：权限请求回调
 * Created  2017/3/9.16:24
 */

public interface PerimissionsCallback {

    void onGranted(ArrayList<PermissionEnum> grantedList);

    void onDenied(ArrayList<PermissionEnum> deniedList);
}
