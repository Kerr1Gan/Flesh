package com.ecjtu.heaven;

import android.app.Application;
import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

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


    }

    private static class SimpleGlideModule extends AppGlideModule {
        public void applyOptions(Context context, GlideBuilder builder) {
            //定义缓存大小为100M
            int diskCacheSize = 100 * 1024 * 1024;

            //自定义缓存 路径 和 缓存大小
//            val diskCachePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/glideCache"

            //提高图片质量
            builder.setDecodeFormat(DecodeFormat.PREFER_ARGB_8888);

            //自定义磁盘缓存:这种缓存只有自己的app才能访问到
            builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSize));
            // builder.setDiskCache( new InternalCacheDiskCacheFactory( context , diskCachePath , diskCacheSize  )) ;
            //自定义磁盘缓存：这种缓存存在SD卡上，所有的应用都可以访问到
//            builder.setDiskCache(DiskLruCacheFactory(diskCachePath, diskCacheSize))

            //Memory Cache
            builder.setMemoryCache(new LruResourceCache(24 * 1024 * 1024));
        }
    }
}
