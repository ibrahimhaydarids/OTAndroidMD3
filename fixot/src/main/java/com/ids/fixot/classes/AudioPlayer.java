package com.ids.fixot.classes;

import android.content.Context;
import android.media.MediaPlayer;

public class AudioPlayer {

    private final static int MAX_VOLUME = 100;
    private final float volume = (float) (1 - (Math.log(MAX_VOLUME - 40) / Math.log(MAX_VOLUME)));
    private MediaPlayer mMediaPlayer;

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public void play(Context c, int rid) {
        stop();

        mMediaPlayer = MediaPlayer.create(c, rid);
        mMediaPlayer.setVolume(volume, volume);
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        });

        mMediaPlayer.start();
    }

}