package com.ecjtu.flesh.model.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

public class V33Model implements Serializable, Parcelable {
    private String baseUrl;
    private String imageUrl;
    private String title;
    private List<String> others;
    private String videoUrl;

    public V33Model() {
    }

    protected V33Model(Parcel in) {
        baseUrl = in.readString();
        imageUrl = in.readString();
        title = in.readString();
        others = in.createStringArrayList();
        videoUrl = in.readString();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getOthers() {
        return others;
    }

    public void setOthers(List<String> others) {
        this.others = others;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(baseUrl);
        dest.writeString(imageUrl);
        dest.writeString(title);
        dest.writeStringList(others);
        dest.writeString(videoUrl);
    }

    public static final Creator<V33Model> CREATOR = new Creator<V33Model>() {
        @Override
        public V33Model createFromParcel(Parcel in) {
            return new V33Model(in);
        }

        @Override
        public V33Model[] newArray(int size) {
            return new V33Model[size];
        }
    };
}