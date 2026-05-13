package com.bipiroo.marinefleettracker.data;

import com.bipiroo.marinefleettracker.model.FishingType;
import com.bipiroo.marinefleettracker.model.Vessel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FleetSeed {
    private static final List<Vessel> VESSELS;

    static {
        List<Vessel> list = new ArrayList<>();
        list.add(new Vessel("v001", "VESSEL 1", "100000001", "MY COMPANY", "FLEET 1", FishingType.SMALL_PURSE_SEINE, true));
        list.add(new Vessel("v002", "VESSEL 2", "100000002", "MY COMPANY", "FLEET 1", FishingType.SMALL_PURSE_SEINE, true));
        list.add(new Vessel("v003", "VESSEL 3", "100000003", "OTHER COMPANY", "FLEET 2", FishingType.LARGE_PURSE_SEINE, false));
        VESSELS = Collections.unmodifiableList(list);
    }

    public static List<Vessel> all() {
        return VESSELS;
    }

    public static Vessel findById(String id) {
        for (Vessel vessel : VESSELS) {
            if (vessel.id.equals(id)) return vessel;
        }
        return null;
    }
}
