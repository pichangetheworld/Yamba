package com.twitter.pchan.yamba.svc;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import com.twitter.pchan.yamba.BuildConfig;
import com.twitter.pchan.yamba.R;
import com.twitter.pchan.yamba.YambaContract;

import com.marakana.android.yamba.clientlib.YambaClient;
import com.marakana.android.yamba.clientlib.YambaClientException;

import java.util.ArrayList;
import java.util.List;

/**
 * Owner: pchan Last Edited: 9/11/13.
 */
public class YambaService extends IntentService {
    private static final String TAG = "SVC";

    private static final String PARAM_STATUS = "YambaService.STATUS";
    private static final String PARAM_OP = "YambaService.OP";

    private static final int POLLER = 661;
    private static final int OP_STATUS = 0;
    private static final int OP_POLLER = 1;

    static volatile YambaClient client;

    private volatile int pollSize;

//    private volatile YambaDBHelper db;

    public YambaService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        pollSize = getResources().getInteger(R.integer.poll_num_stats);

        client = new YambaClient("student", "password");

        // NEVER DO THIS. This will kill the UI thread (if it dies, GG)
//        db = new YambaDBHelper(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static void post(Context ctxt, String status) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "posting " + status);
        }
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_STATUS, status);
        i.putExtra(PARAM_OP, OP_STATUS);
        ctxt.startService(i);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // This is running on a different thread (NOT the UI thread)
        Log.d(TAG, "handling intent");

        switch (intent.getIntExtra(PARAM_OP, 0)) {
            case OP_POLLER:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "poller intent received!");
                }
                poll();
                break;
            case OP_STATUS:
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "post intent received!");
                }
                try {
                    client.postStatus(intent.getStringExtra(PARAM_STATUS));
                } catch (YambaClientException e) {
                    Log.e(TAG, "Post failed...");
                    e.printStackTrace();
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Post succeeded!");
                }
                break;
            default:

        }
    }

    public static void startPoller(Context ctxt) {
        ((AlarmManager) ctxt.getSystemService(Context.ALARM_SERVICE)).setInexactRepeating(
                AlarmManager.RTC,   // Real-time Clock
                System.currentTimeMillis() + 100, // the time the first call is made, in ms
                // ALWAYS + 100, otherwise may schedule in past
                ctxt.getResources().getInteger(R.integer.poll_interval) * 60 * 1000,          // polling interval
                createPollingIntent(ctxt)
        );
    }

    private static PendingIntent createPollingIntent(Context ctxt) {
        Intent i = new Intent(ctxt, YambaService.class);
        i.putExtra(PARAM_OP, OP_POLLER);
        return PendingIntent.getService(ctxt, POLLER, i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void poll() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Polling...");
        }
        try {
            parseTimeLine(client.getTimeline(pollSize));
        } catch (YambaClientException e) {
            e.printStackTrace();
        }
    }

    private int parseTimeLine(List<YambaClient.Status> timeline) {
        long latest = getLatestStatusTime();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "latest: " + latest);
        }

        List<ContentValues> vals = new ArrayList<ContentValues>();

        for (YambaClient.Status status : timeline) {
            long t = status.getCreatedAt().getTime();
            if (t <= latest) {
                continue;
            }

            // A contentvalue is a row in the DB
            ContentValues cv = new ContentValues();
            cv.put(YambaContract.Timeline.Columns.ID, status.getId());
            cv.put(YambaContract.Timeline.Columns.TIME, status.getCreatedAt().getTime());
            cv.put(YambaContract.Timeline.Columns.USER, status.getUser());
            cv.put(YambaContract.Timeline.Columns.STATUS, status.getMessage());
//            if (0 < database.insert(YambaDBHelper.TABLE_TIMELINE, null, cv)) {
//                i++;
//            }
            vals.add(cv);

            if (BuildConfig.DEBUG) {
                Log.d(TAG, "Id: " + status.getId());
                Log.d(TAG, "Timestamp: " + status.getCreatedAt());
                Log.d(TAG, "User: " + status.getUser());
                Log.d(TAG, "Message: " + status.getMessage());
            }
        }

        int n = vals.size();
        if (n <= 0) { return 0; }
        getContentResolver().bulkInsert(
                YambaContract.Timeline.URI,
                vals.toArray(new ContentValues[n]));

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "inserted: " + n);
        }
        return n;
    }

    private long getLatestStatusTime() {
        Cursor c = null;
        try {
            c = getContentResolver().query(
                    YambaContract.Timeline.URI,
                    new String[]{ YambaContract.Timeline.Columns.MAX_TIMESTAMP },
                    null,   // where clause,    e.g. where foo = ? and bar = ?
                    null,   // ... (where args) e.g.        [ 7, "baz" ]
                    null);  // order by
            return ((null == c) || (!c.moveToNext())
                    ? Long.MIN_VALUE : c.getLong(0));
        } finally {
            if (null != c) {
                c.close();
            }
        }
    }
}
