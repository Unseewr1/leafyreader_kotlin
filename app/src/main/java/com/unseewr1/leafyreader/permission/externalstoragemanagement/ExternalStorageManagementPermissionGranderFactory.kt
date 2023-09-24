package com.unseewr1.leafyreader.permission.externalstoragemanagement

import android.app.Activity
import android.os.Build
import com.unseewr1.leafyreader.permission.PermissionGrander

object ExternalStorageManagementPermissionGranderFactory {

    fun create(activity: Activity): PermissionGrander {
        return when (Build.VERSION.SDK_INT) {
            in 1..29 -> ApiLevel30ExternalStorageManagementPermissionGrander(activity)
            in 30 .. Int.MAX_VALUE -> ApiLevel30ExternalStorageManagementPermissionGrander(activity)

            else -> throw NotImplementedError()
        }
    }
}