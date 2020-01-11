package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.Arrays;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/30/19
 */
public class Market {

    private static class EmptyNode extends Follower {
        public EmptyNode() {
            super(FollowerType.None);
        }
    }

    private static EmptyNode EMPTY = new EmptyNode();

    private int marketSize;
    private Follower[] market;
    private int availableSlots;

    public Market() {
        init(8);
    }

    public Market(JsonNode json) {
        this.marketSize = json.get("marketSize").intValue();
        this.availableSlots = json.get("availableSlots").intValue();
        this.market = new Follower[marketSize];
        JsonNode market = json.get("market");
        int i = 0;
        for (JsonNode follower : market) {
            this.market[i] = new Follower(follower);
            i++;
        }
    }

    @Override
    public String toString() {
        return "Market{" +
                "market=" + Arrays.toString(market) +
                '}';
    }

    public void init(int marketSize) {
        this.marketSize = marketSize;
        this.availableSlots = marketSize;
        market = new Follower[marketSize];
        for (int i = 0; i < marketSize; i++) market[i] = EMPTY;
    }

    public Follower remove(int slot) {

        Follower inSlot = market[slot];
        if (inSlot instanceof EmptyNode) {
            inSlot = null;
        } else {
            market[slot] = EMPTY;
            availableSlots++;
        }

        return inSlot;
    }

    public boolean hasSpace() {
        return availableSlots > 0;
    }

    public int getFilledSlotCount() {
        return marketSize - availableSlots;
    }

    /**
     * add follower to the left most slot
     *
     * @param follower
     * @return index of where follower was placed, else -1 if no room
     */
    public int addToMarket(Follower follower) {
        if (hasSpace()) {
            int i = 0;
            for (; i < marketSize; i++) {
                if (market[i] == EMPTY) {
                    market[i] = follower;
                    availableSlots--;
                    break;
                }
            }
            return i;
        } else {
            return -1;
        }
    }

    /**
     * return the index of the first follower of the given type or null if none found
     *
     * @param followerType
     * @return
     */
    public Integer getSlotIndexForFirst(FollowerType followerType) {
        int i = 0;
        for (; i < marketSize; i++) {
            if (market[i] != EMPTY) {
                if (market[i].getFollowerType() == followerType) return i;
            }
        }
        return null;
    }

    public boolean isSlotFilled(int slot) {
        return market[slot] != EMPTY;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public int getMarketSize() {
        return marketSize;
    }

    public Follower[] getMarket() {
        return market;
    }
}
