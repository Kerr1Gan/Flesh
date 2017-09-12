package com.ecjtu.heaven.cache;

import android.os.Parcel;

import com.ecjtu.heaven.cache.base.ParcelableFileCacheHelper;
import com.ecjtu.netcore.model.MenuModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/12.
 */

public class MenuListCacheHelper extends ParcelableFileCacheHelper {

    public MenuListCacheHelper(String path) {
        super(path);
    }

    @Override
    protected <T> T readParcel(Parcel parcel) {
        int len = parcel.readInt();
        List<MenuModel> ret = new ArrayList<>();
        for(int i=0;i<len;i++){
            MenuModel model = new MenuModel(parcel.readString(),parcel.readString());
            ret.add(model);
        }
        return (T) ret;
    }

    @Override
    protected <T> Parcel writeParcel(Parcel parcel, T object) {
        if(!(object instanceof List)){
            return null;
        }
        List<MenuModel> list = (List<MenuModel>) object;
        int len = list.size();
        parcel.writeInt(len);
        for(int i=0;i<len;i++){
            MenuModel model = list.get(i);
            parcel.writeString(model.getTitle());
            parcel.writeString(model.getUrl());
        }
        return parcel;
    }
}
