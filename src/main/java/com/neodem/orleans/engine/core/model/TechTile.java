package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class TechTile {
    private final ActionType actionType;
    private final Follower follower;

    public TechTile(ActionType actionType, Follower follower) {
        this.actionType = actionType;
        this.follower = follower;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public Follower getFollower() {
        return follower;
    }
}
