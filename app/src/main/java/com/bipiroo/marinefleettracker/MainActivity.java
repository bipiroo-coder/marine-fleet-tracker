package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
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
    private TextView summaryText;
    private String mode = "MY";
    private int pressCount = 0;

    private final int navy = Color.rgb(12, 33, 55);
    private final int blue = Color.rgb(20, 96, 168);
    private final int sky = Color.rgb(232, 243, 255);
    private final int card = Color.WHITE;
    private final int muted = Color.rgb(93, 103, 116);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            drawScreen();
            renderList("READY");
        } catch (Exception e) {
            TextView fallback = new TextView(this);
            fallback.setText("Marine Fleet Tracker\nSAFE MODE\n" + e.getClass().getSimpleName());
            fallback.setTextSize(22);
            fallback.setPadding(30, 30, 30, 30);
            setContentView(fallback);
        }
    }

    private void drawScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(18, 18, 18, 18);
        root.setBackgroundColor(Color.rgb(241, 246, 252));

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(22, 20, 22, 20);
        hero.setBackground(roundRect(navy, 28, 0));
        root.addView(hero, new LinearLayout.LayoutParams(-1, -2));

        TextView title = new TextView(this);
        title.setText("Marine Fleet Tracker");
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.WHITE);
        hero.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Fleet dashboard v0.3");
        sub.setTextSize(15);
        sub.setTextColor(Color.rgb(190, 215, 240));
        sub.setPadding(0, 4, 0, 0);
        hero.addView(sub);

        summaryText = new TextView(this);
        summaryText.setTextSize(18);
        summaryText.setTypeface(Typeface.DEFAULT_BOLD);
        summaryText.setTextColor(Color.WHITE);
        summaryText.setPadding(0, 16, 0, 0);
        hero.addView(summaryText);

        statusText = new TextView(this);
        statusText.setTextSize(15);
        statusText.setTextColor(Color.rgb(195, 230, 210));
        statusText.setPadding(0, 8, 0, 0);
        hero.addView(statusText);

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setGravity(Gravity.CENTER);
        row1.setPadding(0, 16, 0, 0);
        row1.addView(makeModeButton("우리회사", "MY"));
        row1.addView(makeModeButton("소형선망", "SMALL"));
        root.addView(row1);

        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setGravity(Gravity.CENTER);
        row2.addView(makeModeButton("대형선망", "LARGE"));
        row2.addView(makeModeButton("전체", "ALL"));
        row2.addView(makeRefreshButton());
        root.addView(row2);

        ScrollView scroll = new ScrollView(this);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        listBox.setPadding(0, 10, 0, 10);
        scroll.addView(listBox);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private Button makeModeButton(String label, String value) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(roundRect(blue, 22, 0));
        button.setPadding(14, 10, 14, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);
        button.setOnClickListener(v -> {
            mode = value;
            pressCount++;
            renderList(label + " selected");
        });
        return button;
    }

    private Button makeRefreshButton() {
        Button button = new Button(this);
        button.setText("갱신");
        button.setAllCaps(false);
        button.setTextSize(16);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setBackground(roundRect(Color.rgb(28, 145, 97), 22, 0));
        button.setPadding(14, 10, 14, 10);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(5, 5, 5, 5);
        button.setLayoutParams(params);
        button.setOnClickListener(v -> {
            pressCount++;
            renderList("refresh complete");
        });
        return button;
    }

    private void renderList(String message) {
        listBox.removeAllViews();
        int total = countMode();
        summaryText.setText(modeLabel() + " · " + total + " vessels");
        statusText.setText(message + " · taps " + pressCount + " · stable screen");

        TextView info = new TextView(this);
        info.setText("지도와 실시간 위치를 붙이기 전 단계입니다. 지금은 화면 반응, 선박 분류, 기본 관제판 디자인을 확인합니다.");
        info.setTextSize(15);
        info.setTextColor(muted);
        info.setPadding(16, 16, 16, 16);
        info.setBackground(roundRect(sky, 22, 0));
        LinearLayout.LayoutParams infoParams = new LinearLayout.LayoutParams(-1, -2);
        infoParams.setMargins(0, 8, 0, 12);
        listBox.addView(info, infoParams);

        int count = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            count++;
            listBox.addView(vesselCard(count, vessel));
        }

        TextView footer = new TextView(this);
        footer.setText("표시 선박 수: " + count + "척");
        footer.setTextSize(17);
        footer.setTypeface(Typeface.DEFAULT_BOLD);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(navy);
        footer.setPadding(10, 18, 10, 18);
        listBox.addView(footer);
    }

    private TextView vesselCard(int index, Vessel vessel) {
        TextView row = new TextView(this);
        String typeIcon = vessel.fishingType == FishingType.SMALL_PURSE_SEINE ? "S" : "L";
        String own = vessel.isMyCompany ? " · MY" : "";
        row.setText(typeIcon + "  " + index + ". " + vessel.name + own + "\n"
                + vessel.company + " / " + vessel.fleet + "\n"
                + vessel.fishingType.label + " · MMSI " + vessel.mmsi + "\n"
                + "상태: 대기 · 위치자료 연결 예정");
        row.setTextSize(17);
        row.setLineSpacing(3, 1.05f);
        row.setTextColor(Color.rgb(25, 35, 45));
        row.setPadding(20, 18, 20, 18);
        row.setBackground(roundRect(card, 24, Color.rgb(218, 228, 238)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, 7, 0, 7);
        row.setLayoutParams(params);
        return row;
    }

    private int countMode() {
        int count = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (matches(vessel)) count++;
        }
        return count;
    }

    private String modeLabel() {
        if ("MY".equals(mode)) return "우리회사";
        if ("SMALL".equals(mode)) return "소형선망";
        if ("LARGE".equals(mode)) return "대형선망";
        return "전체선망";
    }

    private boolean matches(Vessel vessel) {
        if ("MY".equals(mode)) return vessel.isMyCompany;
        if ("SMALL".equals(mode)) return vessel.fishingType == FishingType.SMALL_PURSE_SEINE;
        if ("LARGE".equals(mode)) return vessel.fishingType == FishingType.LARGE_PURSE_SEINE;
        return true;
    }

    private GradientDrawable roundRect(int fill, int radius, int stroke) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(fill);
        drawable.setCornerRadius(radius);
        if (stroke != 0) drawable.setStroke(2, stroke);
        return drawable;
    }
}
