package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class BenefitTrack extends FollowerTrack {

    private int coinReward;

    protected BenefitTrack() {
    }

    public BenefitTrack(int coinReward, FollowerType... followerTypes) {
        super(followerTypes);
        this.coinReward = coinReward;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public int getNextIndexForFollowerType(FollowerType type) {
        Slot[] track = getTrack();
        for (int i = 0; i < track.length; i++) {
            Slot s = track[i];
            if (s.expectedType == type) return i;
        }
        return -1;
    }

    protected void setCoinReward(int coinReward) {
        this.coinReward = coinReward;
    }
}
