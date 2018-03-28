package com.ecjtu.flesh.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ecjtu.componentes.TranslucentUtil;
import com.ecjtu.flesh.R;
import com.ecjtu.flesh.util.activity.ActivityUtil;
import com.ecjtu.flesh.util.admob.AdmobCallback;
import com.ecjtu.flesh.util.admob.AdmobManager;

/**
 * Created by Ethan_Xiang on 2018/3/28.
 */

public class AppWebViewActivity extends AppCompatActivity {

    private static final String INTERFACE_NAME = "android";
    private WebView mWebView;
    private JavaScriptInterface mJsInterface;
    private ProgressBar mProgressBar;
    private AdmobManager mAdmobManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TranslucentUtil.INSTANCE.translucentWindow(this);
        setContentView(R.layout.activity_app_web_view);
        initView();
        if (getIntent() != null) {
            String url = getIntent().getStringExtra("url");
            if (!TextUtils.isEmpty(url)) {
                mWebView.loadUrl(url);
            } else {
                finish();
            }
        } else {
            finish();
        }
        mAdmobManager = new AdmobManager(this);
        mAdmobManager.loadInterstitialAd(getString(R.string.admob_chaye), new AdmobCallback() {
            @Override
            public void onLoaded() {
                mAdmobManager.getLatestInterstitialAd().show();
            }

            @Override
            public void onError(int errorCode) {
            }

            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });
    }

    @SuppressLint("JavascriptInterface")
    private void initView() {
        mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new SimpleWebViewClient());
        mWebView.setWebChromeClient(new SimpleWebChromeClient());

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);

        mJsInterface = new JavaScriptInterface(this);
        mWebView.addJavascriptInterface(mJsInterface, INTERFACE_NAME);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        mAdmobManager.onResume();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mAdmobManager.onPause();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mAdmobManager.onDestroy();
        super.onDestroy();
    }

    public static class JavaScriptInterface {
        private Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;

        }

        @JavascriptInterface
        public void gotoAppDetailSettings() {
            context.startActivity(ActivityUtil.INSTANCE.getAppDetailSettingIntent(context));
        }
    }

    public class SimpleWebViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    class SimpleWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);// 加载完网页进度条消失
            } else {
                mProgressBar.setVisibility(View.VISIBLE);// 开始加载网页时显示进度条
                mProgressBar.setProgress(newProgress);// 设置进度值
            }
        }
    }
}
