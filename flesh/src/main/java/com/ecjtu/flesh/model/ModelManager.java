package com.ecjtu.flesh.model;

import com.ecjtu.flesh.model.models.NotificationModel;

/**
 * Created by Ethan_Xiang on 2017/9/22.
 */

public class ModelManager {

    public static NotificationModel getNotificationModel(int id,String title,String content,int limit,String time,String timeLimit,String actionDetailUrl){
        NotificationModel model = new NotificationModel();
        model.setId(id);
        model.setTitle(title);
        model.setContent(content);
        model.setLimit(limit);
        model.setTime(time);
        model.setTimeLimit(timeLimit);
        model.setActionDetailUrl(actionDetailUrl);
        return model;
    }


}
