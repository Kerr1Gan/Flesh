package com.ecjtu.flesh.ui.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.ecjtu.flesh.R;

import tv.danmaku.ijk.media.exo.video.AndroidMediaController;
import tv.danmaku.ijk.media.exo.video.IjkVideoView;

/**
 * Created by Ethan_Xiang on 2017/10/10.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = RotateNoCreateActivity.newInstance(this, VideoFragment.class, null);
//        startActivity(intent);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + 50, toolbar.getPaddingRight(), toolbar.getPaddingBottom());


        IjkVideoView mVideoView = (IjkVideoView) findViewById(R.id.ijk);
        AndroidMediaController mediaController = new AndroidMediaController(this);
        mVideoView.setMediaController(mediaController);

        mVideoView.setVideoPath("http://k.syasn.com/ps/ps444.mp4?k1=59.63.206.40&k2=ms&k3=18b7c73a3c7f52799c9a8691d612f1e4&k4=240637674fc71257f8efd375f6b3a404&k5=ps444&k6=26bfbfc99467ce4f3633894f6577f2a9&k7=17e1c37f9affcd6ca43df579592aad14&end=300");
        mVideoView.requestFocus();
        mVideoView.start();
    }
}
