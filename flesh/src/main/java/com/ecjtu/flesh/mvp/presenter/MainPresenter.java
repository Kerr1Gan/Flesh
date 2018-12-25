package com.ecjtu.flesh.mvp.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ecjtu.componentes.activity.AppThemeActivity;
import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.userinterface.fragment.SearchFragment;
import com.ecjtu.flesh.util.CloseableUtil;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;
import com.ecjtu.netcore.network.IRequestCallbackV2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

public class MainPresenter implements MainContract.Presenter {

    private MainContract.View view;

    @Override
    public void onStop() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void checkZero(final Activity act, final MainContract.View view) {
        AsyncNetwork request = new AsyncNetwork();
        request.setDoInputOutput(true, false);
        request.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean zero = json.getBoolean("zero");
                    PreferenceManager.getDefaultSharedPreferences(act).edit()
                            .putBoolean(Constants.PREF_ZERO, zero)
                            .putString(Constants.PREF_NOTIFICATION_URL, json.getString("notification"))
                            .apply();
                    if (!zero) {
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.initialize();
                            }
                        });
                    }
                } catch (JSONException e) {
                }
            }
        });
        request.request(Constants.CONFIG_URL);
    }

    @Override
    public void loadServerUrl() {
        AsyncNetwork request = new AsyncNetwork();
        request.request(Constants.SERVER_URL_CONFIG).
                setRequestCallback(new IRequestCallbackV2() {
                    @Override
                    public void onError(@Nullable HttpURLConnection httpURLConnection, @NotNull Exception exception) {
                        PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                .putString(Constants.PREF_SERVER_URL, Constants.SERVER_URL).apply();
                    }

                    @Override
                    public void onSuccess(@Nullable HttpURLConnection httpURLConnection, @NotNull String response) {
                        String url = response;
                        if (!TextUtils.isEmpty(url)) {
                            PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                                    .putString(Constants.PREF_SERVER_URL, url).apply();
                        }
                    }
                });
    }

    @Override
    public void takeView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void dropView() {
        this.view = null;
    }

    @Override
    public void readPaymentId(String deviceId) {
        if (this.view == null) {
            return;
        }
        Context context = view.getContext();
        if (TextUtils.isEmpty(deviceId)) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String sdUrl = Environment.getExternalStorageDirectory().getAbsolutePath();
                File vipFile = new File(sdUrl, Constants.LOCAL_VIP_PATH);
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(vipFile));
                    deviceId = reader.readLine();
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putString("paymentId", deviceId).apply();
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    CloseableUtil.INSTANCE.closeQuitely(reader);
                }
            }
        }
    }

    @Override
    public void query(@NotNull String query) {
        if (view == null) {
            return;
        }
        Bundle bundle = new Bundle();
        try {
            query = URLEncoder.encode(query, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        bundle.putString("url", com.ecjtu.netcore.Constants.HOST_MOBILE_URL + "/search/" + query);
        Intent intent = AppThemeActivity.newInstance(view.getContext(), SearchFragment.class, bundle);
        view.getContext().startActivity(intent);
    }
}
