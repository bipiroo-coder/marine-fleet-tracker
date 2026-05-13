package com.bipiroo.marinefleettracker.model;

public class AisPosition {
    public final String vesselId;
    public final String mmsi;
    public final double latitude;
    public final double longitude;
    public final double speedKn;
    public final double courseDeg;
    public final long receivedAtMillis;
    public final String source;

    public AisPosition(String vesselId, String mmsi, double latitude, double longitude, double speedKn, double courseDeg, long receivedAtMillis, String source) {
        this.vesselId = vesselId;
        this.mmsi = mmsi;
        this.latitude = latitude;
        this.longitude = longitude;
        this.speedKn = speedKn;
        this.courseDeg = courseDeg;
        this.receivedAtMillis = receivedAtMillis;
        this.source = source;
    }

    public boolean isOld(long nowMillis) {
        return nowMillis - receivedAtMillis > 2L * 60L * 60L * 1000L;
    }
}
