package com.ecjtu.flesh.model.models;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by xiang on 2018/2/8.
 */

public class MeiPaiModel extends V33Model implements Serializable , Parcelable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String videoImageUrl;
    private String videoUrl;
    private String imgUrl;
    private String href;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoImageUrl() {
        return videoImageUrl;
    }

    public void setVideoImageUrl(String videoImageUrl) {
        this.videoImageUrl = videoImageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
