package com.ecjtu.netcore.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageDetailModel {
    String baseUrl;
    int maxLen;
    String imgUrl;
    List<String> backupImgUrl;

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
}
