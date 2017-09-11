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
        Element child = body.getElementById("content");
        Elements children = child.getElementsByTag("figure");
        List<PageModel.ItemModel> value = new ArrayList<>();
        PageModel pageModel = new PageModel(value);
        for (Element localChild : children) {
            List<Node> nodes = localChild.childNodes();
            Node item = nodes.get(1);
            String href = item.attr("href");
            String des = item.attr("title");
            String imgUrl = item.childNode(0).attr("data-original");
            PageModel.ItemModel model = new PageModel.ItemModel(href, des, imgUrl);
            value.add(model);
        }
        values.put(getClass().getSimpleName(), pageModel);

        Element element = root.getElementById("pagebtn");
        Elements elements = element.getElementsByTag("a");
        for (int i = 0; i < elements.size(); i++) {
            Element ele = elements.get(i);
            if (ele.text().contains("下一页")) {
                String nextUrl = ele.attr("href");
                pageModel.setNextPage(nextUrl);
                break;
            }
        }
    }
}
