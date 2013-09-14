package com.twitter.pchan.yamba;

import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/**
 * Owner: pchan Last Edited: 9/13/13.
 */
public class TimelineFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TIMELINE_FRAG";

    private static final int TIMELINE_LOADER = 995;

    private static final String[] PROJ = new String[]{
            YambaContract.Timeline.Columns.ID,
            YambaContract.Timeline.Columns.USER,
            YambaContract.Timeline.Columns.TIME,
            YambaContract.Timeline.Columns.STATUS
    };

    // FROM must have the same number of fields as TO
    private static final String[] FROM = new String[PROJ.length - 1];

    static {
        System.arraycopy(PROJ, 1, FROM, 0, FROM.length);
    }

    private static final int[] TO = new int[]{
            R.id.timeline_row_user,
            R.id.timeline_row_time,
            R.id.timeline_row_status
    };

    static class TimelineBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
            if (R.id.timeline_row_time != view.getId()) {
                return false;
            }

            CharSequence s = "long ago";
            long t = cursor.getLong(columnIndex);
            if (t > 0) { s = DateUtils.getRelativeTimeSpanString(t); }
            ((TextView) view).setText(s);
            return true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "created"); }

        View v = super.onCreateView(inflater, container, savedInstanceState);

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                getActivity(),   // Context
                R.layout.fragment_timeline, //cellId, // view
                null,   // map Contents to the view
                FROM,   // contents -->         (array)
                TO,     // of sublist           (array)
                0
        );

        adapter.setViewBinder(new TimelineBinder());
        setListAdapter(adapter);

        // passing 'this' registers this as a listener for loadermanager
        getLoaderManager().initLoader(TIMELINE_LOADER, null, this);

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "requesting a loader");
        return new CursorLoader(
                getActivity(),
                YambaContract.Timeline.URI,     // ---> uses a URI i.e. a content provider!
                // does not reference a database
                PROJ,       // projection MUST contain a column called _ID, with integer primary key
                null,   // where clause
                null,   // arguments to where clause
                YambaContract.Timeline.Columns.TIME + " DESC"  // descending order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "load finished"); }
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(data);

        // populate the xml fields?
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "load reset"); }
        ((SimpleCursorAdapter) getListAdapter()).swapCursor(null);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "registered click"); }

        Cursor c = (Cursor) l.getItemAtPosition(position);

        //noinspection ConstantConditions
        Intent i = TimelineDetailFragment.marshallDetails(
                getActivity(),
                c.getLong(c.getColumnIndex(YambaContract.Timeline.Columns.TIME)),
                c.getString(c.getColumnIndex(YambaContract.Timeline.Columns.USER)),
                c.getString(c.getColumnIndex(YambaContract.Timeline.Columns.STATUS))
        );
        startActivity(i);

//        TimelineDetailActivity.showDetails(
//                getActivity(),
//                c.getLong(c.getColumnIndex(YambaContract.Timeline.Columns.TIME)),
//                c.getString(c.getColumnIndex(YambaContract.Timeline.Columns.USER)),
//                c.getString(c.getColumnIndex(YambaContract.Timeline.Columns.STATUS))
//        );
    }
}
