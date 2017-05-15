package com.dfsx.videoijkplayer.ksyplayer;

import android.content.Context;
import android.media.MediaDataSource;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Surface;
import android.view.SurfaceHolder;
import com.ksyun.media.player.IMediaPlayer;
import com.ksyun.media.player.KSYMediaPlayer;
import tv.danmaku.ijk.media.player.AbstractMediaPlayer;
import tv.danmaku.ijk.media.player.MediaInfo;
import tv.danmaku.ijk.media.player.misc.IMediaDataSource;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;
import tv.danmaku.ijk.media.player.pragma.DebugLog;

import java.io.FileDescriptor;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 金山云视频播放器的MediaPlayer
 * Created by liuwb on 2017/4/10.
 */
public class IjkKsyMediaPlayer extends AbstractMediaPlayer {

    private KSYMediaPlayer mInternalMediaPlayer;
    private KsyMediaPlayerListenerHolder mInternalListenerAdapter;
    private String mDataSource;
    private Object mInitLock = new Object();
    private MediaDataSource mMediaDataSource;
    private boolean mIsReleased;
    private static MediaInfo sMediaInfo;

    /**
     * @param context
     * @param isUseHard
     * @param bufferTimeMax 单位秒
     */
    public IjkKsyMediaPlayer(Context context, boolean isUseHard, int bufferTimeMax) {
        synchronized (mInitLock) {
            mInternalMediaPlayer = new KSYMediaPlayer.Builder(context).build();
        }
        mInternalMediaPlayer.setBufferTimeMax(bufferTimeMax);
        if (isUseHard) {
            mInternalMediaPlayer.setDecodeMode(KSYMediaPlayer.KSYDecodeMode.KSY_DECODE_MODE_AUTO);
        }
        mInternalListenerAdapter = new KsyMediaPlayerListenerHolder(this);
        attachInternalListeners();
    }

    public KSYMediaPlayer getInternalMediaPlayer() {
        return mInternalMediaPlayer;
    }

    @Override
    public void setDisplay(SurfaceHolder surfaceHolder) {
        synchronized (this.mInitLock) {
            if (!this.mIsReleased) {
                this.mInternalMediaPlayer.setDisplay(surfaceHolder);
            }
        }
    }

    @Override
    public void setDataSource(Context context, Uri uri) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        this.mInternalMediaPlayer.setDataSource(context, uri);
    }

    @Override
    public void setDataSource(Context context, Uri uri, Map<String, String> headers) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        this.mInternalMediaPlayer.setDataSource(context, uri, headers);
    }

    @Override
    public void setDataSource(FileDescriptor fileDescriptor) throws IOException, IllegalArgumentException, IllegalStateException {
        this.mInternalMediaPlayer.setDataSource(fileDescriptor);
    }

    @Override
    public void setDataSource(String path) throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
        this.mDataSource = path;
        Uri uri = Uri.parse(path);
        String scheme = uri.getScheme();
        if (!TextUtils.isEmpty(scheme) && scheme.equalsIgnoreCase("file")) {
            this.mInternalMediaPlayer.setDataSource(uri.getPath());
        } else {
            this.mInternalMediaPlayer.setDataSource(path);
        }
    }

    @Override
    public void setDataSource(IMediaDataSource mediaDataSource) {
        super.setDataSource(mediaDataSource);
    }

    @Override
    public String getDataSource() {
        return this.mDataSource;
    }


    @Override
    public void prepareAsync() throws IllegalStateException {
        this.mInternalMediaPlayer.prepareAsync();
    }

    @Override
    public void start() throws IllegalStateException {
        this.mInternalMediaPlayer.start();
    }

    @Override
    public void stop() throws IllegalStateException {
        this.mInternalMediaPlayer.stop();
    }

    @Override
    public void pause() throws IllegalStateException {
        this.mInternalMediaPlayer.pause();
    }

    @Override
    public void setScreenOnWhilePlaying(boolean screenOn) {
        this.mInternalMediaPlayer.setScreenOnWhilePlaying(screenOn);
    }

    @Override
    public int getVideoWidth() {
        return this.mInternalMediaPlayer.getVideoWidth();
    }

    @Override
    public int getVideoHeight() {
        return mInternalMediaPlayer.getVideoHeight();
    }

    @Override
    public boolean isPlaying() {
        return mInternalMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long l) throws IllegalStateException {
        mInternalMediaPlayer.seekTo(l);
    }

    @Override
    public long getCurrentPosition() {
        return mInternalMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getDuration() {
        return mInternalMediaPlayer.getDuration();
    }

    @Override
    public void release() {
        this.mIsReleased = true;
        this.mInternalMediaPlayer.release();
        this.resetListeners();
        this.attachInternalListeners();
    }

    @Override
    public void reset() {
        try {
            this.mInternalMediaPlayer.reset();
        } catch (IllegalStateException var2) {
            DebugLog.printStackTrace(var2);
        }

        this.resetListeners();
        this.attachInternalListeners();
    }

    @Override
    public void setVolume(float v, float v1) {
        this.mInternalMediaPlayer.setVolume(v, v1);
    }

    @Override
    public int getAudioSessionId() {
        return mInternalMediaPlayer.getAudioSessionId();
    }

    @Override
    public MediaInfo getMediaInfo() {
        if (mInternalMediaPlayer.getMediaInfo() != null) {
            MediaInfo module = new MediaInfo();
            module.mVideoDecoder = mInternalMediaPlayer.getMediaInfo().mVideoDecoder;
            module.mVideoDecoderImpl = mInternalMediaPlayer.getMediaInfo().mVideoDecoderImpl;
            module.mAudioDecoder = mInternalMediaPlayer.getMediaInfo().mAudioDecoder;
            module.mAudioDecoderImpl = mInternalMediaPlayer.getMediaInfo().mAudioDecoderImpl;
            sMediaInfo = module;
        }
        if (sMediaInfo == null) {
            MediaInfo module = new MediaInfo();
            module.mVideoDecoder = "android";
            module.mVideoDecoderImpl = "HW";
            module.mAudioDecoder = "android";
            module.mAudioDecoderImpl = "HW";
            sMediaInfo = module;
        }

        return sMediaInfo;
    }

    @Override
    public void setLogEnabled(boolean b) {
        mInternalMediaPlayer.setLogEnabled(b);
    }

    @Override
    public boolean isPlayable() {
        return mInternalMediaPlayer.isPlayable();
    }

    @Override
    public void setAudioStreamType(int i) {
        mInternalMediaPlayer.setAudioStreamType(i);
    }

    @Override
    public void setKeepInBackground(boolean b) {
        mInternalMediaPlayer.setKeepInBackground(b);
    }

    @Override
    public int getVideoSarNum() {
        return mInternalMediaPlayer.getVideoSarNum();
    }

    @Override
    public int getVideoSarDen() {
        return mInternalMediaPlayer.getVideoSarDen();
    }

    @Override
    public void setWakeMode(Context context, int i) {
        mInternalMediaPlayer.setWakeMode(context, i);
    }

    @Override
    public void setLooping(boolean b) {
        mInternalMediaPlayer.setLooping(b);
    }

    @Override
    public boolean isLooping() {
        return mInternalMediaPlayer.isLooping();
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        return new ITrackInfo[0];
    }

    @Override
    public void setSurface(Surface surface) {
        this.mInternalMediaPlayer.setSurface(surface);
    }

    private void attachInternalListeners() {
        this.mInternalMediaPlayer.setOnPreparedListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnBufferingUpdateListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnCompletionListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnSeekCompleteListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnVideoSizeChangedListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnErrorListener(this.mInternalListenerAdapter);
        this.mInternalMediaPlayer.setOnInfoListener(this.mInternalListenerAdapter);
    }

    private class KsyMediaPlayerListenerHolder implements IMediaPlayer.OnPreparedListener, IMediaPlayer.OnCompletionListener,
            IMediaPlayer.OnBufferingUpdateListener, IMediaPlayer.OnSeekCompleteListener, IMediaPlayer.OnVideoSizeChangedListener,
            IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener, IMediaPlayer.OnMessageListener, IMediaPlayer.OnLogEventListener {
        public final WeakReference<IjkKsyMediaPlayer> mWeakMediaPlayer;

        public KsyMediaPlayerListenerHolder(IjkKsyMediaPlayer mp) {
            this.mWeakMediaPlayer = new WeakReference(mp);
        }

        @Override
        public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            if (self != null) {
                self.notifyOnBufferingUpdate(i);
            }
        }

        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            if (self != null) {
                self.notifyOnCompletion();
            }
        }

        @Override
        public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            return self != null && self.notifyOnError(i, i1);
        }

        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            return self != null && self.notifyOnInfo(i, i1);
        }

        @Override
        public void onLogEvent(IMediaPlayer iMediaPlayer, String s) {
        }

        @Override
        public void onMessage(IMediaPlayer iMediaPlayer, String s, String s1, double v) {

        }

        @Override
        public void onPrepared(IMediaPlayer iMediaPlayer) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            if (self != null) {
                self.notifyOnPrepared();
            }
        }

        @Override
        public void onSeekComplete(IMediaPlayer iMediaPlayer) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            if (self != null) {
                self.notifyOnSeekComplete();
            }
        }

        @Override
        public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2, int i3) {
            IjkKsyMediaPlayer self = mWeakMediaPlayer.get();
            if (self != null) {
                self.notifyOnVideoSizeChanged(i, i1, i2, i3);
            }
        }
    }

}
