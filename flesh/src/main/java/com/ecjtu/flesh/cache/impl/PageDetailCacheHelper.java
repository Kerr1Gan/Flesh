package com.ecjtu.flesh.cache.impl;

import android.os.Parcel;

import com.ecjtu.flesh.cache.base.ParcelableFileCacheHelper;
import com.ecjtu.netcore.model.PageDetailModel;

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */

public class PageDetailCacheHelper extends ParcelableFileCacheHelper {
    public PageDetailCacheHelper(String path) {
        super(path);
    }

    @Override
    protected <T> T readParcel(Parcel parcel) {
        String baseUrl = parcel.readString();
        int maxLen = parcel.readInt();
        String imgUrl = parcel.readString();
        PageDetailModel model = new PageDetailModel(baseUrl);

        model.setMaxLen(maxLen);
        model.setImgUrl(imgUrl);
        return (T) model;
    }

    @Override
    protected <T> Parcel writeParcel(Parcel parcel, T object) {
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
