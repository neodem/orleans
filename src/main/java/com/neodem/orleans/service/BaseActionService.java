package com.neodem.orleans.service;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public abstract class BaseActionService implements ActionService {

    protected final Map<ActionType, Grouping<Follower>> actionMappings;

    public BaseActionService(Map<ActionType, Grouping<Follower>> actionMappings) {
        this.actionMappings = actionMappings;
    }

    @Override
    public boolean validAction(ActionType actionType, List<Follower> followers) {
        Assert.notNull(actionType, "actionType may not be null");
        Assert.notNull(followers, "followers may not be null");
        Grouping<Follower> neededFollowers = actionMappings.get(actionType);
        Grouping<Follower> actualFollowers = new Grouping<>(followers);
        return actualFollowers.canFitInto(neededFollowers);
    }

    @Override
    public boolean fullAction(ActionType actionType, List<Follower> followers, Follower techToken) {
//TODO
        return false;
    }
}
