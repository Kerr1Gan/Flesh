package com.ecjtu.flesh.ui.activity;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.ecjtu.flesh.Constants;
import com.ecjtu.flesh.R;
import com.ecjtu.flesh.mvp.presenter.MainActivityDelegate;
import com.ecjtu.flesh.mvp.presenter.MainContract;
import com.ecjtu.netcore.network.AsyncNetwork;
import com.ecjtu.netcore.network.IRequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.net.HttpURLConnection;

public class MainActivity extends AppCompatActivity implements MainContract.View{

    private MainActivityDelegate mDelegate;

    private static final String STATUS_BAR_HEIGHT = "status_bar_height";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        transparent();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        View content = findViewById(R.id.content);
        content.setPadding(content.getPaddingLeft(), content.getPaddingTop() + getStatusBarHeight(), content.getPaddingRight(), content.getPaddingBottom());

        checkZero();

        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.PREF_ZERO, false)) {
            mDelegate = new MainActivityDelegate(this);
        }

    }

    private void checkZero() {
        AsyncNetwork request = new AsyncNetwork();
        request.setDoInputOutput(true, false);
        request.request(Constants.CONFIG_URL);
        request.setRequestCallback(new IRequestCallback() {
            @Override
            public void onSuccess(HttpURLConnection httpURLConnection, String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean zero = json.getBoolean("zero");
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit()
                            .putBoolean(Constants.PREF_ZERO, zero)
                            .putString(Constants.PREF_NOTIFICATION_URL, json.getString("notification"))
                            .apply();
                    if (!zero && mDelegate == null) {
                        MainActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (mDelegate == null) {
                                    mDelegate = new MainActivityDelegate(MainActivity.this);
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mDelegate != null) {
            mDelegate.onStop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDelegate != null) {
            mDelegate.onResume();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDelegate != null) {
            mDelegate.onDestroy();
        }
        Glide.get(this).clearMemory();
    }

    protected int getStatusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier(STATUS_BAR_HEIGHT, "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    protected void transparent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);  //去除半透明状态栏
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN); //一般配合fitsSystemWindows()使用, 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        if (Build.VERSION.SDK_INT >= 24/*Build.VERSION_CODES.N*/) {
            try {
                Class decorViewClazz = Class.forName("com.android.internal.policy.DecorView");
                Field field = decorViewClazz.getDeclaredField("mSemiTransparentStatusBarColor");
                field.setAccessible(true);
                field.setInt(getWindow().getDecorView(), Color.TRANSPARENT); //改为透明
            } catch (Exception e) {
            }
        }
    }
}
