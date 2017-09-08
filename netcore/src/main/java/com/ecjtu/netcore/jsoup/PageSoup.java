package com.ecjtu.netcore.jsoup;

import com.ecjtu.netcore.model.PageModel;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public class PageSoup extends MenuSoup {
    private static final String TAG = "PageSoup";

    public PageSoup(String html) {
        super(html);
    }

    @Override
    public void parse(Document root, Element head, Element body, Map<String, Object> values) {
        super.parse(root, head, body, values);
        Element child = body.getElementById("pins");
        Elements children = child.getElementsByTag("li");
        List<PageModel.ItemModel> value = new ArrayList<>();
        PageModel pageModel = new PageModel(value);
        for (Element localChild : children) {
            List<Node> nodes = localChild.childNodes();
            Node item =nodes.get(0);
            String href = item.attr("href");
            String des = item.childNode(0).attr("alt");
            String imgUrl = item.childNode(0).attr("data-original");
            PageModel.ItemModel model = new PageModel.ItemModel(href, des, imgUrl);
            value.add(model);
        }
        values.put(getClass().getSimpleName(), pageModel);

        Elements links = body.getElementsByClass("nav-links");
        links=links.get(0).getElementsByTag("a");
        String url = links.get(links.size() - 2).attr("href");
        url= url.substring(0,url.length()-1);
        int maxLen = Integer.valueOf(url.substring(url.lastIndexOf("/")+1));
        pageModel.setMaxPage(maxLen);
    }
}
