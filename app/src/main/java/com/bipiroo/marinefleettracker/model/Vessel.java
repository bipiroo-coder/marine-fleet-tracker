package com.bipiroo.marinefleettracker.model;

public class Vessel {
    public final String id;
    public final String name;
    public final String mmsi;
    public final String company;
    public final String fleet;
    public final FishingType fishingType;
    public final boolean isMyCompany;

    public Vessel(String id, String name, String mmsi, String company, String fleet, FishingType fishingType, boolean isMyCompany) {
        this.id = id;
        this.name = name;
        this.mmsi = mmsi;
        this.company = company;
        this.fleet = fleet;
        this.fishingType = fishingType;
        this.isMyCompany = isMyCompany;
    }
}
