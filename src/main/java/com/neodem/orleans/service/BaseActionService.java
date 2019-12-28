package com.neodem.orleans.service;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;
import org.springframework.util.Assert;

import java.util.ArrayList;
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

        List<Follower> sanitizedFollowers = sanitizeFollowers(followers);

        Grouping<Follower> neededFollowers = actionMappings.get(actionType);
        Grouping<Follower> testFollowers = new Grouping<>(sanitizedFollowers);
        return testFollowers.canFitInto(neededFollowers);
    }

    @Override
    public boolean canPlace(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready) {
        if(placedInActionAlready == null || placedInActionAlready.isEmpty() && validAction(actionType, followersToPlace)) return true;

        // returns a copy
        List<Follower> template = actionMappings.get(actionType).getTemplate();
        for(Follower placed : placedInActionAlready) {
            template.remove(placed);
        }

        List<Follower> sanitizedFollowers = sanitizeFollowers(followersToPlace);
        for(Follower toPlace : sanitizedFollowers) {
            if(template.contains(toPlace)) template.remove(toPlace);
            else return false;
        }

        return true;
    }

    @Override
    public boolean fullAction(ActionType actionType, List<Follower> followers, Follower techToken) {
//TODO
        return false;
    }

    private List<Follower> sanitizeFollowers(List<Follower> followers) {
        List<Follower> sanitizedFollowers = new ArrayList<>();
        for(Follower follower : followers) {
            if(follower == Follower.StarterBoatman) sanitizedFollowers.add(Follower.Boatman);
            else if(follower == Follower.StarterCraftsman) sanitizedFollowers.add(Follower.Craftsman);
            else if(follower == Follower.StarterFarmer) sanitizedFollowers.add(Follower.Farmer);
            else if(follower == Follower.StarterTrader) sanitizedFollowers.add(Follower.Trader);
            else sanitizedFollowers.add(follower);
        }
        return sanitizedFollowers;
    }
}
