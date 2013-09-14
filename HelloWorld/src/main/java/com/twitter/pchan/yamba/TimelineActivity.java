package com.twitter.pchan.yamba;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Owner: pchan Last Edited: 9/12/13.
 */
public class TimelineActivity extends YambaActivity {
    private static final String TAG = "TIMELINE_ACT";

    private static final String DETAILS_TAG = "TimelineActivity";

    private boolean usingFragments;

    public TimelineActivity() {
        super(TAG, R.layout.activity_timeline);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        usingFragments = (findViewById(R.id.fragment_timeline_detail) != null);

        if (BuildConfig.DEBUG) { Log.d(TAG, "using fragments is: " + usingFragments); }

        if (usingFragments) { addDetailFragment(); }
    }

    private void addDetailFragment() {
        FragmentManager mgr = getFragmentManager();

        // if fragment already exists, we're done
        if (mgr.findFragmentByTag(DETAILS_TAG) != null) { return; }

        // otherwise we need to create the fragment
        FragmentTransaction xact = mgr.beginTransaction();
        xact.add(
                R.id.fragment_timeline_detail,
                TimelineDetailFragment.newInstance(null),
                DETAILS_TAG
        );
        xact.commit();
    }

    public void showDetails(long timestamp, String user, String status) {
        Bundle args = new Bundle();
        args.putLong(YambaContract.Timeline.Columns.TIME, timestamp);
        args.putString(YambaContract.Timeline.Columns.USER, user);
        args.putString(YambaContract.Timeline.Columns.STATUS, status);

        FragmentTransaction xact = getFragmentManager().beginTransaction();
        xact.replace(
                R.id.fragment_timeline_detail,
                TimelineDetailFragment.newInstance(args),
                DETAILS_TAG
        );
        xact.addToBackStack(null);
        xact.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        xact.commit();
    }

    @Override
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        if (usingFragments) {
            //noinspection ConstantConditions
            showDetails(
                    intent.getExtras().getLong(YambaContract.Timeline.Columns.TIME, 0),
                    intent.getExtras().getString(YambaContract.Timeline.Columns.USER),
                    intent.getExtras().getString(YambaContract.Timeline.Columns.STATUS)
            );
        } else {
            super.startActivityFromFragment(fragment, intent, requestCode);
        }
    }
}
