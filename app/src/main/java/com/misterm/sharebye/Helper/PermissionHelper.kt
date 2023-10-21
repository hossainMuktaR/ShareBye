package com.misterm.sharebye.Helper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionHelper{
    fun hasPermissonSingle(context: Context, permissionName: String, requestCode: Int):  Boolean{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (ContextCompat.checkSelfPermission(context,permissionName) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(context as Activity, arrayOf(permissionName),requestCode)
                return false
            }
        }
        return true
    }

    fun hasStoragePermission(context: Context,permissionName: String, requestCode: Int): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                    addCategory("android.intent.category.DEFAULT")
                    data = Uri.parse("package:${context.applicationContext.packageName}")
                }
                ContextCompat.startActivity(context, intent, null)
                return false
            }
            return true
        } else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (context.checkSelfPermission(permissionName) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(context as Activity,arrayOf<String>(permissionName), requestCode)
                    return false
                }
                return true
            }
        return true
    }
}