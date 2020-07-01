package com.example.orcsndwizards.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;


public class MusicPlayer {


    private static final String TAG = MusicPlayer.class.getName();
    private Context mContext = null;
    private int mResId = 0;

    private MediaPlayer mCurrentPlayer = null;
    private MediaPlayer mNextPlayer = null;


    public MusicPlayer(Context context, int resId) {
        mContext = context;
        mResId = resId;
        try {
            AssetFileDescriptor afd = context.getResources().openRawResourceFd(mResId);
            mCurrentPlayer = new MediaPlayer();
            mCurrentPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mCurrentPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mCurrentPlayer.start();
                }
            });
            mCurrentPlayer.prepareAsync();
            createNextMediaPlayerRaw();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createNextMediaPlayerRaw() {
        AssetFileDescriptor afd = mContext.getResources().openRawResourceFd(mResId);
        mNextPlayer = new MediaPlayer();
        try {
            mNextPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mNextPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mNextPlayer.seekTo(0);
                    mCurrentPlayer.setNextMediaPlayer(mNextPlayer);
                    mCurrentPlayer.setOnCompletionListener(onCompletionListener);
                }
            });
            mNextPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private final MediaPlayer.OnCompletionListener onCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mCurrentPlayer = mNextPlayer;
                    createNextMediaPlayerRaw();
                    mediaPlayer.release();
                }
            };

    public void stop() throws IllegalStateException {
        if (mCurrentPlayer != null && mCurrentPlayer.isPlaying()) {
            Log.d(TAG, "stop()");
            mCurrentPlayer.stop();
        } else {
            Log.d(TAG, "stop() | mCurrentPlayer " +
                    "is NULL or not playing");
        }

    }

}