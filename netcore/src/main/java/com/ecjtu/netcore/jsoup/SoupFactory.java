package com.ecjtu.netcore.jsoup;

import com.ecjtu.netcore.jsoup.BaseSoup;

import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class SoupFactory {

    public static Map<String, Object> parseHtml(Class<? extends BaseSoup> clazz, String html) {
        return parseHtml(clazz, html, new Object());
    }

    public static Map<String, Object> parseHtml(Class<? extends BaseSoup> clazz, String html, Object... arg) {
        try {
            BaseSoup soup = clazz.getConstructor(String.class).newInstance(html);
            return soup.doParse(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
