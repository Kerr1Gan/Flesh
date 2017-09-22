package com.ecjtu.flesh.model;

import com.ecjtu.flesh.model.models.NotificationModel;

/**
 * Created by Ethan_Xiang on 2017/9/22.
 */

public class ModelManager {

    public static NotificationModel getNotificationModel(int id, String title, String content, String ticker, int limit, String time, String timeLimit, String actionDetailUrl, String h5Page) {
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
        return model;
    }


}
