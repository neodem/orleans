package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/30/19
 */
public class Market {

    public boolean isSlotFilled(int slot) {
        return market[slot] != EMPTY;
    }

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
