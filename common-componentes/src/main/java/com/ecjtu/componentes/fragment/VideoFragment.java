package com.ecjtu.componentes.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ecjtu.componentes.R;

/**
 * Created by Ethan_Xiang on 2017/10/10.
 */

public class VideoFragment extends Fragment {

    private String mVideoUrl = "http://sp.9ky.cc/vlook.php?id=YklkPTQ5NDcyMjY=";
    private MediaController mMediaController;
    private VideoView mVideoView;
    private int mLastPosition = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cc_fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMediaController = new MediaController(getContext());
        mVideoView = (VideoView) view.findViewById(R.id.video);
        mVideoView.setVideoPath(mVideoUrl);
        mVideoView.setMediaController(mMediaController);
        mVideoView.requestFocus();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVideoView.start();
        mVideoView.seekTo(mLastPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPosition = mVideoView.getCurrentPosition();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVideoView.stopPlayback();
    }

}
