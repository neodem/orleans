package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class BenefitTrack {

    private final Map<Follower, Integer> blueprint = new HashMap<>();
    private final Map<Follower, Integer> filledSpots = new HashMap<>();
    private final int coinReward;
    private final int maxSize;

    private int filledSpotsCount;
    private boolean full;

    public BenefitTrack(int coinReward, Follower... followers) {
        this.coinReward = coinReward;

        for (Follower f : followers) {
            Util.mapInc(blueprint, f);
        }

        int max = 0;
        for (Follower f : blueprint.keySet()) {
            max += blueprint.get(f);
        }
        maxSize = max;

        full = false;
        filledSpotsCount = 0;
    }

    public int getCoinReward() {
        return coinReward;
    }

    public boolean canAdd(Follower follower) {
        if (full) return false;

        int needed = blueprint.get(follower);
        int actual = filledSpots.get(follower);

        return needed > actual;
    }

    public boolean add(Follower follower) {
        if (canAdd(follower)) {
            int current = filledSpots.get(follower);
            filledSpots.put(follower, ++current);
            filledSpotsCount++;
        }

        if (filledSpotsCount == maxSize) {
            full = true;
        }

        return full;
    }
}
