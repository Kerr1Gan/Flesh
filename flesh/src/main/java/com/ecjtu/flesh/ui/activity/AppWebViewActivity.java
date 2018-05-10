package com.ecjtu.flesh.ui.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.ecjtu.componentes.TranslucentUtil;
import com.ecjtu.flesh.R;
import com.ecjtu.flesh.util.activity.ActivityUtil;
import com.ecjtu.flesh.util.admob.AdmobCallback;
import com.ecjtu.flesh.util.admob.AdmobManager;

/**
 * Created by Ethan_Xiang on 2018/3/28.
 */

public class AppWebViewActivity extends AppCompatActivity implements BrowserDelegate {
    private static final String TAG = "AppWebViewActivity";
    private static final String INTERFACE_NAME = "android";
    private WebView mWebView;
    private JavaScriptInterface mJsInterface;
    private ProgressBar mProgressBar;
    private AdmobManager mAdmobManager;
    private View mTop;
    private int mOriginStatusBarColor = Color.WHITE;
    private long mLastBackTime = -1;

    private View mCustomView;
    private int mOriginalOrientation;
    private FullscreenHolder mFullscreenHolder;
    private VideoView mVideoView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;

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
        findViewById(R.id.top).setPadding(0, TranslucentUtil.INSTANCE.getStatusBarHeight(this), 0, 0);
        mWebView = (WebView) findViewById(R.id.web_view);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true); // 支持视频全屏播放关键点
        settings.setSupportZoom(true); // 支持缩放,支持视频全屏播放关键点
        // settings.setAllowFileAccess(true); // 允许访问文件
        mJsInterface = new JavaScriptInterface(this);
        mWebView.addJavascriptInterface(mJsInterface, INTERFACE_NAME);
        mWebView.setWebViewClient(new SimpleWebViewClient());
        mWebView.setWebChromeClient(new SimpleWebChromeClient());

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mTop = findViewById(R.id.top);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            if (mLastBackTime < 0 || System.currentTimeMillis() - mLastBackTime > 3 * 1000) {
                mLastBackTime = System.currentTimeMillis();
                Toast.makeText(this, "再次点击将退出", Toast.LENGTH_SHORT).show();
            } else {
                super.onBackPressed();
            }
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
        if (mWebView != null) {
            mWebView.removeJavascriptInterface(INTERFACE_NAME);
            mWebView.setWebViewClient(null);
            mWebView.setWebChromeClient(null);
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }

    /**
     * 获取view的bitmap
     *
     * @param v
     * @return
     */
    public static Bitmap getBitmapFromView(View v) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        // Draw background
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(c);
        } else {
            c.drawColor(Color.WHITE);
        }
        // Draw view to canvas
        v.draw(c);
        return b;
    }

    public void setStatusBarColor(Bitmap bitmap) {
        if (null != bitmap) {
            int pixel = bitmap.getPixel(bitmap.getWidth() / 2, 5);
            //获取颜色
            int redValue = Color.red(pixel);
            int greenValue = Color.green(pixel);
            int blueValue = Color.blue(pixel);
            mTop.setBackgroundColor(pixel);
            bitmap.recycle();
        }
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
            mOriginStatusBarColor = Color.WHITE;
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            return super.shouldInterceptRequest(view, request);
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
                if (mWebView.getContentHeight() >= TranslucentUtil.INSTANCE.getStatusBarHeight(AppWebViewActivity.this)) {
                    if (mOriginStatusBarColor == Color.WHITE) {
                        Bitmap bitmap = getBitmapFromView(mWebView);
                        int pixel = bitmap.getPixel(bitmap.getWidth() / 2, 5);
                        if (pixel != mOriginStatusBarColor) {
                            mOriginStatusBarColor = pixel;
                            setStatusBarColor(bitmap);
                        } else {
                            bitmap.recycle();
                        }
                    }
                }
            }
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            AppWebViewActivity.this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {
            AppWebViewActivity.this.onHideCustomView();
        }
    }

    @Override
    public void updateAutoComplete() {

    }

    @Override
    public void updateBookmarks() {

    }

    @Override
    public void updateInputBox(String query) {

    }

    @Override
    public void updateProgress(int progress) {

    }

    @Override
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {

    }

    @Override
    public void showFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {

    }

    @Override
    public void onCreateView(WebView view, Message resultMsg) {

    }

    private void setCustomFullscreen(boolean fullscreen) {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        /*
         * Can not use View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION,
         * so we can not hide NavigationBar :(
         */
        int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        if (fullscreen) {
            layoutParams.flags |= bits;
        } else {
            layoutParams.flags &= ~bits;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        getWindow().setAttributes(layoutParams);
    }

    private class VideoCompletionListener implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            return false;
        }

        @Override
        public void onCompletion(MediaPlayer mp) {
            onHideCustomView();
        }
    }

    @Override
    public boolean onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (view == null) {
            return false;
        }
        if (mCustomView != null && callback != null) {
            callback.onCustomViewHidden();
            return false;
        }

        mCustomView = view;
        mOriginalOrientation = getRequestedOrientation();

        mFullscreenHolder = new FullscreenHolder(this);
        mFullscreenHolder.addView(
                mCustomView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        decorView.addView(
                mFullscreenHolder,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                ));

        mCustomView.setKeepScreenOn(true);
        setCustomFullscreen(true);

        if (view instanceof FrameLayout) {
            if (((FrameLayout) view).getFocusedChild() instanceof VideoView) {
                mVideoView = (VideoView) ((FrameLayout) view).getFocusedChild();
                mVideoView.setOnErrorListener(new VideoCompletionListener());
                mVideoView.setOnCompletionListener(new VideoCompletionListener());
            }
        }
        mCustomViewCallback = callback;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Auto landscape when video shows
        return true;
    }

    @Override
    public boolean onHideCustomView() {
        if (mCustomView == null || mCustomViewCallback == null) {
            return false;
        }

        FrameLayout decorView = (FrameLayout) getWindow().getDecorView();
        if (decorView != null) {
            decorView.removeView(mFullscreenHolder);
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            try {
                mCustomViewCallback.onCustomViewHidden();
            } catch (Throwable t) {
            }
        }

        mCustomView.setKeepScreenOn(false);
        setCustomFullscreen(false);

        mFullscreenHolder = null;
        mCustomView = null;
        if (mVideoView != null) {
            mVideoView.setOnErrorListener(null);
            mVideoView.setOnCompletionListener(null);
            mVideoView = null;
        }
        setRequestedOrientation(mOriginalOrientation);

        return true;
    }

    @Override
    public void onLongPress(String url) {

    }
}

class FullscreenHolder extends FrameLayout {
    public FullscreenHolder(Context context) {
        super(context);
        this.setBackgroundColor(context.getResources().getColor(android.R.color.black));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}

interface BrowserDelegate {
    void updateAutoComplete();

    void updateBookmarks();

    void updateInputBox(String query);

    void updateProgress(int progress);

    void openFileChooser(ValueCallback<Uri> uploadMsg);

    void showFileChooser(ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams);

    void onCreateView(WebView view, Message resultMsg);

    boolean onShowCustomView(View view, WebChromeClient.CustomViewCallback callback);

    boolean onHideCustomView();

    void onLongPress(String url);
}
