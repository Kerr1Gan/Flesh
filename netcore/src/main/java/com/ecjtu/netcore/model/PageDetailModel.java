package com.ecjtu.netcore.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageDetailModel {
    int id;
    String baseUrl;
    int maxLen;
    String imgUrl;
    List<String> backupImgUrl;
    int type; // 0 is image,1 is video

    public PageDetailModel(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
        String[] temp = null;
        if (backupImgUrl != null) {
            temp = backupImgUrl.toArray(new String[]{});
        }
        backupImgUrl = new ArrayList<>();
        for (int i = 0; i < maxLen; i++) {
            backupImgUrl.add("");
        }
        if (temp != null) {
            for (int i = 0; i < temp.length; i++) {
                backupImgUrl.set(i, temp[i]);
            }
        }
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public List<String> getBackupImgUrl() {
        return backupImgUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setBackupImgUrl(List<String> backupImgUrl) {
        this.backupImgUrl = backupImgUrl;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
