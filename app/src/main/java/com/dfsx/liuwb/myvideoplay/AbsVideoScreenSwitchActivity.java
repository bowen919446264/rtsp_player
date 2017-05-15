package com.dfsx.liuwb.myvideoplay;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.dfsx.videoijkplayer.VideoPlayView;

/**
 * Created by liuwb on 2017/1/11.
 */
public abstract class AbsVideoScreenSwitchActivity extends FragmentActivity {
    protected VideoPlayView videoPlayer;

    private ViewGroup portraitVideoContainer;


    private FrameLayout fullScreenContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createVideoPlayer();

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(R.layout.act_video_layout);
        FrameLayout container = (FrameLayout) findViewById(R.id.video_act_layout_container);
        LayoutInflater.from(this).inflate(layoutResID, container);
    }

    @Override
    public void setContentView(View view) {
        View actView = LayoutInflater.from(this).inflate(R.layout.act_video_layout, null);
        FrameLayout container = (FrameLayout) actView.findViewById(R.id.video_act_layout_container);
        container.addView(view);
        super.setContentView(actView);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        View actView = LayoutInflater.from(this).inflate(R.layout.act_video_layout, null);
        FrameLayout container = (FrameLayout) actView.findViewById(R.id.video_act_layout_container);
        container.addView(view);
        super.setContentView(actView, params);
    }

    private void createVideoPlayer() {
        videoPlayer = new VideoPlayView(this);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        videoPlayer.setLayoutParams(params);
    }

    public void startVideo(String path) {
        if (videoPlayer != null) {
            videoPlayer.start(path);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("TAG", "onConfigurationChanged -- ");
        if (videoPlayer != null) {
            videoPlayer.onChanged(newConfig);
            ViewGroup videoContainer = (ViewGroup) videoPlayer.getParent();
            removeVideoPlayerFromContainer(videoPlayer);
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                if (portraitVideoContainer == null) {
                    addVideoPlayerToContainer();
                } else {
                    addVideoPlayerView(portraitVideoContainer);
                }
                fullScreenContainer.setVisibility(View.GONE);
            } else {
                addVideoPlayerToFullScreenContainer(videoPlayer);
                portraitVideoContainer = videoContainer;
            }
        }
    }

    protected final void addVideoPlayerView(ViewGroup container) {
        if (container != null) {
            if (!isExistVideoViewInContainer(container)) {
                container.addView(videoPlayer);
            }
        }
    }

    private boolean isExistVideoViewInContainer(ViewGroup container) {
        if (container == null) {
            return false;
        }
        for (int i = 0; i < container.getChildCount(); i++) {
            View itemView = container.getChildAt(i);
            if (itemView instanceof VideoPlayView) {
                return true;
            }
        }
        return false;
    }

    /**
     * 向现实Video的容器里添加VideoPlayer
     *
     * @param videoPlayer
     */
    public abstract void addVideoPlayerToContainer(VideoPlayView videoPlayer);

    private void addVideoPlayerToContainer() {
        if (fullScreenContainer != null) {
            fullScreenContainer.setVisibility(View.GONE);
        }
        addVideoPlayerToContainer(videoPlayer);

    }

    public void removeVideoPlayerFromContainer(VideoPlayView videoPlayer) {
        if (videoPlayer != null) {
            ViewGroup view = (ViewGroup) videoPlayer.getParent();
            if (view != null) {
                view.removeView(videoPlayer);
            }
        }
    }

    /**
     * 想全屏的video容器中添加VideoPlayer
     *
     * @param videoPlayer
     */
    public void addVideoPlayerToFullScreenContainer(VideoPlayView videoPlayer) {
        if (fullScreenContainer == null) {
            fullScreenContainer = (FrameLayout) findViewById(R.id.full_screen_video_container);
        }
        fullScreenContainer.removeView(videoPlayer);
        fullScreenContainer.addView(videoPlayer, 0);
        fullScreenContainer.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        removeVideoPlayerFromContainer(videoPlayer);
        addVideoPlayerToContainer();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getResumePlayerStatus()) {
            videoPlayer.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearVideoPlayStatus();
        if (videoPlayer.isPlay()) {
            saveVideoPlayStatus();
            videoPlayer.pause();
        } else if (!videoPlayer.isInPlaybackState()) {
            videoPlayer.release();
        }
    }

    private boolean getResumePlayerStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        boolean isPaly = sp.getBoolean(key, false);
        sp.edit().clear().commit();
        return isPaly;
    }

    private void saveVideoPlayStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().putBoolean(key, videoPlayer.isPlay()).commit();
    }

    private void clearVideoPlayStatus() {
        String key = "ABsVideo_play_" + getClass().getName();
        SharedPreferences sp = this.getSharedPreferences(key, 0);
        sp.edit().clear().commit();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoPlayer.stop();
        videoPlayer.release();
        videoPlayer.onDestroy();
        videoPlayer = null;
        clearVideoPlayStatus();
        removeVideoPlayerFromContainer(videoPlayer);
    }
}
