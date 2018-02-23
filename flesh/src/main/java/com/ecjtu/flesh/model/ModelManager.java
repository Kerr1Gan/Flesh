package com.ecjtu.flesh.model;

import com.ecjtu.flesh.model.models.MeiPaiModel;
import com.ecjtu.flesh.model.models.NotificationModel;
import com.ecjtu.flesh.model.models.V33VideoModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/22.
 */

public class ModelManager {

    /**
     * @param id
     * @param title
     * @param content
     * @param ticker
     * @param limit
     * @param time
     * @param timeLimit
     * @param actionDetailUrl
     * @param h5Page
     * @param type            0 is anli page, 1 is h5 page
     * @return
     */
    public static NotificationModel getNotificationModel(int id, String title, String content, String ticker, int limit, String time, String timeLimit, String actionDetailUrl, String h5Page, int type) {
        NotificationModel model = new NotificationModel();
        model.setId(id);
        model.setTitle(title);
        model.setContent(content);
        model.setTicker(ticker);
        model.setLimit(limit);
        model.setTime(time);
        model.setTimeLimit(timeLimit);
        model.setActionDetailUrl(actionDetailUrl);
        model.setH5Page(h5Page);
        model.setType(type);
        return model;
    }

    public static List<V33VideoModel> getVideoModelByJsonString(String json) {
        try {
            JSONObject root = new JSONObject(json);
            JSONArray arr = root.getJSONArray("data");
            List<V33VideoModel> ret = new ArrayList<>();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                V33VideoModel model = new V33VideoModel();
                model.setK(obj.optString("k"));
                model.setId(obj.optString("id"));
                model.setTitle(obj.optString("title"));
                model.setPic_url(obj.optString("pic_url"));
                model.setCishu(obj.optString("cishu"));
                model.setMember(obj.optString("member"));
                model.setSource(obj.optString("source"));
                model.setAddtime(obj.optString("addtime"));
                model.setHits(obj.optString("hits"));
                ret.add(model);
            }
            return ret;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

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
