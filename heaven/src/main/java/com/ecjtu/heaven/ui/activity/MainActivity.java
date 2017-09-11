package com.ecjtu.heaven.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ecjtu.heaven.R;
import com.ecjtu.heaven.presenter.MainActivityDelegate;

public class MainActivity extends AppCompatActivity {

    private MainActivityDelegate mDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDelegate = new MainActivityDelegate(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mDelegate.onStop();
    }
}
