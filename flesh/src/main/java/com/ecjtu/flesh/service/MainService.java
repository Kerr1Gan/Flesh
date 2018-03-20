package com.ecjtu.flesh.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.ecjtu.componentes.activity.ActionBarFragmentActivity;
import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.db.DatabaseManager;
import com.ecjtu.flesh.db.table.impl.NotificationTableImpl;
import com.ecjtu.flesh.model.models.NotificationModel;
import com.ecjtu.flesh.notification.SimpleNotificationBuilder;
import com.ecjtu.flesh.ui.fragment.WebViewFragment;
import com.ecjtu.flesh.util.encrypt.SecretKey;
import com.ecjtu.flesh.util.encrypt.SecretKeyUtils;
import com.ecjtu.flesh.util.file.FileUtil;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kotlin.jvm.internal.Intrinsics;


/**
 * Created by KerriGan on 2017/6/18.
 */

public class MainService extends Service {

    private static final String TAG = "MainService";
    private static final String ACTION_UPLOAD_DATABASE = "action_upload_database";
    private static final String EXTRA_DEVICE_ID = "extra_device_id";

    private HandlerThread mHandlerThread = null;

    private Handler mHandler = null;

    private AsyncNetwork mBaseRequest;

    private AsyncNetwork mNotificationRequest;

    private String mNotifyUrl = "";

    private final long DELAY_TIME = 10 * 60 * 1000;

    private static final int MSG_REQUEST = 0x10;

    private static final int MSG_CHECK_NOTIFICATION = 0x11;

    private Handler mMainHandler = null;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new SimpleServiceStub(this).asBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        mMainHandler = new Handler();
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
                                } else if (model.getType() == 2) {
                                    // force open the url
                                    builder.buildH5(model.getTitle(), model.getContent(), model.getTicker(), model.getH5Page());
                                    Intent intent = ActionBarFragmentActivity.newInstance(MainService.this, WebViewFragment.class, WebViewFragment.openUrl(model.getH5Page()));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    MainService.this.startActivity(intent);
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
        if (intent.getAction() != null && intent.getAction().equals(ACTION_UPLOAD_DATABASE)) {
            String deviceId = intent.getStringExtra(EXTRA_DEVICE_ID);
            if (TextUtils.isEmpty(deviceId)) {
                uploadDatabase(deviceId);
            }
        } else {
            mHandler.sendEmptyMessage(MSG_REQUEST);
        }
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
                        NotificationModel model = NotificationModel.getNotificationModel(obj.getInt("id"), obj.getString("title"),
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

    private AmazonS3Client mS3;

    public void uploadDatabase(final String deviceId) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                OutputStream outputStream = null;
                S3Object s3Object = null;
                try {
                    final String time;
                    AmazonS3Client var22;
                    if (mS3 == null) {
                        SecretKey secretKey = SecretKeyUtils.INSTANCE.getKeyFromServer();
                        SecretKeyUtils var10000 = SecretKeyUtils.INSTANCE;
                        if (secretKey == null) {
                            Intrinsics.throwNpe();
                        }

                        Key var10001 = secretKey.getKey();
                        Intrinsics.checkExpressionValueIsNotNull(var10001, "secretKey!!.key");
                        String content = var10000.getS3InfoFromServer(var10001);
                        String[] params = content.split(",");
                        BasicAWSCredentials provider = new BasicAWSCredentials(params[0], params[1]);
                        ClientConfiguration config = new ClientConfiguration();
                        config.setProtocol(Protocol.HTTP);
                        mS3 = new AmazonS3Client(provider, config);
                        Region region = Region.getRegion(Regions.CN_NORTH_1);
                        if (mS3 != null) {
                            mS3.setRegion(region);
                            mS3.setEndpoint("s3.ap-northeast-2.amazonaws.com");
                        }
                    }

                    File dbPath = getDatabasePath("heaven");
                    var22 = mS3;
                    s3Object = var22 != null ? var22.getObject("firststorage0001", "databases/" + deviceId) : null;
                    if (s3Object != null) {
                        String var26;
                        label173:
                        {
                            ObjectMetadata var23 = s3Object.getObjectMetadata();
                            if (var23 != null) {
                                Map var25 = var23.getUserMetadata();
                                if (var25 != null) {
                                    var26 = (String) var25.get("update_time");
                                    if (var26 != null) {
                                        break label173;
                                    }
                                }
                            }

                            var26 = "-1";
                        }

                        time = var26;
                        outputStream = new FileOutputStream(dbPath);
                        FileUtil var27 = FileUtil.INSTANCE;
                        S3ObjectInputStream var24 = s3Object.getObjectContent();
                        Intrinsics.checkExpressionValueIsNotNull(var24, "s3Object.objectContent");
                        var27.copyFile(var24, outputStream);
                        mMainHandler.post((new Runnable() {
                            public final void run() {
                                if (Intrinsics.areEqual(time, "-1") ^ true) {
                                    SharedPreferences.Editor var10000 = PreferenceManager.getDefaultSharedPreferences(MainService.this).edit();
                                    String var1 = time;
                                    String var3 = "pref_sync_data_time";
                                    SharedPreferences.Editor var2 = var10000;
                                    long var4 = Long.parseLong(var1);
                                    var2.putLong(var3, var4).apply();
                                }
                                Log.i(TAG, "同步成功");
                            }
                        }));
                    }
                } catch (Exception var19) {
                    var19.printStackTrace();
                    mMainHandler.post((new Runnable() {
                        public final void run() {
                            Log.i(TAG, "同步失败");
                        }
                    }));
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (Exception var18) {
                        ;
                    }

                    try {
                        if (s3Object != null) {
                            s3Object.close();
                        }
                    } catch (Exception var17) {
                        ;
                    }

                }
            }
        });
    }

    public static Intent createUploadDbIntent(String deviceId) {
        Intent i = new Intent(ACTION_UPLOAD_DATABASE);
        i.putExtra(EXTRA_DEVICE_ID, deviceId);
        return i;
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
