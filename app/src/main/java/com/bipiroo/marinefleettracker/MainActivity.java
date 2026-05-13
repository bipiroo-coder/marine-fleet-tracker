package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bipiroo.marinefleettracker.data.FleetSeed;
import com.bipiroo.marinefleettracker.model.FishingType;
import com.bipiroo.marinefleettracker.model.Vessel;

public class MainActivity extends Activity {
    private LinearLayout listBox;
    private String mode = "MY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(16, 16, 16, 16);

        TextView title = new TextView(this);
        title.setText("Marine Fleet Tracker");
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setGravity(Gravity.CENTER);

        Button my = makeButton("My");
        Button small = makeButton("Small");
        Button large = makeButton("Large");
        Button all = makeButton("All");

        my.setOnClickListener(v -> { mode = "MY"; renderList(); });
        small.setOnClickListener(v -> { mode = "SMALL"; renderList(); });
        large.setOnClickListener(v -> { mode = "LARGE"; renderList(); });
        all.setOnClickListener(v -> { mode = "ALL"; renderList(); });

        buttons.addView(my);
        buttons.addView(small);
        buttons.addView(large);
        buttons.addView(all);
        root.addView(buttons);

        ScrollView scrollView = new ScrollView(this);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(listBox);
        root.addView(scrollView, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
        renderList();
    }

    private Button makeButton(String text) {
        Button button = new Button(this);
        button.setText(text);
        button.setAllCaps(false);
        return button;
    }

    private void renderList() {
        listBox.removeAllViews();

        TextView header = new TextView(this);
        header.setText("Mode: " + mode);
        header.setTextSize(18);
        header.setTypeface(Typeface.DEFAULT_BOLD);
        header.setPadding(0, 20, 0, 12);
        listBox.addView(header);

        int count = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            count++;
            TextView row = new TextView(this);
            row.setText(vessel.name + "\n" + vessel.company + " / " + vessel.fleet + " / " + vessel.fishingType.label + "\nMMSI: " + vessel.mmsi);
            row.setTextSize(16);
            row.setPadding(16, 16, 16, 16);
            listBox.addView(row);
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No vessels");
            empty.setTextSize(16);
            empty.setPadding(16, 16, 16, 16);
            listBox.addView(empty);
        }
    }

    private boolean matches(Vessel vessel) {
        if ("MY".equals(mode)) return vessel.isMyCompany;
        if ("SMALL".equals(mode)) return vessel.fishingType == FishingType.SMALL_PURSE_SEINE;
        if ("LARGE".equals(mode)) return vessel.fishingType == FishingType.LARGE_PURSE_SEINE;
        return true;
    }
}
