package com.twitter.pchan.yamba;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Owner: pchan Last Edited: 9/13/13.
 */
public class TimelineDetailActivity extends YambaActivity {
    private static final String TAG = "DETAILS";

    public TimelineDetailActivity() {
        super(TAG, R.layout.activity_timeline_detail);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert ((TimelineDetailFragment)getFragmentManager()
         .findFragmentById(R.id.fragment_timeline_details)) != null;
        ((TimelineDetailFragment) getFragmentManager()
         .findFragmentById(R.id.fragment_timeline_details))
         .setDetails(getIntent().getExtras());
    }
}
