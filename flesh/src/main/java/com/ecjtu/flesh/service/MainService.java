package com.ecjtu.flesh.service;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.db.DatabaseManager;
import com.ecjtu.flesh.db.table.impl.NotificationTableImpl;
import com.ecjtu.flesh.model.ModelManager;
import com.ecjtu.flesh.model.models.NotificationModel;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by KerriGan on 2017/6/18.
 */

public class MainService extends Service {

    private static final String TAG = "MainService";

    private HandlerThread mHandlerThread = null;

    private Handler mHandler = null;

    private AsyncNetwork mBaseRequest;

    private AsyncNetwork mNotificationRequest;

    private String mNotifyUrl = "";

    private final long DELAY_TIME = 10 * 60 * 1000;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SimpleServiceStub(this).asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");

        mHandlerThread = new HandlerThread(TAG);
        mHandlerThread.start();

        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mBaseRequest.request("https://kerr1gan.github.io/flesh/config.json");
                mNotificationRequest.request(mNotifyUrl);
                mHandler.sendEmptyMessageDelayed(0, DELAY_TIME);
            }
        };
        initRequest();
        mHandler.sendEmptyMessageDelayed(0, DELAY_TIME);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return START_STICKY;
    }

    private void initRequest() {
        mBaseRequest = new AsyncNetwork();
        mBaseRequest.setDoInputOutput(true, false);
        mBaseRequest.request("https://kerr1gan.github.io/flesh/config.json");
        mBaseRequest.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean zero = json.getBoolean("zero");
                    mNotifyUrl = json.getString("notification");
                    PreferenceManager.getDefaultSharedPreferences(MainService.this).edit()
                            .putBoolean(Constants.PREF_ZERO, zero)
                            .putString(Constants.PREF_NOTIFICATION_URL, mNotifyUrl)
                            .apply();
                } catch (JSONException e) {
                }
            }
        });

        mNotificationRequest = new AsyncNetwork();
        mNotificationRequest.request(mNotifyUrl);
        mNotificationRequest.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                List<NotificationModel> models = new ArrayList<NotificationModel>();
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        NotificationModel model = ModelManager.getNotificationModel(obj.getInt("id"), obj.getString("title"), obj.getString("content"),
                                obj.getInt("limit"), obj.getString("time"), obj.getString("timeLimit"), obj.getString("actionDetailUrl"));
                        models.add(model);
                    }
                } catch (JSONException e) {
                }

                SQLiteDatabase db = DatabaseManager.getInstance(MainService.this).getDatabase();
                if (db != null) {
                    db.beginTransaction();
                    NotificationTableImpl impl = new NotificationTableImpl();
                    for (NotificationModel item : models) {
                        impl.addNotification(db, item);
                    }
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    db.close();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        mHandlerThread.quit();
        mHandler.removeCallbacksAndMessages(null);
    }

    public boolean hasNotification() {
        SQLiteDatabase db = DatabaseManager.getInstance(this).getDatabase();
        if (db != null) {
            NotificationTableImpl impl = new NotificationTableImpl();
            List<NotificationModel> models = impl.getAllNotification(db);
            db.close();
            for (NotificationModel model : models) {
                if (model.getOccurs() < model.getLimit() || model.getLimit() < 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class SimpleServiceStub extends IServiceInterface.Stub {
        MainService mService;

        public SimpleServiceStub(MainService service) {
            mService = service;
        }

        @Override
        public boolean hasNotification() throws RemoteException {
            return mService.hasNotification();
        }
    }

}
