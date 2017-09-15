package com.ecjtu.netcore.jsoup.impl;

import com.ecjtu.netcore.jsoup.BaseSoup;
import com.ecjtu.netcore.model.PageDetailModel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageDetailSoup extends BaseSoup {
    public static final String TAG = "PageDetailSoup";

    public PageDetailSoup(String html) {
        super(html);
    }

    @Override
    public void parse(Document root, Element head, Element body, Map<String, Object> values) {
        Elements elements = body.getElementsByClass("prev-next-page");
        String text = elements.get(0).text();
        text = text.substring(text.indexOf("/") + 1);
        int maxPage = Integer.valueOf(text.replace("页", ""));
        elements = body.getElementsByTag("figure");
        elements = elements.get(0).getElementsByTag("img");
        String imageUrl = elements.get(0).attr("src");
        String originImageUrl = imageUrl;
        PageDetailModel model = new PageDetailModel((String) getArguments()[0]);
        String suffix = imageUrl.substring(imageUrl.lastIndexOf("."));
        imageUrl = imageUrl.substring(0,imageUrl.lastIndexOf("."));
        imageUrl = imageUrl.substring(0,imageUrl.length()-2);
        imageUrl += "%02d";
        imageUrl += suffix;
        model.setImgUrl(imageUrl);
        model.setMaxLen(maxPage);
        values.put(getClass().getSimpleName(), model);
        values.put("origin_img",originImageUrl);
    }
}
