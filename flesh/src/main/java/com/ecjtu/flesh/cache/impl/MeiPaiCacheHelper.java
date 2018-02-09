package com.ecjtu.flesh.cache.impl;

import android.os.Parcel;

import com.ecjtu.flesh.model.models.MeiPaiModel;
import com.ecjtu.parcel.base.ParcelableFileCacheHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2018/2/9.
 */

public class MeiPaiCacheHelper extends ParcelableFileCacheHelper {

    public MeiPaiCacheHelper(String path) {
        super(path);
    }

    @Override
    public <T> T readParcel(Parcel parcel) {
        Map<String, List<MeiPaiModel>> out = new LinkedHashMap<>();
        parcel.readMap(out, getClass().getClassLoader());
        return (T) out;
    }

    @Override
    public <T> Parcel writeParcel(Parcel parcel, T object) {
        if (!(object instanceof Map)) {
            return null;
        }
        Map<String, List<MeiPaiModel>> local = (Map<String, List<MeiPaiModel>>) object;
        parcel.writeMap(local);
        return parcel;
    }
}
