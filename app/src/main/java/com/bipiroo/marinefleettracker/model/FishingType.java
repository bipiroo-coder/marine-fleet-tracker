package com.bipiroo.marinefleettracker.model;

public enum FishingType {
    SMALL_PURSE_SEINE("소형선망"),
    LARGE_PURSE_SEINE("대형선망");

    public final String label;

    FishingType(String label) {
        this.label = label;
    }
}
