package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.original.model.BenefitName;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class BenefitTrack {

    private final Grouping<Follower> blueprint;
    private final BenefitName benefitName;
    private final int coinReward;

    public BenefitTrack(BenefitName benefitName, Grouping<Follower> blueprint, int coinReward) {
        this.blueprint = blueprint;
        this.benefitName = benefitName;
        this.coinReward = coinReward;
    }

    public Grouping<Follower> getBlueprint() {
        return blueprint;
    }

    public BenefitName getBenefitName() {
        return benefitName;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public boolean canAdd(Follower follower) {
        return false;
    }

    public boolean add(Follower follower) {
        return false;
    }
}
