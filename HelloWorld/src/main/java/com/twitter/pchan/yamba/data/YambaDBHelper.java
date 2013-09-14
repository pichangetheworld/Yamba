package com.twitter.pchan.yamba.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Owner: pchan Last Edited: 9/11/13.
 */
class YambaDBHelper extends SQLiteOpenHelper {
    private static final String TAG = "DBHELPER";

    public static final String DATABASE = "yamba.db";
    public static final int VERSION = 2;

    public static final String TABLE_TIMELINE_V1 = "timeline";
    public static final String TABLE_TIMELINE = "p_timeline";
    public static final String COL_ID = "p_id";
    public static final String COL_TIME = "p_timestamp";
    public static final String COL_USER = "p_user";
    public static final String COL_MSG = "p_message";

    public YambaDBHelper(Context context) {
        super(context, DATABASE, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating db");
        db.execSQL(
                "CREATE TABLE " + TABLE_TIMELINE + "(" +
                        // stuff in the database
                        COL_ID + " INTEGER PRIMARY KEY, " +
                        COL_TIME + " INTEGER NOT NULL, " +
                        COL_USER + " STRING NOT NULL, " +
                        COL_MSG + " STRING" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE " + TABLE_TIMELINE_V1);
        onCreate(db);
    }
}
