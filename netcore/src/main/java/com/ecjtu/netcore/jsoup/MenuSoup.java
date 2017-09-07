package com.ecjtu.netcore.jsoup;

import com.ecjtu.netcore.model.MenuModel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class MenuSoup extends BaseSoup {
    private static final String TAG = "MenuSoup";

    public MenuSoup(String html) {
        super(html);
    }

    @Override
    public void parse(Document root, Element head,Element body, Map<String, Object> values) {
        Element set = body.getElementById("menu-nav");
        Elements childArr = set.getElementsByTag("a");
        List<MenuModel> models = new ArrayList<>();
        for (Element ele : childArr) {
            String title = ele.attr("title");
            String url = ele.attr("href");
            if (!title.equalsIgnoreCase("首页")) {
                MenuModel model = new MenuModel(title, url);
                models.add(model);
            }
        }
        values.put(getClass().getSimpleName(), models);
    }
}
