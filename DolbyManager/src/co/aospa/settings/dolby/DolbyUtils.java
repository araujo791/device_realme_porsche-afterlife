/*
 * Copyright (C) 2018,2020 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package co.aospa.settings.dolby;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Handler;
import android.os.SystemClock;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;

import java.util.Arrays;
import java.util.List;

public final class DolbyUtils {

    private static final String TAG = "DolbyUtils";
    private static final String DEFAULT_PRESET = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0";
    private static final int EFFECT_PRIORITY = 100;

    private static DolbyUtils mInstance;
    private DolbyAtmos mDolbyAtmos;
    private MediaSessionManager mMediaSessionManager;
    private Context mContext;
    private Handler mHandler = new Handler();

    private DolbyUtils(Context context) {
        mContext = context;
        mDolbyAtmos = new DolbyAtmos(EFFECT_PRIORITY, 0);
        mMediaSessionManager = context.getSystemService(MediaSessionManager.class);
    }

    public static synchronized DolbyUtils getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DolbyUtils(context);
        }
        return mInstance;
    }

    public void onBootCompleted() {
        Log.i(TAG, "onBootCompleted");
        mDolbyAtmos.setEnabled(mDolbyAtmos.getDsOn());
        mDolbyAtmos.setVolumeLevelerEnabled(false);
    }

    private void triggerPlayPause(MediaController controller) {
        long when = SystemClock.uptimeMillis();
        final KeyEvent evDownPause = new KeyEvent(when, when, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PAUSE, 0);
        final KeyEvent evUpPause = KeyEvent.changeAction(evDownPause, KeyEvent.ACTION_UP);
        final KeyEvent evDownPlay = new KeyEvent(when, when, KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY, 0);
        final KeyEvent evUpPlay = KeyEvent.changeAction(evDownPlay, KeyEvent.ACTION_UP);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                controller.dispatchMediaButtonEvent(evDownPause);
            }
        });
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.dispatchMediaButtonEvent(evUpPause);
            }
        }, 20);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.dispatchMediaButtonEvent(evDownPlay);
            }
        }, 1000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                controller.dispatchMediaButtonEvent(evUpPlay);
            }
        }, 1020);
    }

    private int getMediaControllerPlaybackState(MediaController controller) {
        if (controller != null) {
            final PlaybackState playbackState = controller.getPlaybackState();
            if (playbackState != null) {
                return playbackState.getState();
            }
        }
        return PlaybackState.STATE_NONE;
    }

    private void refreshPlaybackIfNecessary(){
        if (mMediaSessionManager == null) return;

        final List<MediaController> sessions
                = mMediaSessionManager.getActiveSessionsForUser(
                null, UserHandle.ALL);
        for (MediaController controller : sessions) {
            if (PlaybackState.STATE_PLAYING ==
                    getMediaControllerPlaybackState(controller)) {
                triggerPlayPause(controller);
                break;
            }
        }
    }

    private void checkEffect() {
        if (!mDolbyAtmos.hasControl()) {
            Log.w(TAG, "lost control, recreating effect");
            mDolbyAtmos.release();
            mDolbyAtmos = new DolbyAtmos(EFFECT_PRIORITY, 0);
        }
    }

    public void setDsOn(boolean on) {
        checkEffect();
        Log.i(TAG, "setDsOn: " + on);
        mDolbyAtmos.setDsOn(on);
        refreshPlaybackIfNecessary();
    }

    public boolean getDsOn() {
        boolean on = mDolbyAtmos.getDsOn();
        Log.i(TAG, "getDsOn: " + on);
        return on;
    }

    public void setProfile(int index) {
        checkEffect();
        Log.i(TAG, "setProfile: " + index);
        mDolbyAtmos.setProfile(index);
    }

    public int getProfile() {
        int profile = mDolbyAtmos.getProfile();
        Log.i(TAG, "getProfile: " + profile);
        return profile;
    }

    public void setPreset(String preset) {
        checkEffect();
        int[] gains = Arrays.stream(preset.split(",")).mapToInt(Integer::parseInt).toArray();
        Log.i(TAG, "setPreset: " + Arrays.toString(gains));
        mDolbyAtmos.setGeqBandGains(gains);
    }
}
