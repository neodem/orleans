package com.neodem.orleans.engine.core.model;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class TechTile {
    private final ActionType actionType;
    private final FollowerType followerType;

    public TechTile(ActionType actionType, FollowerType followerType) {
        this.actionType = actionType;
        this.followerType = followerType;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public FollowerType getFollowerType() {
        return followerType;
    }
}
