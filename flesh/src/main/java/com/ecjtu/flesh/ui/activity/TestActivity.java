package com.ecjtu.flesh.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ecjtu.componentes.activity.RotateNoCreateActivity;
import com.ecjtu.componentes.fragment.VideoFragment;

/**
 * Created by Ethan_Xiang on 2017/10/10.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = RotateNoCreateActivity.newInstance(this, VideoFragment.class, null);
        startActivity(intent);


//        setContentView(R.layout.activity_test);
    }
}
