package com.unseewr1.leafyreader.permission.externalstoragemanagement

import android.app.Activity
import com.unseewr1.leafyreader.permission.PermissionGrander

abstract class ExternalStorageManagementPermissionGrander(protected val activity: Activity) : PermissionGrander {

    override fun requirePermission() {
        if (!permissionReceived()) {
            tryObtainPermission()
        }
    }


    protected abstract fun permissionReceived(): Boolean

    protected abstract fun tryObtainPermission()
}