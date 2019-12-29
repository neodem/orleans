package com.neodem.orleans.engine.original;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.BenefitTrackerBase;
import com.neodem.orleans.engine.core.model.BenefitTrack;
import com.neodem.orleans.engine.original.model.BenefitName;

import java.util.HashMap;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.Follower.*;
import static com.neodem.orleans.engine.original.model.BenefitName.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class OriginalBenefitTracker extends BenefitTrackerBase {

    private final Map<BenefitName, BenefitTrack> benefitTracks;

    public OriginalBenefitTracker() {
        benefitTracks = new HashMap<>();
        benefitTracks.put(BuildingCityWall, new BenefitTrack(BuildingCityWall, new Grouping<>(Knight, Knight, Knight, Trader, Farmer, Farmer, Farmer, Craftsman, Craftsman, Craftsman), 1));
        benefitTracks.put(PapalConclave, new BenefitTrack(PapalConclave, new Grouping<>(Knight, Monk, Monk), 3));
        benefitTracks.put(DefeatingPlague, new BenefitTrack(DefeatingPlague, new Grouping<>(Scholar, Scholar, Boatman, Farmer, Trader), 2));
        benefitTracks.put(Astronomy, new BenefitTrack(Astronomy, new Grouping<>(Scholar, Scholar, Trader), 1));
        benefitTracks.put(FoundingBoatmenGuild, new BenefitTrack(FoundingBoatmenGuild, new Grouping<>(Boatman, Boatman, Boatman, Scholar), 1));
        benefitTracks.put(BuildingCathedral, new BenefitTrack(BuildingCathedral, new Grouping<>(Craftsman, Craftsman, Monk, Monk, Trader, Trader), 2));
        benefitTracks.put(PeaceTreaty, new BenefitTrack(PeaceTreaty, new Grouping<>(Monk, Scholar, Knight, Knight), 2));
        benefitTracks.put(Canalisation, new BenefitTrack(Canalisation, new Grouping<>(Boatman, Boatman, Boatman, Trader, Trader, Farmer, Farmer, Farmer, Craftsman, Craftsman), 1));
    }

    @Override
    protected Map<BenefitName, BenefitTrack> benefitTracks() {
        return benefitTracks;
    }
}
