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
import android.text.TextUtils;
import android.util.Log;

import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.db.DatabaseManager;
import com.ecjtu.flesh.db.table.impl.NotificationTableImpl;
import com.ecjtu.flesh.model.ModelManager;
import com.ecjtu.flesh.model.models.NotificationModel;
import com.ecjtu.flesh.notification.SimpleNotificationBuilder;
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

    private static final int MSG_REQUEST = 0x10;

    private static final int MSG_CHECK_NOTIFICATION = 0x11;

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

                switch (msg.what) {
                    case MSG_REQUEST:
                        mBaseRequest.request(Constants.CONFIG_URL);
                        if (!TextUtils.isEmpty(mNotifyUrl)) {
                            mNotificationRequest.request(mNotifyUrl);
                        }
                        mHandler.sendEmptyMessageDelayed(MSG_REQUEST, DELAY_TIME);
                        break;
                    case MSG_CHECK_NOTIFICATION:
                        List<NotificationModel> notify = hasNotification();
                        if (notify != null) {
                            for (int i = 0; i < notify.size(); i++) {
                                NotificationModel model = notify.get(i);
                                SimpleNotificationBuilder builder = new SimpleNotificationBuilder(MainService.this);
                                if (model.getType() == 0) {
                                    builder.build(model.getTitle(), model.getContent(), model.getTicker(), model.getActionDetailUrl());
                                } else if (model.getType() == 1) {
                                    builder.buildH5(model.getTitle(), model.getContent(), model.getTicker(), model.getH5Page());
                                }
                                builder.send(null);
                                model.setOccurs(model.getOccurs() + 1);
                            }

                            SQLiteDatabase db = DatabaseManager.getInstance(MainService.this).getDatabase();
                            if (db != null) {
                                db.beginTransaction();
                                NotificationTableImpl impl = new NotificationTableImpl();
                                for (NotificationModel item : notify) {
                                    impl.updateNotification(db, item);
                                }
                                db.setTransactionSuccessful();
                                db.endTransaction();
                                db.close();
                            }

                        }
                        break;
                }

            }
        };
        initRequest();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        mHandler.sendEmptyMessage(MSG_REQUEST);
        return START_STICKY;
    }

    private void initRequest() {
        mBaseRequest = new AsyncNetwork();
        mBaseRequest.setDoInputOutput(true, false);
        mBaseRequest.request(Constants.CONFIG_URL);
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
                    mNotificationRequest.request(mNotifyUrl);
                } catch (JSONException e) {
                }
            }
        });

        mNotificationRequest = new AsyncNetwork();
        mNotificationRequest.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                List<NotificationModel> models = new ArrayList<NotificationModel>();
                try {
                    JSONArray arr = new JSONArray(response);
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        NotificationModel model = ModelManager.getNotificationModel(obj.getInt("id"), obj.getString("title"),
                                obj.getString("content"), obj.getString("ticker"),
                                obj.getInt("limit"), obj.getString("time"), obj.getString("timeLimit"), obj.getString("actionDetailUrl"),
                                obj.getString("h5Page"), obj.getInt("type"));
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

                mHandler.sendEmptyMessage(MSG_CHECK_NOTIFICATION);
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

    public List<NotificationModel> hasNotification() {
        SQLiteDatabase db = DatabaseManager.getInstance(this).getDatabase();
        List<NotificationModel> ret = null;
        if (db != null) {
            NotificationTableImpl impl = new NotificationTableImpl();
            List<NotificationModel> models = impl.getAllNotification(db);
            db.close();
            ret = new ArrayList<>();
            for (NotificationModel model : models) {
                if (model.getOccurs() < model.getLimit() || model.getLimit() < 0) {
                    ret.add(model);
                }
            }
        }
        return ret;
    }

    public static class SimpleServiceStub extends IServiceInterface.Stub {
        MainService mService;

        public SimpleServiceStub(MainService service) {
            mService = service;
        }

        @Override
        public boolean hasNotification() throws RemoteException {
            return mService.hasNotification().size() > 0;
        }
    }

}
