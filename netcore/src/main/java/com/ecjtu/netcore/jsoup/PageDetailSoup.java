package com.ecjtu.netcore.jsoup;

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
        Elements ele = body.getElementsByClass("pagenavi");
        Elements ss = ele.get(0).getElementsByTag("a");
        ss.get(ss.size() - 2);
        String url = ss.get(ss.size() - 2).attr("href");
        int maxLen = Integer.valueOf(url.substring(url.lastIndexOf("/") + 1));
        String imgUrl = body.getElementsByClass("main-image").get(0).getElementsByTag("img").attr("src");
        PageDetailModel model = new PageDetailModel((String) getArguments()[0]);
        model.setImgUrl(imgUrl);
        model.setMaxLen(maxLen);
        values.put(getClass().getSimpleName(), model);
    }
}
