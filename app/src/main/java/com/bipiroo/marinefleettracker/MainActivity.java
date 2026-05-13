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
    private LinearLayout bodyBox;
    private TextView statusText;
    private TextView summaryText;
    private String mode = "MY";
    private String page = "DASH";
    private int pressCount = 0;

    private final int navy = Color.rgb(10, 28, 48);
    private final int blue = Color.rgb(22, 97, 170);
    private final int green = Color.rgb(29, 145, 94);
    private final int sky = Color.rgb(232, 243, 255);
    private final int bg = Color.rgb(241, 246, 252);
    private final int muted = Color.rgb(88, 101, 118);
    private final int line = Color.rgb(218, 228, 238);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            drawScreen();
            render("통합판 시작");
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
        root.setPadding(16, 16, 16, 16);
        root.setBackgroundColor(bg);

        LinearLayout hero = new LinearLayout(this);
        hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(22, 20, 22, 20);
        hero.setBackground(roundRect(navy, 30, 0));
        root.addView(hero, new LinearLayout.LayoutParams(-1, -2));

        TextView title = new TextView(this);
        title.setText("Marine Fleet Tracker");
        title.setTextSize(27);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setTextColor(Color.WHITE);
        hero.addView(title);

        TextView sub = new TextView(this);
        sub.setText("Integrated MVP v0.4 · 선단 관제 / 지도 / 위판 / 통계");
        sub.setTextSize(14);
        sub.setTextColor(Color.rgb(190, 215, 240));
        sub.setPadding(0, 5, 0, 0);
        hero.addView(sub);

        summaryText = new TextView(this);
        summaryText.setTextSize(18);
        summaryText.setTypeface(Typeface.DEFAULT_BOLD);
        summaryText.setTextColor(Color.WHITE);
        summaryText.setPadding(0, 16, 0, 0);
        hero.addView(summaryText);

        statusText = new TextView(this);
        statusText.setTextSize(14);
        statusText.setTextColor(Color.rgb(200, 235, 212));
        statusText.setPadding(0, 8, 0, 0);
        hero.addView(statusText);

        root.addView(buttonRow(new String[][]{{"관제", "DASH"}, {"지도", "MAP"}, {"위판", "AUCTION"}}));
        root.addView(buttonRow(new String[][]{{"통계", "STATS"}, {"설정", "SETTINGS"}, {"갱신", "REFRESH"}}));
        root.addView(buttonRow(new String[][]{{"우리회사", "MY"}, {"소형", "SMALL"}, {"대형", "LARGE"}, {"전체", "ALL"}}));

        ScrollView scroll = new ScrollView(this);
        bodyBox = new LinearLayout(this);
        bodyBox.setOrientation(LinearLayout.VERTICAL);
        bodyBox.setPadding(0, 10, 0, 16);
        scroll.addView(bodyBox);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        setContentView(root);
    }

    private LinearLayout buttonRow(String[][] items) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(0, 7, 0, 0);
        for (String[] item : items) {
            row.addView(makeButton(item[0], item[1]));
        }
        return row;
    }

    private Button makeButton(String label, String value) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(15);
        button.setTextColor(Color.WHITE);
        button.setTypeface(Typeface.DEFAULT_BOLD);
        button.setPadding(10, 9, 10, 9);
        button.setBackground(roundRect(isModeValue(value) ? blue : green, 22, 0));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(4, 3, 4, 3);
        button.setLayoutParams(params);
        button.setOnClickListener(v -> {
            pressCount++;
            if ("REFRESH".equals(value)) {
                render("새로고침 완료");
            } else if (isModeValue(value)) {
                mode = value;
                render(label + " 필터");
            } else {
                page = value;
                render(label + " 화면");
            }
        });
        return button;
    }

    private boolean isModeValue(String value) {
        return "MY".equals(value) || "SMALL".equals(value) || "LARGE".equals(value) || "ALL".equals(value);
    }

    private void render(String message) {
        bodyBox.removeAllViews();
        int shown = countMode();
        summaryText.setText(pageLabel() + " · " + modeLabel() + " · " + shown + "척");
        statusText.setText(message + " · taps " + pressCount + " · 외부 API 미연결 안전모드");

        if ("DASH".equals(page)) renderDashboard();
        else if ("MAP".equals(page)) renderMap();
        else if ("AUCTION".equals(page)) renderAuction();
        else if ("STATS".equals(page)) renderStats();
        else renderSettings();
    }

    private void renderDashboard() {
        addNotice("통합 관제 화면입니다. 현재는 샘플 선박 기준이며, 실제 AIS/어시장 데이터는 설정에서 연결하는 구조로 확장합니다.");
        addKpiRow("현재 표시", countMode() + "척", "월 추정 위판", formatMoney(totalMonthAmount()) + "원");
        addKpiRow("오늘 추정 상자", totalTodayBoxes() + "상자", "연 추정 위판", formatMoney(totalYearAmount()) + "원");
        int i = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            i++;
            addVesselCard(i, vessel, true);
        }
    }

    private void renderMap() {
        addNotice("간이 지도형 화면입니다. 외부 지도 라이브러리 없이 먼저 해상 구역·선박 위치 흐름을 확인하게 만들었습니다.");
        TextView map = new TextView(this);
        map.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
        map.setTextSize(15);
        map.setTextColor(Color.rgb(20, 55, 85));
        map.setText("북\n┌────────────────────┐\n│     S1       L1     │\n│                    │\n│  MY1        S2      │\n│                    │\n│        MY2          │\n└────────────────────┘\n남\n\nMY=우리회사, S=소형선망, L=대형선망");
        map.setPadding(18, 18, 18, 18);
        map.setBackground(roundRect(Color.rgb(222, 238, 251), 24, line));
        addView(map, 0, 8, 0, 12);

        int i = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            i++;
            TextView row = cardText("위치 " + i + " · " + vessel.name + "\n구역: " + zoneFor(i) + " · 속력: " + speedFor(i) + "kn · 상태: " + stateFor(i) + "\n최종수신: 샘플 데이터 · 실제 AIS 연결 예정", 16, navy, Color.WHITE);
            addView(row, 0, 6, 0, 6);
        }
    }

    private void renderAuction() {
        addNotice("위판 화면입니다. 부산·제주·전남·경남 위판 데이터 연결 전까지는 샘플 계산값으로 화면 구조를 확인합니다.");
        int i = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            i++;
            int today = boxesFor(i);
            int month = today * 12;
            long amount = (long) month * 38000L;
            TextView row = cardText(vessel.name + "\n오늘: " + today + "상자 · 이번달: " + month + "상자\n월 위판 추정: " + formatMoney(amount) + "원\n최근 위판장: 자동연동 예정", 17, navy, Color.WHITE);
            addView(row, 0, 7, 0, 7);
        }
    }

    private void renderStats() {
        addNotice("월별·연도별·선박별·회사별 구분 화면입니다. 실제 위판 데이터가 들어오면 이 계산식에 연결됩니다.");
        addKpiRow("월 전체", formatMoney(totalMonthAmount()) + "원", "연 전체", formatMoney(totalYearAmount()) + "원");
        addKpiRow("소형선망", countType(FishingType.SMALL_PURSE_SEINE) + "척", "대형선망", countType(FishingType.LARGE_PURSE_SEINE) + "척");
        TextView table = cardText("회사별 요약\nMY COMPANY: " + myCompanyCount() + "척 · 기준회사\nOTHER COMPANY: 비교대상\n\n분석 예정\n- 월별 위판고\n- 연도별 위판고\n- 선박별 순위\n- 회사별 순위\n- 전날 조업 추정", 17, navy, Color.WHITE);
        addView(table, 0, 8, 0, 8);
    }

    private void renderSettings() {
        addNotice("설정 화면입니다. 나중에 기준회사 변경, 선박 등록, AIS API, 위판 데이터 연결을 여기서 관리합니다.");
        TextView settings = cardText("기준회사: MY COMPANY\n기본화면: 관제\n위치 갱신: 수동/자동 예정\n외부 AIS API: 미연결\n위판 데이터: 미연결\n\n다음 단계\n1. 실제 선박 등록 화면\n2. 좌표 저장\n3. 지도 라이브러리 연결\n4. API 키 입력\n5. CSV/KML/GeoJSON 내보내기", 17, navy, Color.WHITE);
        addView(settings, 0, 8, 0, 8);
    }

    private void addVesselCard(int index, Vessel vessel, boolean full) {
        String own = vessel.isMyCompany ? " · 기준회사" : "";
        String text = index + ". " + vessel.name + own + "\n"
                + vessel.company + " / " + vessel.fleet + "\n"
                + vessel.fishingType.label + " · MMSI " + vessel.mmsi + "\n"
                + "구역: " + zoneFor(index) + " · 속력: " + speedFor(index) + "kn · " + stateFor(index) + "\n"
                + "월 위판 추정: " + formatMoney((long) boxesFor(index) * 12L * 38000L) + "원";
        addView(cardText(text, 17, Color.rgb(25, 35, 45), Color.WHITE), 0, 7, 0, 7);
    }

    private void addNotice(String text) {
        TextView notice = cardText(text, 15, muted, sky);
        addView(notice, 0, 8, 0, 12);
    }

    private void addKpiRow(String a, String av, String b, String bv) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.addView(kpiCard(a, av));
        row.addView(kpiCard(b, bv));
        addView(row, 0, 5, 0, 5);
    }

    private TextView kpiCard(String label, String value) {
        TextView view = cardText(label + "\n" + value, 16, navy, Color.WHITE);
        view.setGravity(Gravity.CENTER);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -2, 1);
        params.setMargins(5, 5, 5, 5);
        view.setLayoutParams(params);
        return view;
    }

    private TextView cardText(String text, int size, int color, int fill) {
        TextView view = new TextView(this);
        view.setText(text);
        view.setTextSize(size);
        view.setTextColor(color);
        view.setLineSpacing(4, 1.05f);
        view.setPadding(18, 18, 18, 18);
        view.setBackground(roundRect(fill, 24, line));
        return view;
    }

    private void addView(TextView view, int l, int t, int r, int b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(l, t, r, b);
        bodyBox.addView(view, params);
    }

    private void addView(LinearLayout view, int l, int t, int r, int b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(l, t, r, b);
        bodyBox.addView(view, params);
    }

    private int boxesFor(int index) { return 40 + index * 18; }
    private String speedFor(int index) { return String.valueOf(4 + index); }
    private String zoneFor(int index) { return "SEA-" + (char)('A' + index) + "0" + index; }
    private String stateFor(int index) { return index % 2 == 0 ? "항해중" : "조업추정"; }

    private int totalTodayBoxes() {
        int sum = 0;
        int i = 0;
        for (Vessel vessel : FleetSeed.all()) {
            if (!matches(vessel)) continue;
            i++;
            sum += boxesFor(i);
        }
        return sum;
    }

    private long totalMonthAmount() { return (long) totalTodayBoxes() * 12L * 38000L; }
    private long totalYearAmount() { return totalMonthAmount() * 12L; }

    private String formatMoney(long v) {
        if (v >= 100000000L) return (v / 100000000L) + "억 " + ((v % 100000000L) / 10000L) + "만";
        if (v >= 10000L) return (v / 10000L) + "만";
        return String.valueOf(v);
    }

    private int countMode() {
        int count = 0;
        for (Vessel vessel : FleetSeed.all()) if (matches(vessel)) count++;
        return count;
    }

    private int countType(FishingType type) {
        int count = 0;
        for (Vessel vessel : FleetSeed.all()) if (vessel.fishingType == type) count++;
        return count;
    }

    private int myCompanyCount() {
        int count = 0;
        for (Vessel vessel : FleetSeed.all()) if (vessel.isMyCompany) count++;
        return count;
    }

    private String pageLabel() {
        if ("MAP".equals(page)) return "지도";
        if ("AUCTION".equals(page)) return "위판";
        if ("STATS".equals(page)) return "통계";
        if ("SETTINGS".equals(page)) return "설정";
        return "관제";
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
