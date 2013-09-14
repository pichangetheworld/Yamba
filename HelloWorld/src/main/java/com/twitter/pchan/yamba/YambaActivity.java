package com.twitter.pchan.yamba;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * Owner: pchan Last Edited: 9/13/13.
 */
public class YambaActivity extends Activity {
    protected String TAG;
    protected int layoutResId;

    public YambaActivity(String tag, int layoutId) {
        TAG = tag; layoutResId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "created"); }
        super.onCreate(savedInstanceState);
        setContentView(layoutResId);
    }
}
