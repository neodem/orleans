package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.BenefitTrack;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.original.model.BenefitName;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public abstract class BenefitTrackerBase implements BenefitTracker {

    protected abstract Map<BenefitName, BenefitTrack> benefitTracks();

    @Override
    public boolean canAddToBenefit(BenefitName benefitName, Follower follower) {
        return benefitTracks().get(benefitName).canAdd(follower);
    }

    @Override
    public boolean addToBenefit(BenefitName benefitName, Follower follower) {
        return benefitTracks().get(benefitName).add(follower);
    }

    @Override
    public int getBenefitCoinReward(BenefitName benefitName) {
        return benefitTracks().get(benefitName).getCoinReward();
    }
}