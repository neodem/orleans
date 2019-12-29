package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class EmptyFollowerSlot extends Follower {
    public EmptyFollowerSlot() {
        super(null);
    }

    @Override
    public String toString() {
        return "<EmtyFollowerSlot>";
    }
}
