package com.ecjtu.heaven.cache;

import android.os.Parcel;

import com.ecjtu.netcore.model.PageModel;

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
    <T> T readParcel(Parcel parcel) {
        PageModel pageModel = new PageModel();
        String nextPage = parcel.readString();
        pageModel.setNextPage(nextPage);
        int len = parcel.readInt();
        List<PageModel.ItemModel> list = new ArrayList<>();
        for (int i = 0; i < len; i++) {
            String href = parcel.readString();
            String description = parcel.readString();
            String imageUrl = parcel.readString();
            PageModel.ItemModel item = new PageModel.ItemModel(href, description, imageUrl);
            list.add(item);
        }
        pageModel.setItemList(list);
        return (T) pageModel;
    }

    @Override
    <T> Parcel writeParcel(Parcel parcel, T object) {
        if (!(object instanceof PageModel)) {
            return null;
        }
        PageModel local = (PageModel) object;
        parcel.writeString(local.getNextPage());
        int len = local.getItemList().size();
        parcel.writeInt(local.getItemList().size());
        for (int i = 0; i < len; i++) {
            PageModel.ItemModel item = local.getItemList().get(i);
            parcel.writeString(item.getHref());
            parcel.writeString(item.getDescription());
            parcel.writeString(item.getImgUrl());
        }
        return parcel;
    }
}
