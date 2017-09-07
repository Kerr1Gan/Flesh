package com.ecjtu.netcore.model;

import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageModel {
    List<ItemModel> itemList;
    int maxLen;

    public PageModel(List<ItemModel> itemList) {
        this.itemList = itemList;
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public List<ItemModel> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemModel> itemList) {
        this.itemList = itemList;
    }

    public static class ItemModel {
        String href;
        String description;
        String imgUrl;

        public ItemModel(String href, String description, String imgUrl) {
            this.href = href;
            this.description = description;
            this.imgUrl = imgUrl;
        }

        public String getHref() {
            return href;
        }

        public void setHref(String href) {
            this.href = href;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }

    }
}
