package com.ecjtu.flesh.cache.impl;

import android.os.Parcel;

import com.ecjtu.flesh.model.models.VideoModel;
import com.ecjtu.parcel.base.ParcelableFileCacheHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2018/1/26.
 */

public class V33CacheHelper extends ParcelableFileCacheHelper {

    public V33CacheHelper(String path) {
        super(path);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readParcel(Parcel parcel) {
        Map<String, List<VideoModel>> out = new LinkedHashMap<>();
        parcel.readMap(out, getClass().getClassLoader());
        return (T) out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Parcel writeParcel(Parcel parcel, T object) {
        if (!(object instanceof Map)) {
            return null;
        }
        Map<String, List<VideoModel>> local = (Map<String, List<VideoModel>>) object;
        parcel.writeMap(local);
        return parcel;
    }
}
