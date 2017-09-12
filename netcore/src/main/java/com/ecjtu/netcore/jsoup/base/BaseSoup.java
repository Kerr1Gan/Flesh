package com.ecjtu.netcore.jsoup.base;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ethan_Xiang on 2017/9/7.
 */

public abstract class BaseSoup implements ISoup {

    private String mHtml;

    private Map<String, Object> mValues;

    protected Element mHeader;

    protected Element mBody;

    private Object[] mArguments;

    public BaseSoup(String html) {
        mHtml = html;
    }

    public Map<String, Object> doParse(Object... arg) {
        mArguments = arg;
        if (mValues == null) {
            mValues = new HashMap<>();
        }
        Document doc = Jsoup.parse(mHtml);
        mHeader = doc.head();
        mBody = doc.body();
        parse(doc, mHeader, mBody, mValues);
        return mValues;
    }

    public Map<String, Object> getValues() {
        return mValues;
    }

    public Object[] getArguments(){
        return mArguments;
    }
}
