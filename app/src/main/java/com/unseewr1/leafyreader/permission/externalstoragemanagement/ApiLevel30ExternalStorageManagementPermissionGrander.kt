package com.unseewr1.leafyreader.permission.externalstoragemanagement

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.startActivity
import com.unseewr1.leafyreader.BuildConfig

class ApiLevel30ExternalStorageManagementPermissionGrander(activity: Activity) :
    ExternalStorageManagementPermissionGrander(activity) {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun permissionReceived(): Boolean = Environment.isExternalStorageManager()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun tryObtainPermission() {
        val uri = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
        startActivity(
            activity,
            Intent(
                Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                uri
            ),
            null
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //permission grands with Intent, no need to handle
    }
}