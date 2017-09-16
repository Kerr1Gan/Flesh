package com.ecjtu.heaven;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import com.ecjtu.heaven.db.DatabaseManager;
import com.ecjtu.heaven.db.table.impl.ClassPageTableImpl;
import com.ecjtu.heaven.db.table.impl.DetailPageTableImpl;
import com.ecjtu.heaven.db.table.impl.DetailPageUrlsTableImpl;
import com.ecjtu.heaven.db.table.impl.HistoryTableImpl;
import com.ecjtu.heaven.db.table.impl.LikeTableImpl;
import com.ecjtu.heaven.db.table.impl.LikeTableImplV2;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SimpleGlideModule module = new SimpleGlideModule();
        GlideBuilder builder = new GlideBuilder();
        module.applyOptions(this, builder);
        Glide glide = builder.build(this);
        Glide.init(glide);

        initDb();
        initSDK();
    }

    private void initDb() {
        DatabaseManager manager = DatabaseManager.getInstance(this);
        manager.registerTable(new LikeTableImpl());
        manager.registerTable(new ClassPageTableImpl());
        manager.registerTable(new DetailPageTableImpl());
        manager.registerTable(new DetailPageUrlsTableImpl());
        manager.registerTable(new HistoryTableImpl());
        manager.registerTable(new LikeTableImplV2());
        manager.getHelper(this,"heaven",2).getWritableDatabase();
    }

    private void initSDK(){
//        CrashReport.initCrashReport(getApplicationContext(), "bea4125c41", true);
        Bugly.init(getApplicationContext(), "bea4125c41", false);
    }

    private static class SimpleGlideModule extends AppGlideModule {
        public void applyOptions(Context context, GlideBuilder builder) {
            //定义缓存大小为500M
            int diskCacheSize = 500 * 1024 * 1024;
            //提高图片质量
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            //自定义磁盘缓存:这种缓存只有自己的app才能访问到
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSize));
            //Memory Cache
            builder.setMemoryCache(new LruResourceCache(24 * 1024 * 1024));
        }
    }
}
