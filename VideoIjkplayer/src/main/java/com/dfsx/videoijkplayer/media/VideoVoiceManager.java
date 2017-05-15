package com.dfsx.videoijkplayer.media;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by liuwb on 2016/11/3.
 */
public class VideoVoiceManager {

    private static VideoVoiceManager instance;

    private AudioManager am;

    private VideoVoiceManager(Context context) {
        am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public static synchronized VideoVoiceManager getInstance(Context context) {
        if (instance == null) {
            instance = new VideoVoiceManager(context);
        }
        return instance;
    }

    public void muteAudio() {
        am.setStreamMute(AudioManager.STREAM_MUSIC, true);
    }

    public void unmuteAudio() {
        am.setStreamMute(AudioManager.STREAM_MUSIC, false);
    }

    public AudioManager getAm() {
        return am;
    }
}
