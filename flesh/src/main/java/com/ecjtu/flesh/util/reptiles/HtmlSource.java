package com.ecjtu.flesh.util.reptiles;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by KerriGan on 2016/10/22.
 */
public class HtmlSource {
    //maybe htmlunit better
    private WebView mWebView;
    private String mHtml;
    private ICallback mListener;
    private List<ICallback> mListeners = new ArrayList<>();

    private HandlerThread mHandlerThread = new HandlerThread("HtmlSource");
    private Handler mHandler;

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    public HtmlSource(Context context) {
        mWebView = new WebView(context.getApplicationContext());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        mWebView.setWebViewClient(new SimpleWebViewClient());
        mWebView.setVisibility(View.GONE);

        mHandlerThread.start();
        mHandler = new WorkHandler(mHandlerThread.getLooper());
    }

    public void loadExeJsHtml(String url, ICallback listener) {
        setCallbackListener(listener);
        loadExeJsHtml(url);
    }

    public void loadExeJsHtml(String url) {
        mWebView.loadUrl(url);
    }

    public void setCallbackListener(ICallback listener) {
        mListener = listener;
    }

    /**
     * just invoke once ,will release after one callback
     */
    public void addCallbackListeners(ICallback listener) {
        mListeners.add(listener);
    }

    public String getLatestHtml() {
        return mHtml;
    }

    public int getProgress() {
        return mWebView.getProgress();
    }

    private class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mListener != null) {
                mListener.afterJsHtml(mHtml);
            }
            for (ICallback list : mListeners) {
                list.afterJsHtml(mHtml);
            }
            //release listeners
            mListeners.clear();
        }
    }

    public void clearTask() {
        mHandler.removeMessages(0, null);
    }


    public void release() {
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
        if (mWebView != null) {
            mWebView.removeJavascriptInterface("local_obj");
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();
            if (mWebView.getParent() != null) {
                ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            }
            mWebView.destroy();
            mWebView = null;
        }
    }

    final class SimpleWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("HtmlSource", "onPageStarted");
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            Log.d("HtmlSource", "onPageFinished ");
            view.loadUrl("javascript:window.local_obj.showSource(" +
                    "'<html>'+" +
                    "document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            super.onPageFinished(view, url);
        }
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(String html) {
            mHtml = html;
            if (mHandler != null) {
                try {
                    mHandler.obtainMessage().sendToTarget();
                } catch (Exception e) {
                }
            }

        }
    }

    public interface ICallback {
        /**
         * not in ui thread
         */
        void afterJsHtml(String html);
    }
}