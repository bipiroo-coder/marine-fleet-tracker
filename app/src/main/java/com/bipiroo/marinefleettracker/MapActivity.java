package com.bipiroo.marinefleettracker;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapActivity extends Activity {
    MapView map;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        Configuration.getInstance().setUserAgentValue(getPackageName());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.WHITE);

        TextView top = new TextView(this);
        top.setText("Real Map View · sample fleet markers");
        top.setTextSize(18);
        top.setTextColor(Color.rgb(10,28,48));
        top.setPadding(18,18,18,18);
        root.addView(top, new LinearLayout.LayoutParams(-1, -2));

        map = new MapView(this);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);
        map.getController().setZoom(8.0);
        map.getController().setCenter(new GeoPoint(35.10, 129.10));
        root.addView(map, new LinearLayout.LayoutParams(-1, 0, 1));

        addMarker("MY1", 35.10, 129.10);
        addMarker("S1", 35.25, 129.35);
        addMarker("L1", 34.90, 128.95);
        addMarker("S2", 35.35, 128.85);
        setContentView(root);
    }

    void addMarker(String title, double lat, double lon) {
        Marker m = new Marker(map);
        m.setPosition(new GeoPoint(lat, lon));
        m.setTitle(title);
        m.setSnippet("sample position");
        m.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        map.getOverlays().add(m);
    }

    @Override protected void onResume() { super.onResume(); if(map != null) map.onResume(); }
    @Override protected void onPause() { if(map != null) map.onPause(); super.onPause(); }
}
