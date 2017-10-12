package com.ecjtu.flesh.cache.impl;

import android.os.Parcel;

import com.ecjtu.netcore.model.PageModel;
import com.ecjtu.parcel.base.ParcelableFileCacheHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/11.
 */

public class PageListCacheHelper extends ParcelableFileCacheHelper {
    public PageListCacheHelper(String path) {
        super(path);
    }

    @Override
    protected <T> T readParcel(Parcel parcel) {
        PageModel pageModel = new PageModel();
        String nextPage = parcel.readString();
        int id = parcel.readInt();
        pageModel.setId(id);
        pageModel.setNextPage(nextPage);
        int len = parcel.readInt();
        List<PageModel.ItemModel> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            String href = parcel.readString();
            String description = parcel.readString();
            String imageUrl = parcel.readString();
            PageModel.ItemModel item = new PageModel.ItemModel(href, description, imageUrl);
            item.setId(parcel.readInt());
            item.setHeight(parcel.readInt());
            list.add(item);
        }
        pageModel.setItemList(list);
        return (T) pageModel;
    }

    @Override
    protected <T> Parcel writeParcel(Parcel parcel, T object) {
        if (!(object instanceof PageModel)) {
            return null;
        }
        PageModel local = (PageModel) object;
        parcel.writeString(local.getNextPage());
        parcel.writeInt(local.getId());
        int len = local.getItemList().size();
        parcel.writeInt(local.getItemList().size());
        for (int i = 0; i < len; i++) {
            PageModel.ItemModel item = local.getItemList().get(i);
            parcel.writeString(item.getHref());
            parcel.writeString(item.getDescription());
            parcel.writeString(item.getImgUrl());
            parcel.writeInt(item.getId());
            parcel.writeInt(item.getHeight());
        }
        return parcel;
    }

    @Override
    public int getVersion() {
        return super.getVersion();
    }
}
