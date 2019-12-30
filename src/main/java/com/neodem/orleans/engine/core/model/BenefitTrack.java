package com.neodem.orleans.engine.core.model;

import java.util.List;

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

    public int getNextIndexForFollowerType(FollowerType type) {
        List<Slot> track = getTrack();
        for (int i = 0; i < track.size(); i++) {
            Slot s = track.get(i);
            if (s.ft == type) return i;
        }
        return -1;
    }
}
