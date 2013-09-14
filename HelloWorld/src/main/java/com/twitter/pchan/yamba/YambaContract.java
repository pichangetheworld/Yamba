package com.twitter.pchan.yamba;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Owner: pchan Last Edited: 9/12/13.
 */
public class YambaContract {
    private YambaContract() { } // uninstantiable class, just definitions of constants, names

    // SELECT rowid as ... from ...

    public static final int VERSION = 1;

    public static final String AUTHORITY = "com.twitter.pchan.yamba.timeline";

    public static final Uri BASE_URI = new Uri.Builder()
            .scheme(ContentResolver.SCHEME_CONTENT)
            .authority(AUTHORITY)
            .build();

    // minor types
    // text/html
    // audio/mp3

    // in android: .../item or .../dir
    private static final String MINOR_TYPE = "/vnd." + AUTHORITY;

    public static final String ITEM_TYPE
            = ContentResolver.CURSOR_ITEM_BASE_TYPE + MINOR_TYPE;
    public static final String DIR_TYPE
            = ContentResolver.CURSOR_DIR_BASE_TYPE + MINOR_TYPE;

    public static class Timeline {
        private Timeline() { }

        public static final String TABLE = "timeline";

        public static final Uri URI = BASE_URI.buildUpon().appendPath(TABLE).build();

        public static class Columns {
            public static final String ID = BaseColumns._ID;
            public static final String USER = "user";
            public static final String STATUS = "status";
            public static final String TIME = "timestamp";

            public static final String MAX_TIMESTAMP = "max_ts";
        }
    }
}
