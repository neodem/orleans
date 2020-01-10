package com.neodem.orleans.engine.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.neodem.orleans.engine.core.model.BenefitTrack;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.original.model.BenefitName;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public abstract class BenefitTrackerBase implements BenefitTracker {

    public BenefitTrackerBase() {
    }

    public BenefitTrackerBase(JsonNode json) {
    }

    protected abstract Map<BenefitName, BenefitTrack> benefitTracks();

    @Override
    public boolean canAddToBenefit(BenefitName benefitName, Follower follower) {
        BenefitTrack benefitTrack = benefitTracks().get(benefitName);
        int index = benefitTrack.getNextIndexForFollowerType(follower.getType());
        return index != -1;
    }

    @Override
    public boolean addToBenefit(BenefitName benefitName, Follower follower) {
        BenefitTrack benefitTrack = benefitTracks().get(benefitName);
        int index = benefitTrack.getNextIndexForFollowerType(follower.getType());

        return benefitTracks().get(benefitName).add(follower, index);
    }

    @Override
    public int getBenefitCoinReward(BenefitName benefitName) {
        return benefitTracks().get(benefitName).getCoinReward();
    }
}
