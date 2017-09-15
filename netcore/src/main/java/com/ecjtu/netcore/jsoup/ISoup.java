package com.ecjtu.netcore.jsoup;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public interface ISoup {
    void parse(Document root, Element head, Element body, Map<String, Object> values);

    Map<String, Object> doParse(Object ...arg);
}
