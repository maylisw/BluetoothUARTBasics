package com.adafruit.bluefruit.le.connect;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

public class BluefruitApplication extends Application {
    // Log
    private final static String TAG = BluefruitApplication.class.getSimpleName();

    // Data
    private static boolean mIsActivityVisible;
    private RefWatcher mRefWatcher;

    // region Lifecycle
    @Override
    public void onCreate() {
        super.onCreate();

        // Setup LeakCanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mRefWatcher = LeakCanary.install(this);


        // Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler(this::handleUncaughtException);
    }

    // endregion


    // region Detect app in background: https://stackoverflow.com/questions/3667022/checking-if-an-android-application-is-running-in-the-background

    public static boolean isActivityVisible() {
        return mIsActivityVisible;
    }

    public static void activityResumed() {
        mIsActivityVisible = true;
    }

    public static void activityPaused() {
        mIsActivityVisible = false;
    }

    // endregion
}
