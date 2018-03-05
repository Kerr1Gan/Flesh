package com.ecjtu.flesh.model.models;

import android.os.Parcel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public static Map<String, List<MeiPaiModel>> getMeiPaiModelByJsonString(String json) {
        try {
            JSONArray jArray = new JSONArray(json);
            Map<String, List<MeiPaiModel>> ret = new LinkedHashMap<>();
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObj = jArray.getJSONObject(i);
                String key = jObj.optString("key", "");
                JSONArray array = jObj.optJSONArray("array");
                List<MeiPaiModel> list = new ArrayList<>();
                for (int j = 0; j < array.length(); j++) {
                    JSONObject obj = array.getJSONObject(j);
                    MeiPaiModel model = new MeiPaiModel();
                    model.setHref(obj.optString("href", ""));
                    model.setImgUrl(obj.optString("imgURl", ""));
                    model.setTitle(obj.optString("title", ""));
                    model.setVideoUrl(obj.optString("videoUrl", ""));
                    model.setVideoImageUrl(obj.optString("videoImageUrl", ""));
                    list.add(model);
                }
                ret.put(key, list);
            }
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
