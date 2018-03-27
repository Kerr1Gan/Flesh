package com.ecjtu.netcore.model;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class MenuModel {
    int id;
    String title;
    String url;

    public MenuModel(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuModel)) {
            return false;
        }

        MenuModel local = (MenuModel) o;
        if (url.equals(local.url) && title.equals(local.title) && id == local.id) {
            return true;
        } else {
            return false;
        }
    }
}
