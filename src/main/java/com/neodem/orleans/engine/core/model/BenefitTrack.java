package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class BenefitTrack extends FollowerTrack {

    private final int coinReward;

    public BenefitTrack(int coinReward, FollowerType... followerTypes) {
        super(followerTypes);
        this.coinReward = coinReward;
    }

    public int getCoinReward() {
        return coinReward;
    }
}
