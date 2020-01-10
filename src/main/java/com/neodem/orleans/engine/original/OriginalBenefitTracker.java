package com.neodem.orleans.engine.original;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.engine.core.BenefitTrackerBase;
import com.neodem.orleans.engine.core.model.BenefitTrack;
import com.neodem.orleans.engine.original.model.BenefitName;

import java.util.HashMap;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.FollowerType.*;
import static com.neodem.orleans.engine.original.model.BenefitName.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class OriginalBenefitTracker extends BenefitTrackerBase {

    private Map<BenefitName, BenefitTrack> benefitTracks;

    public OriginalBenefitTracker() {
        initTracks();
    }

    private void initTracks() {
        benefitTracks = new HashMap<>();
        benefitTracks.put(BuildingCityWall, new BenefitTrack(1, Knight, Knight, Knight, Trader, Farmer, Farmer, Farmer, Craftsman, Craftsman, Craftsman));
        benefitTracks.put(PapalConclave, new BenefitTrack(3, Knight, Monk, Monk));
        benefitTracks.put(DefeatingPlague, new BenefitTrack(2, Scholar, Scholar, Boatman, Farmer, Trader));
        benefitTracks.put(Astronomy, new BenefitTrack(1, Scholar, Scholar, Trader));
        benefitTracks.put(FoundingBoatmenGuild, new BenefitTrack(1, Boatman, Boatman, Boatman, Scholar));
        benefitTracks.put(BuildingCathedral, new BenefitTrack(2, Craftsman, Craftsman, Monk, Monk, Trader, Trader));
        benefitTracks.put(PeaceTreaty, new BenefitTrack(2, Monk, Scholar, Knight, Knight));
        benefitTracks.put(Canalisation, new BenefitTrack(1, Boatman, Boatman, Boatman, Trader, Trader, Farmer, Farmer, Farmer, Craftsman, Craftsman));
    }

    public OriginalBenefitTracker(JsonNode json) {
        ObjectMapper mapper = new ObjectMapper();

        try {

            TypeReference<HashMap<BenefitName, BenefitTrack>> btRef = new TypeReference<>() {
            };
            this.benefitTracks = mapper.readValue(json.get("benefitTracks").toString(), btRef);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    @Override
    protected Map<BenefitName, BenefitTrack> benefitTracks() {
        return benefitTracks;
    }

    public Map<BenefitName, BenefitTrack> getBenefitTracks() {
        return benefitTracks;
    }
}
