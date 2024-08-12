package com.termux.shared.termux.terminal.io;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;

import com.termux.shared.termux.settings.preferences.TermuxPreferenceConstants;


public class BellHandler {
    private static BellHandler instance = null;
    private static final Object lock = new Object();

    public static BellHandler getInstance(Context context) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new BellHandler((Vibrator) context.getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE));
                }
            }
        }

        return instance;
    }

    private static final long DURATION = 50;
    private static final long MIN_PAUSE = 3 * DURATION;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private long lastBell = 0;
    private final Runnable bellRunnable;

    private BellHandler(final Vibrator vibrator) {
        bellRunnable = new Runnable() {
            @Override
            public void run() {
                if (vibrator != null) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                vibrator.vibrate(VibrationEffect.createOneShot(DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(DURATION);
                            }
                        } catch (Exception e) {
                            // Issue on samsung devices on android 8
                            // java.lang.NullPointerException: Attempt to read from field 'android.os.VibrationEffect com.android.server.VibratorService$Vibration.mEffect' on a null object reference
                        }
                }
            }
        };
    }

    public synchronized void doBell() {
        long now = now();
        long timeSinceLastBell = now - lastBell;

        if (timeSinceLastBell < 0) {
            // there is a next bell pending; don't schedule another one
        } else if (timeSinceLastBell < MIN_PAUSE) {
            // there was a bell recently, schedule the next one
            handler.postDelayed(bellRunnable, MIN_PAUSE - timeSinceLastBell);
            lastBell = lastBell + MIN_PAUSE;
        } else {
            // the last bell was long ago, do it now
            bellRunnable.run();
            lastBell = now;
        }
    }

    private long now() {
        return SystemClock.uptimeMillis();
    }
}
