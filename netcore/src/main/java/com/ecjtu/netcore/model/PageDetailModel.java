package com.ecjtu.netcore.model;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageDetailModel {
    String baseUrl;
    int maxLen;
    String imgUrl;

    public PageDetailModel(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
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
}
