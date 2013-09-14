package com.twitter.pchan.yamba;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Owner: pchan Last Edited: 9/13/13.
 */
public class TimelineDetailFragment extends Fragment {
    private static final String TAG = "DETAIL_FRAGMENT";

    private View details;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "created"); }

        View v = inflater.inflate(R.layout.fragment_timeline_detail, container, false);
        assert v != null;
        details = v.findViewById(R.id.timeline_details);

        // if this is not using fragments
//        setDetails(getActivity().getIntent().getExtras());
        // if this is using fragments
        setDetails(getArguments());

        return v;
    }

    public void setDetails(Bundle args) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "args: " + args + ", details: " + details);
        }

        if ((args == null) || (details == null)) return;

        ((TextView) details.findViewById(R.id.timeline_detail_time))
                .setText(DateUtils.getRelativeTimeSpanString(
                        args.getLong(YambaContract.Timeline.Columns.TIME, 0)));
        ((TextView) details.findViewById(R.id.timeline_detail_status))
                .setText(args.getString(YambaContract.Timeline.Columns.STATUS));
        ((TextView) details.findViewById(R.id.timeline_detail_user))
                .setText(args.getString(YambaContract.Timeline.Columns.USER));
    }

    public static Fragment newInstance(Bundle args) {
        Fragment details = new TimelineDetailFragment();
        details.setArguments(args);
        return details;
    }

    public static Intent marshallDetails(Context ctxt, long timestamp, String user, String status) {
        Intent i = new Intent(ctxt, TimelineDetailActivity.class);
        i.putExtra(YambaContract.Timeline.Columns.TIME, timestamp);
        i.putExtra(YambaContract.Timeline.Columns.USER, user);
        i.putExtra(YambaContract.Timeline.Columns.STATUS, status);
        return i;
    }
}
