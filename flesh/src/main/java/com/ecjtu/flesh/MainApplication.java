package com.ecjtu.flesh;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.ecjtu.flesh.db.DatabaseManager;
import com.ecjtu.flesh.db.table.impl.ClassPageListTableImpl;
import com.ecjtu.flesh.db.table.impl.ClassPageTableImpl;
import com.ecjtu.flesh.db.table.impl.DetailPageTableImpl;
import com.ecjtu.flesh.db.table.impl.DetailPageUrlsTableImpl;
import com.ecjtu.flesh.db.table.impl.HistoryTableImpl;
import com.ecjtu.flesh.db.table.impl.LikeTableImpl;
import com.ecjtu.flesh.db.table.impl.LikeTableImplV2;
import com.tencent.bugly.Bugly;

import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (isAppMainProcess("com.ecjtu.flesh")) {
            SimpleGlideModule module = new SimpleGlideModule();
            GlideBuilder builder = new GlideBuilder();
            module.applyOptions(this, builder);
            Glide glide = builder.build(this);
            Glide.init(glide);

            initDb();
            initSDK();
            init();
        } else {
            //child process
            SimpleGlideModule module = new SimpleGlideModule();
            GlideBuilder builder = new GlideBuilder();
            module.applyOptions(this, builder);
            Glide glide = builder.build(this);
            Glide.init(glide);
        }
    }

    private void initDb() {
        DatabaseManager manager = DatabaseManager.getInstance(this);
        manager.registerTable(new LikeTableImpl());
        manager.registerTable(new ClassPageTableImpl());
        manager.registerTable(new ClassPageListTableImpl());
        manager.registerTable(new DetailPageTableImpl());
        manager.registerTable(new DetailPageUrlsTableImpl());
        manager.registerTable(new HistoryTableImpl());
        manager.registerTable(new LikeTableImplV2());
        manager.getHelper(this, "heaven", 2).getWritableDatabase();
    }

    private void initSDK() {
//        CrashReport.initCrashReport(getApplicationContext(), "bea4125c41", true);
        Bugly.init(getApplicationContext(), "cf90f27bcb", false);
    }

    private void init() {

    }

    private static class SimpleGlideModule extends AppGlideModule {
        public void applyOptions(Context context, GlideBuilder builder) {
            //定义缓存大小为500M
            long diskCacheSize = Constants.DEFAULT_GLIDE_CACHE_SIZE;
            //提高图片质量
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            //自定义磁盘缓存:这种缓存只有自己的app才能访问到
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, (int) diskCacheSize));
            //Memory Cache
            builder.setMemoryCache(new LruResourceCache(24 * 1024 * 1024));
        }
    }

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public boolean isAppMainProcess(String packageName) {
        int pid = android.os.Process.myPid();
        String process = getAppNameByPID(this, pid);
        return packageName.equals(process);
    }

    /**
     * 根据Pid得到进程名
     */
    public String getAppNameByPID(Context context, int pid) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo processInfo : manager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return "";
    }

    /**
     * 程序是否在前台运行
     */
    public boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        /**
         * 获取Android设备中所有正在运行的App
         */
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName) && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }
        return false;
    }

}
