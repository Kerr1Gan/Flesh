package com.ecjtu.heaven.ui.activity;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ecjtu.heaven.R;
import com.ecjtu.heaven.presenter.MainActivityDelegate;

public class MainActivity extends AppCompatActivity {

    private MainActivityDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,(Toolbar) findViewById(R.id.tool_bar), 0, 0);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);

        mDelegate = new MainActivityDelegate(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDelegate.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDelegate.onResume();
    }
}
