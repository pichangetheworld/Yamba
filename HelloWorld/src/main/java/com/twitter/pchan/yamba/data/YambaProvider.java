package com.twitter.pchan.yamba.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.twitter.pchan.yamba.YambaContract;

import java.util.Map;

/**
 * Owner: pchan Last Edited: 9/12/13.
 */
public class YambaProvider extends ContentProvider {
    private static final String TAG = "PROVIDER";

    private static final int TIMELINE_ITEM_TYPE = 1;
    private static final int TIMELINE_DIR_TYPE = 2;

    //  scheme             authority                 path   [id]
    // content://com.twitter.pchan.yamba.timeline/timeline/7

    // content://android.contacts/contact/7/phone/2 <-- Android also supports this but complex
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        MATCHER.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM_TYPE);
        MATCHER.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE,
                TIMELINE_DIR_TYPE);
    }

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
            .addColumn(YambaContract.Timeline.Columns.ID, YambaDBHelper.COL_ID, ColumnMap.Type.LONG)
            .addColumn(YambaContract.Timeline.Columns.TIME, YambaDBHelper.COL_TIME, ColumnMap.Type.LONG)
            .addColumn(YambaContract.Timeline.Columns.USER, YambaDBHelper.COL_USER, ColumnMap.Type.STRING)
            .addColumn(YambaContract.Timeline.Columns.STATUS, YambaDBHelper.COL_MSG, ColumnMap.Type.STRING)
            .build();

    private static final Map<String, String> PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
            .addColumn(YambaContract.Timeline.Columns.ID, YambaDBHelper.COL_ID)
            .addColumn(YambaContract.Timeline.Columns.TIME, YambaDBHelper.COL_TIME)
            .addColumn(YambaContract.Timeline.Columns.USER, YambaDBHelper.COL_USER)
            .addColumn(YambaContract.Timeline.Columns.STATUS, YambaDBHelper.COL_MSG)
            .addColumn(
                    YambaContract.Timeline.Columns.MAX_TIMESTAMP,
                    "max(" + YambaDBHelper.COL_TIME + ")")
            .build().getProjectionMap();

    private YambaDBHelper dbHelper;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "provider created");
        dbHelper = new YambaDBHelper(getContext());
        //noinspection ConstantConditions
        return null != dbHelper;
    }

    @Override
    public Cursor query(Uri uri,
                        String[] projection,
                        String selection,
                        String[] selectionArgs,
                        String sortOrder) {
        Log.d(TAG, "query");

        String table;
        long pk = -1;
        switch (MATCHER.match(uri)) {
            case TIMELINE_ITEM_TYPE:
                pk = ContentUris.parseId(uri);
            case TIMELINE_DIR_TYPE:
                table = YambaDBHelper.TABLE_TIMELINE;
                break;
            default:
                throw new IllegalArgumentException("Unexpected uri: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        //noinspection ConstantConditions
        qb.setTables(table);

        // SELECT virt1 AS phys1, virt2 AS phys2, FROM virtTable WHERE virt1 IS NOT NULL
        //        ORDER BY virt2
        qb.setProjectionMap(PROJ_MAP_TIMELINE);

        if (pk > 0) { qb.appendWhere(YambaDBHelper.COL_ID + "=" + pk); }

        return qb.query(getDb(), projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case TIMELINE_ITEM_TYPE:
                return YambaContract.ITEM_TYPE;
            case TIMELINE_DIR_TYPE:
                return YambaContract.DIR_TYPE;
        }
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] rows) {
        Log.d(TAG, "insert: " + rows.length);

        String table;
        switch (MATCHER.match(uri)) {
//            case TIMELINE_ITEM_TYPE: <-- don't want to insert items
            case TIMELINE_DIR_TYPE:
                table = YambaDBHelper.TABLE_TIMELINE;
                break;
            default:
                throw new IllegalArgumentException("Unexpected uri: " + uri);
        }

        int count = 0;

        SQLiteDatabase db = getDb();
        try {
            db.beginTransaction();
            for (ContentValues row : rows) {
                if (db.insert(table,
                        null,
                        COL_MAP_TIMELINE.translateCols(row)) > 0) {
                    ++count;
                }
            }
            db.setTransactionSuccessful();
        } // instead of catching all (kinds of) exceptions, endTransaction() (checks all/nothing)
        finally {
            db.endTransaction();
        }

        return count;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException("insert not supported");
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("delete not supported");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("update not supported");
    }

    private SQLiteDatabase getDb() {
        return dbHelper.getWritableDatabase();
    }
}
