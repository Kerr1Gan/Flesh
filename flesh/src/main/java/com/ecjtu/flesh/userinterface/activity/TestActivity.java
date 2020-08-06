package com.ecjtu.flesh.userinterface.activity;

import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;

import com.ecjtu.flesh.R;
import com.ecjtu.flesh.util.reptiles.HtmlSource;

/**
 * Created by Ethan_Xiang on 2017/10/10.
 */

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = RotateNoCreateActivity.newInstance(this, VideoTabFragment.class, null);
//        startActivity(intent);
        setContentView(R.layout.activity_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        this.setSupportActionBar(toolbar);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setPadding(toolbar.getPaddingLeft(), toolbar.getPaddingTop() + 50, toolbar.getPaddingRight(), toolbar.getPaddingBottom());


//        IjkVideoView mVideoView = (IjkVideoView) findViewById(R.id.ijk);
//        AndroidMediaController mediaController = new AndroidMediaController(this);
//        mVideoView.setMediaController(mediaController);
//
////        mVideoView.setVideoPath("http://k.syasn.com/ps/ps444.mp4?k1=59.63.206.40&k2=ms&k3=18b7c73a3c7f52799c9a8691d612f1e4&k4=240637674fc71257f8efd375f6b3a404&k5=ps444&k6=26bfbfc99467ce4f3633894f6577f2a9&k7=17e1c37f9affcd6ca43df579592aad14&end=300");
////        mVideoView.setVideoPath("https://firststorage0001.s3.ap-northeast-2.amazonaws.com/0388.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20180222T114459Z&X-Amz-SignedHeaders=host&X-Amz-Expires=3598&X-Amz-Credential=AKIAIDWWXUYSUAJ24SZA%2F20180222%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=90b91819e9e7d17c08eb775d8c1d019560d2035da424b07e712df3c4eb50ed88");
//        mVideoView.setVideoPath("https://s3.ap-northeast-2.amazonaws.com/firststorage0001/%E5%A4%AA%E7%A9%BA%E6%95%91%E6%8F%B4.Salyut-7.2017.1080p.WEB-DL.X264.AAC-%E4%B8%AD%E6%96%87%E5%AD%97%E5%B9%95-RARBT.mp4");
//        mVideoView.requestFocus();
//        mVideoView.start();

//        MobileAds.initialize(this,
//                getString(R.string.admob_app_id));
//        AdView mAdView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);
//        mAdView.setAdListener(new AdListener(){
//            @Override
//            public void onAdClosed() {
//                super.onAdClosed();
//            }
//
//            @Override
//            public void onAdFailedToLoad(int i) {
//                super.onAdFailedToLoad(i);
//            }
//
//            @Override
//            public void onAdLeftApplication() {
//                super.onAdLeftApplication();
//            }
//
//            @Override
//            public void onAdOpened() {
//                super.onAdOpened();
//            }
//
//            @Override
//            public void onAdLoaded() {
//                super.onAdLoaded();
//            }
//
//            @Override
//            public void onAdClicked() {
//                super.onAdClicked();
//            }
//
//            @Override
//            public void onAdImpression() {
//                super.onAdImpression();
//            }
//        });

//        Intent intent = new Intent(this,AppWebViewActivity.class);
//        intent.putExtra("url","http://m.mzitu.com");
//        startActivity(intent);

        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                final Runnable run = this;
                if (source != null)
                    source.release();
                source = new HtmlSource(TestActivity.this);
                source.addCallbackListeners(new HtmlSource.ICallback() {
                    @Override
                    public void afterJsHtml(String html) {
                        Log.i("tttttt", html);
                        handler.post(run);
                    }
                });
                source.loadExeJsHtml("http://ofo91.com");
            }
        });
    }

    HtmlSource source = null;

}
