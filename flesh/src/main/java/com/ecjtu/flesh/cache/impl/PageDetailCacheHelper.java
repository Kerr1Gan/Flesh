package com.ecjtu.flesh.cache.impl;

import android.os.Parcel;

import com.ecjtu.netcore.model.PageDetailModel;
import com.ecjtu.parcel.base.ParcelableFileCacheHelper;

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */

public class PageDetailCacheHelper extends ParcelableFileCacheHelper {
    public PageDetailCacheHelper(String path) {
        super(path);
    }

    @Override
    public <T> T readParcel(Parcel parcel) {
        String baseUrl = parcel.readString();
        int maxLen = parcel.readInt();
        String imgUrl = parcel.readString();
        PageDetailModel model = new PageDetailModel(baseUrl);

        model.setMaxLen(maxLen);
        model.setImgUrl(imgUrl);
        return (T) model;
    }

    @Override
    public <T> Parcel writeParcel(Parcel parcel, T object) {
        if (!(object instanceof PageDetailModel)) {
            return null;
        }
        PageDetailModel local = (PageDetailModel) object;
        parcel.writeString(local.getBaseUrl());
        parcel.writeInt(local.getMaxLen());
        parcel.writeString(local.getImgUrl());
        return parcel;
    }
}
