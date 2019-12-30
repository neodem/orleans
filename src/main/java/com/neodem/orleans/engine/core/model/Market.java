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
            super(FollowerType.Monk);
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
        return availableSlots < marketSize;
    }

    public void addToMarket(Follower follower) {
        if (hasSpace())
            market[--availableSlots] = follower;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }
}
