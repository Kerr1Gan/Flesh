package com.ecjtu.flesh.model.models;

import android.os.Parcel;

import java.io.Serializable;

/**
 * Created by xiang on 2018/2/8.
 */

public class MeiPaiModel extends VideoModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String title;
    private String videoImageUrl;
    private String videoUrl;
    private String imgUrl;
    private String href;

    public MeiPaiModel() {
    }

    protected MeiPaiModel(Parcel in) {
        super(in);
        title = in.readString();
        videoImageUrl = in.readString();
        videoUrl = in.readString();
        imgUrl = in.readString();
        href = in.readString();
    }

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(title);
        dest.writeString(videoImageUrl);
        dest.writeString(videoUrl);
        dest.writeString(imgUrl);
        dest.writeString(href);
    }

    public static final Creator<MeiPaiModel> CREATOR = new Creator<MeiPaiModel>() {
        @Override
        public MeiPaiModel createFromParcel(Parcel in) {
            return new MeiPaiModel(in);
        }

        @Override
        public MeiPaiModel[] newArray(int size) {
            return new MeiPaiModel[size];
        }
    };
}
