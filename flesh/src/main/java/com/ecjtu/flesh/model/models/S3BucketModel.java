package com.ecjtu.flesh.model.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ethan_Xiang on 2018/3/5.
 */

public class S3BucketModel {

    private String title;
    private String bucketName;

    public S3BucketModel(String title, String bucketName) {
        this.title = title;
        this.bucketName = bucketName;
    }

    public String getTitle() {
        return title;
    }

    public String getBucketName() {
        return bucketName;
    }

    public static List<S3BucketModel> fromJson(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);
            List<S3BucketModel> modelList = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jObj = jsonArray.getJSONObject(i);
                String title = jObj.optString("title");
                String bucketName = jObj.optString("bucketName");
                S3BucketModel model = new S3BucketModel(title, bucketName);
                modelList.add(model);
            }
            return modelList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
