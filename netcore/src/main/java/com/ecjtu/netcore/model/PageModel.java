package com.ecjtu.netcore.model;

import java.util.List;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageModel {
    List<ItemModel> itemList;
    String nextPage;

    public PageModel(List<ItemModel> itemList) {
        this.itemList = itemList;
    }

    public PageModel(){
    }

    public List<ItemModel> getItemList() {
        return itemList;
    }

    public void setItemList(List<ItemModel> itemList) {
        this.itemList = itemList;
    }


    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
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

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof ItemModel)){
                return false;
            }
            ItemModel other = (ItemModel) o;
            return other.href.equals(this.href);
        }
    }
}
