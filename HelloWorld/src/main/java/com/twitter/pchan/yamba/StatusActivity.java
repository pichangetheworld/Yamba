package com.twitter.pchan.yamba;

import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.pchan.yamba.svc.YambaService;

public class StatusActivity extends YambaActivity {
    public static final String TAG = "STATUSACT";

    public StatusActivity() {
        super(TAG, R.layout.activity_status);
    }

    // On Java, the following prints out the time since loaded
    // On Android, the process is killed and restarted multiple times - this number is useless!
    //
    //public static final long BOOT_TIME = System.currentTimeMillis();
    //public static void timeSinceBoot() {
    //    System.out.println(System.currentTimeMillis() - BOOT_TIME);
    //}

//    private EditText statusView;
//    private TextView countView;

//    static class Poster extends AsyncTask<String, Void, Integer> {
//
//        private final Context ctxt;
//
//        public Poster(Context ctxt) { this.ctxt = ctxt; }
//
//        // doInBackground is GUARANTEED to run in the background thread!! (!= UI Thread)
//        @Override
//        protected Integer doInBackground(String... status) {
//            int message = R.string.fail;
//            try {
//                client.postStatus(status[0]);
//                message = R.string.success;
//            } catch (YambaClientException e) {
//                Log.e(TAG, "Post failed...");
//                e.printStackTrace();
//            }
//            return message;
//        }
//
//        // running on UI thread
//        @Override
//        protected void onPostExecute(Integer result) {
//            Toast.makeText(ctxt, result, Toast.LENGTH_LONG).show();
//            //poster = null;
//        }
//    }

    //static Poster poster;

    // Everything else has been moved to the StatusFragment
}
