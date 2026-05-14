package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bipiroo.marinefleettracker.data.FleetSeed;
import com.bipiroo.marinefleettracker.model.FishingType;
import com.bipiroo.marinefleettracker.model.Vessel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    LinearLayout box;
    TextView title2, state;
    String page = "DASH";
    String mode = "ALL";
    int tap = 0;
    int navy = Color.rgb(10,28,48), blue = Color.rgb(22,97,170), green = Color.rgb(29,145,94);
    int bg = Color.rgb(241,246,252), sky = Color.rgb(232,243,255), line = Color.rgb(218,228,238);
    EditText nameIn, mmsiIn, companyIn, fleetIn;
    CheckBox mineIn, largeIn;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        try { make(); render("start"); }
        catch(Exception e) { TextView v=new TextView(this); v.setText("SAFE MODE\n"+e.getClass().getSimpleName()); v.setTextSize(24); v.setPadding(30,30,30,30); setContentView(v); }
    }

    void make() {
        LinearLayout root = new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(16,16,16,16); root.setBackgroundColor(bg);
        LinearLayout head = new LinearLayout(this); head.setOrientation(LinearLayout.VERTICAL); head.setPadding(22,20,22,20); head.setBackground(round(navy,30,0)); root.addView(head,new LinearLayout.LayoutParams(-1,-2));
        TextView t = txt("Marine Fleet Tracker",27,Color.WHITE,true); head.addView(t);
        TextView sub = txt("Full MVP v0.7 · real map screen added",14,Color.rgb(190,215,240),false); head.addView(sub);
        title2 = txt("",18,Color.WHITE,true); title2.setPadding(0,14,0,0); head.addView(title2);
        state = txt("",14,Color.rgb(200,235,212),false); state.setPadding(0,6,0,0); head.addView(state);
        root.addView(row(new String[][]{{"관제","DASH"},{"위치","MAP"},{"위판","SALE"}}));
        root.addView(row(new String[][]{{"통계","STAT"},{"선박","VESSEL"},{"설정","SET"}}));
        root.addView(row(new String[][]{{"우리","MY"},{"소형","SMALL"},{"대형","LARGE"},{"전체","ALL"}}));
        ScrollView sv = new ScrollView(this); box = new LinearLayout(this); box.setOrientation(LinearLayout.VERTICAL); box.setPadding(0,10,0,20); sv.addView(box); root.addView(sv,new LinearLayout.LayoutParams(-1,0,1));
        setContentView(root);
    }

    LinearLayout row(String[][] a) { LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.setGravity(Gravity.CENTER); r.setPadding(0,7,0,0); for(String[] x:a) r.addView(btn(x[0],x[1])); return r; }
    Button btn(String s,String v) { Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextSize(15); b.setTextColor(Color.WHITE); b.setTypeface(Typeface.DEFAULT_BOLD); b.setBackground(round(isMode(v)?blue:green,22,0)); b.setOnClickListener(x->{tap++; if(isMode(v)) mode=v; else page=v; render(s);}); LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-2,1); p.setMargins(4,3,4,3); b.setLayoutParams(p); return b; }
    boolean isMode(String v) { return v.equals("MY")||v.equals("SMALL")||v.equals("LARGE")||v.equals("ALL"); }

    void render(String msg) {
        box.removeAllViews();
        title2.setText(label(page)+" · "+label(mode)+" · "+filtered().size()+"척");
        state.setText(msg+" · tap "+tap+" · 저장 "+saved().size()+"척");
        if(page.equals("MAP")) map(); else if(page.equals("SALE")) sale(); else if(page.equals("STAT")) stat(); else if(page.equals("VESSEL")) vesselPage(); else if(page.equals("SET")) setPage(); else dash();
    }

    void dash() { notice("전체 앱 화면입니다. 선박등록, 위치, 위판, 통계를 한 APK 안에 묶었습니다."); kpi("표시",filtered().size()+"척","저장",saved().size()+"척"); kpi("오늘",boxes()+"상자","월위판",money(month())); int i=0; for(Vessel v:filtered()) card(++i,v); }

    void map() {
        notice("위치 화면입니다. 아래 버튼을 누르면 실제 OpenStreetMap 지도 화면이 열립니다. 현재 마커는 샘플 위치입니다.");
        Button open = act("실제 지도 열기", green, v -> startActivity(new Intent(this, MapActivity.class)));
        box.addView(open);
        add(cardText("간이 위치판\n북\n┌──────────────┐\n│ S1      L1   │\n│   MY1   S2   │\n│      MY2     │\n└──────────────┘\n남",16,navy,sky));
        int i=0; for(Vessel v:filtered()) add(cardText((++i)+". "+v.name+"\n구역 "+zone(i)+" · "+speed(i)+"kn · "+stateFor(i),16,navy,Color.WHITE));
    }

    void sale() { notice("위판 화면입니다. 현재는 샘플 계산이며 실제 위판자료 연결 자리입니다."); int i=0; for(Vessel v:filtered()){i++; int b=40+i*18; add(cardText(v.name+"\n오늘 "+b+"상자 · 월 "+(b*12)+"상자\n월 위판 "+money((long)b*12*38000)+"\n전날 조업추정 "+zone(i),17,navy,Color.WHITE));} }
    void stat() { notice("통계 화면입니다. 월별·연도별·선박별·회사별 집계 구조입니다."); kpi("월",money(month()),"연",money(month()*12)); kpi("소형",countType(FishingType.SMALL_PURSE_SEINE)+"척","대형",countType(FishingType.LARGE_PURSE_SEINE)+"척"); add(cardText("회사별\n우리회사 "+mineCount()+"척\n전체 "+all().size()+"척\n\n예정: 선박별 순위, 회사별 순위, 전날 조업추정",17,navy,Color.WHITE)); }
    void vesselPage() { notice("선박을 여러 척 저장할 수 있습니다. 저장 후 관제/위치/위판/통계에 반영됩니다."); nameIn=input("선명"); mmsiIn=input("MMSI"); companyIn=input("회사"); fleetIn=input("선단"); mineIn=new CheckBox(this); mineIn.setText("우리회사"); mineIn.setChecked(true); largeIn=new CheckBox(this); largeIn.setText("대형선망, 해제하면 소형선망"); add(nameIn); add(mmsiIn); add(companyIn); add(fleetIn); box.addView(mineIn); box.addView(largeIn); LinearLayout r=new LinearLayout(this); r.addView(act("추가",Color.rgb(210,118,38),v->addVessel())); r.addView(act("전체삭제",Color.rgb(170,58,58),v->clearVessels())); box.addView(r); int i=0; for(Vessel v:saved()) card(++i,v); }
    void setPage() { notice("설정 화면입니다. 실제 위치 API, 지도, 위판자료 연결은 다음 단계에서 이 화면에 붙입니다."); add(cardText("현재상태\n오프라인 MVP + 실제 지도 화면\n저장선박 "+saved().size()+"척\n기준회사 필터 지원\n\n포함기능\n관제 / 위치 / 실제지도 / 위판 / 통계 / 선박등록",17,navy,Color.WHITE)); }

    void addVessel(){ String n=clean(val(nameIn,"VESSEL")), m=clean(val(mmsiIn,"000000000")), c=clean(val(companyIn,"MY COMPANY")), f=clean(val(fleetIn,"FLEET")); String rec=n+"|"+m+"|"+c+"|"+f+"|"+(largeIn.isChecked()?"L":"S")+"|"+(mineIn.isChecked()?"1":"0"); String old=prefs().getString("vessels",""); prefs().edit().putString("vessels", old.length()==0?rec:old+";;"+rec).apply(); render("saved"); }
    void clearVessels(){ prefs().edit().remove("vessels").apply(); render("cleared"); }
    List<Vessel> all(){ ArrayList<Vessel> a=new ArrayList<>(); a.addAll(FleetSeed.all()); a.addAll(saved()); return a; }
    List<Vessel> saved(){ ArrayList<Vessel> a=new ArrayList<>(); String raw=prefs().getString("vessels",""); if(raw.length()==0)return a; int id=1; for(String row:raw.split(";;")){String[] p=row.split("\\|",-1); if(p.length<6)continue; a.add(new Vessel("u"+id++,p[0],p[1],p[2],p[3],p[4].equals("L")?FishingType.LARGE_PURSE_SEINE:FishingType.SMALL_PURSE_SEINE,p[5].equals("1")));} return a; }
    List<Vessel> filtered(){ ArrayList<Vessel> a=new ArrayList<>(); for(Vessel v:all()) if(match(v)) a.add(v); return a; }
    boolean match(Vessel v){ if(mode.equals("MY"))return v.isMyCompany; if(mode.equals("SMALL"))return v.fishingType==FishingType.SMALL_PURSE_SEINE; if(mode.equals("LARGE"))return v.fishingType==FishingType.LARGE_PURSE_SEINE; return true; }

    void card(int i,Vessel v){ add(cardText(i+". "+v.name+(v.isMyCompany?" · 우리회사":"")+"\n"+v.company+" / "+v.fleet+"\n"+v.fishingType.label+" · MMSI "+v.mmsi+"\n"+zone(i)+" · "+speed(i)+"kn · "+stateFor(i),17,Color.rgb(25,35,45),Color.WHITE)); }
    void notice(String s){ add(cardText(s,15,Color.rgb(88,101,118),sky)); }
    void kpi(String a,String av,String b,String bv){ LinearLayout r=new LinearLayout(this); r.setOrientation(LinearLayout.HORIZONTAL); r.addView(kpiCard(a+"\n"+av)); r.addView(kpiCard(b+"\n"+bv)); box.addView(r); }
    TextView kpiCard(String s){ TextView v=cardText(s,16,navy,Color.WHITE); v.setGravity(Gravity.CENTER); v.setTypeface(Typeface.DEFAULT_BOLD); v.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); return v; }
    TextView cardText(String s,int size,int color,int fill){ TextView v=txt(s,size,color,false); v.setLineSpacing(4,1.05f); v.setPadding(18,18,18,18); v.setBackground(round(fill,24,line)); return v; }
    TextView txt(String s,int size,int color,boolean bold){ TextView v=new TextView(this); v.setText(s); v.setTextSize(size); v.setTextColor(color); if(bold)v.setTypeface(Typeface.DEFAULT_BOLD); return v; }
    EditText input(String h){ EditText e=new EditText(this); e.setHint(h); e.setSingleLine(true); e.setTextSize(17); e.setPadding(18,12,18,12); e.setBackground(round(Color.WHITE,18,line)); return e; }
    Button act(String s,int c,android.view.View.OnClickListener l){ Button b=new Button(this); b.setText(s); b.setAllCaps(false); b.setTextColor(Color.WHITE); b.setTextSize(17); b.setTypeface(Typeface.DEFAULT_BOLD); b.setBackground(round(c,22,0)); b.setOnClickListener(l); b.setLayoutParams(new LinearLayout.LayoutParams(0,-2,1)); return b; }
    void add(TextView v){ LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2); p.setMargins(0,7,0,7); box.addView(v,p); }
    int boxes(){ int s=0,i=0; for(Vessel v:filtered())s+=40+(++i)*18; return s; }
    long month(){ return (long)boxes()*12*38000; }
    int countType(FishingType t){ int c=0; for(Vessel v:all())if(v.fishingType==t)c++; return c; }
    int mineCount(){ int c=0; for(Vessel v:all())if(v.isMyCompany)c++; return c; }
    String label(String x){ if(x.equals("DASH"))return"관제"; if(x.equals("MAP"))return"위치"; if(x.equals("SALE"))return"위판"; if(x.equals("STAT"))return"통계"; if(x.equals("VESSEL"))return"선박"; if(x.equals("SET"))return"설정"; if(x.equals("MY"))return"우리회사"; if(x.equals("SMALL"))return"소형"; if(x.equals("LARGE"))return"대형"; return"전체"; }
    String money(long v){ if(v>=100000000)return(v/100000000)+"억 "+((v%100000000)/10000)+"만"; if(v>=10000)return(v/10000)+"만"; return""+v; }
    String zone(int i){ return "SEA-"+(char)('A'+i)+"0"+i; }
    String speed(int i){ return ""+(4+i); }
    String stateFor(int i){ return i%2==0?"항해중":"조업추정"; }
    String val(EditText e,String f){ String s=e.getText().toString().trim(); return s.length()==0?f:s; }
    String clean(String s){ return s.replace("|"," ").replace(";;"," "); }
    SharedPreferences prefs(){ return getSharedPreferences("fleet_store_v5",MODE_PRIVATE); }
    GradientDrawable round(int fill,int radius,int stroke){ GradientDrawable d=new GradientDrawable(); d.setColor(fill); d.setCornerRadius(radius); if(stroke!=0)d.setStroke(2,stroke); return d; }
}
