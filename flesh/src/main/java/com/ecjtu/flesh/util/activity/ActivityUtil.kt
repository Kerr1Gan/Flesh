package com.ecjtu.flesh.util.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.app.ActivityManager



/**
 * Created by Ethan_Xiang on 2017/8/10.
 */
object ActivityUtil{
    //Settings.ACTION_APPLICATION_DETAIL_SETTING
    fun getAppDetailSettingIntent(context: Context): Intent {
        var localIntent = Intent()
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            localIntent.setData(Uri.fromParts("package", context.getPackageName(), null))
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW)
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails")
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName())
        }
        return localIntent
    }

    /**
     * 程序是否在前台运行
     *
     */
    fun isAppOnForeground(context: Context): Boolean {
        val activityManager = context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val packageName = context.getApplicationContext().getPackageName()
        /**
         * 获取Android设备中所有正在运行的App
         */
        val appProcesses = activityManager
                .runningAppProcesses ?: return false
        for (appProcess in appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName == packageName && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true
            }
        }
        return false
    }
}