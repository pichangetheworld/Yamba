package com.twitter.pchan.yamba;

import android.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.twitter.pchan.yamba.svc.YambaService;

/**
 * Owner: pchan Last Edited: 9/13/13.
 */
public class StatusFragment extends Fragment {
    private static final String TAG = "STATUS_FRAGMENT";

    private EditText statusView;
    private TextView countView;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup parent, Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "created " + this); }

        //
        View v = layoutInflater.inflate(R.layout.fragment_status, parent, false);
        assert v != null;

        (v.findViewById(R.id.submit_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post();
            }
        });

        countView = (TextView) v.findViewById(R.id.status_count);
        statusView = (EditText) v.findViewById(R.id.status_text);
        statusView.addTextChangedListener(
                // Anonymous class
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        updateCount();
                    }
                }
        );

        return v;
    }

    private void updateCount() {
        int charsRemaining = getResources().getInteger(R.integer.status_limit) - statusView.length();
        countView.setText(String.valueOf(charsRemaining));

        if (charsRemaining <= getResources().getInteger(R.integer.err_limit)) {
            countView.setTextColor(getResources().getColor(R.color.err_color));
        } else if (charsRemaining <= getResources().getInteger(R.integer.warn_limit)) {
            countView.setTextColor(getResources().getColor(R.color.warn_color));
        } else {
            countView.setTextColor(getResources().getColor(R.color.status_color));
        }

        getActivity().findViewById(R.id.submit_button).setEnabled(charsRemaining >= 0 &&
                charsRemaining < getResources().getInteger(R.integer.status_limit));
    }

    void post() {
        //noinspection ConstantConditions
        String status = statusView.getText().toString();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "posting: " + status);
        }

        // NO!
        //try { Thread.sleep(4 * 60 * 100); }
        //catch (InterruptedException e) { }

        //if (null != poster) return;

        //poster = new Poster(getApplicationContext());
        statusView.setText("");

        // XXX TODO
        YambaService.post(getActivity(), status);

        //poster.execute(status/*, "foo", "bar"*/);
    }

    @Override
    public void onStart() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "started!");
        }
        super.onStart();
    }

    @Override
    public void onResume() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "resumed!");
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "paused...");
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "stopped..!");
        }
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "saving..!");
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "destroyed!");
        }
        super.onDestroy();
    }
}
