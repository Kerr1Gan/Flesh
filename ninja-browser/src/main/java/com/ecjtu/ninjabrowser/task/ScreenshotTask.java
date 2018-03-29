package com.ecjtu.ninjabrowser.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.ecjtu.ninjabrowser.R;
import com.ecjtu.ninjabrowser.unit.BrowserUnit;
import com.ecjtu.ninjabrowser.unit.ViewUnit;
import com.ecjtu.ninjabrowser.view.NinjaToast;
import com.ecjtu.ninjabrowser.view.NinjaWebView;

public class ScreenshotTask extends AsyncTask<Void, Void, Boolean> {
    private Context context;
    private ProgressDialog dialog;
    private NinjaWebView webView;
    private int windowWidth;
    private float contentHeight;
    private String title;
    private String path;

    public ScreenshotTask(Context context, NinjaWebView webView) {
        this.context = context;
        this.dialog = null;
        this.webView = webView;
        this.windowWidth = 0;
        this.contentHeight = 0f;
        this.title = null;
        this.path = null;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage(context.getString(R.string.toast_wait_a_minute));
        dialog.show();

        windowWidth = ViewUnit.getWindowWidth(context);
        contentHeight = webView.getContentHeight() * ViewUnit.getDensity(context);
        title = webView.getTitle();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Bitmap bitmap = ViewUnit.capture(webView, windowWidth, contentHeight, false, Bitmap.Config.ARGB_8888);
            path = BrowserUnit.screenshot(context, bitmap, title);
        } catch (Exception e) {
            path = null;
        }
        return path != null && !path.isEmpty();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.hide();
        dialog.dismiss();

        if (result) {
            NinjaToast.show(context, context.getString(R.string.toast_screenshot_successful) + path);
        } else {
            NinjaToast.show(context, R.string.toast_screenshot_failed);
        }
    }
}
