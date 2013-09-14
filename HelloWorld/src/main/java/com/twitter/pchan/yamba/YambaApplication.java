package com.twitter.pchan.yamba;

import android.app.Application;
import android.util.Log;

import com.twitter.pchan.yamba.svc.YambaService;

/**
 * Owner: pchan Last Edited: 9/11/13.
 */
public class YambaApplication extends Application {
    private static final String TAG = "YambaApplication";

    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Application started");
        }
        YambaService.startPoller(this);
    }
}
