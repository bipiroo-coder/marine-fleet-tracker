package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView view = new TextView(this);
        view.setText("Marine Fleet Tracker");
        view.setTextSize(24);
        setContentView(view);
    }
}
