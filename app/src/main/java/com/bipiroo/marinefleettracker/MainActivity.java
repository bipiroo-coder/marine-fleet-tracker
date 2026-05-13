package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
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
    private TextView statusText;
    private String mode = "MY";
    private int pressCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            drawScreen();
            renderList("앱 시작 완료");
        } catch (Exception e) {
            TextView fallback = new TextView(this);
            fallback.setText("Marine Fleet Tracker\n오류 화면\n" + e.getClass().getSimpleName());
            fallback.setTextSize(22);
            fallback.setPadding(30, 30, 30, 30);
            setContentView(fallback);
        }
    }

    private void drawScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(Color.WHITE);

        TextView title = new TextView(this);
        title.setText("Marine Fleet Tracker v0.2");
        title.setTextSize(26);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.BLACK);
        root.addView(title);

        statusText = new TextView(this);
        statusText.setTextSize(17);
        statusText.setTextColor(Color.rgb(0, 80, 0));
        statusText.setGravity(Gravity.CENTER);
        statusText.setPadding(0, 12, 0, 12);
        root.addView(statusText);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER);
        row1.addView(makeModeButton("우리회사", "MY"));
        row1.addView(makeModeButton("소형", "SMALL"));
        root.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setGravity(Gravity.CENTER);
        row2.addView(makeModeButton("대형", "LARGE"));
        row2.addView(makeModeButton("전체", "ALL"));
        row2.addView(makeRefreshButton());
        root.addView(row2);

        ScrollView scroll = new ScrollView(this);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(listBox);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private Button makeModeButton(String label, String value) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(18);
        button.setOnClickListener(v -> {
            mode = value;
            pressCount++;
            renderList(label + " 버튼 작동");
        });
        return button;
    }

    private Button makeRefreshButton() {
        Button button = new Button(this);
        button.setText("새로고침");
        button.setAllCaps(false);
        button.setTextSize(18);
        button.setOnClickListener(v -> {
            pressCount++;
            renderList("새로고침 작동");
        });
        return button;
    }

    private void renderList(String message) {
        listBox.removeAllViews();
        statusText.setText(message + " | 화면: " + mode + " | 클릭: " + pressCount);

        TextView info = new TextView(this);
        info.setText("현재는 안정성 확인용 화면입니다. 이 화면에서 버튼이 바뀌면 앱은 정상 작동 중입니다. 다음 단계에서 지도와 위치 표시를 붙입니다.");
        info.setTextSize(16);
        info.setTextColor(Color.DKGRAY);
        info.setPadding(10, 18, 10, 18);
        listBox.addView(info);

        int count = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            count++;
            TextView row = new TextView(this);
            row.setText(count + ". " + vessel.name + "\n회사: " + vessel.company + "\n선단: " + vessel.fleet + "\n어업: " + vessel.fishingType.label + "\nMMSI: " + vessel.mmsi);
            row.setTextSize(18);
            row.setTextColor(Color.BLACK);
            row.setPadding(18, 18, 18, 18);
            listBox.addView(row);
        }

        TextView footer = new TextView(this);
        footer.setText("표시 선박 수: " + count);
        footer.setTextSize(18);
        footer.setTypeface(Typeface.DEFAULT_BOLD);
        footer.setPadding(10, 24, 10, 24);
        listBox.addView(footer);
    }

    private boolean matches(Vessel vessel) {
        if ("MY".equals(mode)) return vessel.isMyCompany;
        if ("SMALL".equals(mode)) return vessel.fishingType == FishingType.SMALL_PURSE_SEINE;
        if ("LARGE".equals(mode)) return vessel.fishingType == FishingType.LARGE_PURSE_SEINE;
        return true;
    }
}
