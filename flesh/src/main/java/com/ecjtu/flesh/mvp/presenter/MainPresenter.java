package com.ecjtu.flesh.mvp.presenter;

import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.ecjtu.flesh.Constants;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;
import com.ecjtu.netcore.network.IRequestCallbackV2;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

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
    public void checkZero() {
        AsyncNetwork request = new AsyncNetwork();
        request.setDoInputOutput(true, false);
        request.request(Constants.CONFIG_URL);
        request.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean zero = json.getBoolean("zero");
                    PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit()
                            .putBoolean(Constants.PREF_ZERO, zero)
                            .putString(Constants.PREF_NOTIFICATION_URL, json.getString("notification"))
                            .apply();
                    if (!zero) {
                        view.initialize();
                    }
                } catch (JSONException e) {
                }
            }
        });
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
}
