package com.ecjtu.componentes.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.cc_fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMediaController = new MediaController(getContext());
        VideoView video = (VideoView) view.findViewById(R.id.video);
        video.setVideoPath(mVideoUrl);
        video.setMediaController(mMediaController);
        video.requestFocus();
        video.start();
    }
}
