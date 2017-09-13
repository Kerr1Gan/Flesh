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
import com.ecjtu.heaven.db.table.impl.LikeTableImpl;

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
    }

    private void initDb() {
        DatabaseManager manager = DatabaseManager.getInstance(this);
        manager.registerTable(new LikeTableImpl());
        SQLiteDatabase db = manager.getHelper(this, "test", 1).getWritableDatabase();
        db.close();
    }

    private static class SimpleGlideModule extends AppGlideModule {
        public void applyOptions(Context context, GlideBuilder builder) {
            //定义缓存大小为100M
            int diskCacheSize = 100 * 1024 * 1024;
            //提高图片质量
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);
            //自定义磁盘缓存:这种缓存只有自己的app才能访问到
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSize));
            //Memory Cache
            builder.setMemoryCache(new LruResourceCache(24 * 1024 * 1024));
        }
    }
}
