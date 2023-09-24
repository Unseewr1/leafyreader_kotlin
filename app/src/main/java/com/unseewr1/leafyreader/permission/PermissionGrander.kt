package com.unseewr1.leafyreader.permission

interface PermissionGrander {

    fun requirePermission()

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
}