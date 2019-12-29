package com.neodem.orleans.engine.core;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.original.model.PlaceTile;
import com.neodem.orleans.engine.core.model.PlayerState;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public abstract class ActionHelperBase implements ActionHelper {

    protected  abstract Map<ActionType, Grouping<Follower>> actionMappings();
    protected  abstract Map<ActionType, ActionProcessor> actionProcessors();
    protected  abstract Map<ActionType, PlaceTile> placeTileMap();

    public Grouping getGrouping(ActionType actionType) {
        return actionMappings().get(actionType);
    }

    @Override
    public boolean isCommonAction(ActionType actionType) {
        return !placeTileMap().containsKey(actionType);
    }

    @Override
    public boolean isPlaceTileAction(ActionType actionType) {
        return placeTileMap().containsKey(actionType);
    }

    @Override
    public PlaceTile getPlaceTile(ActionType actionType) {
        return placeTileMap().get(actionType);
    }

    @Override
    public boolean isActionAllowed(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        ActionProcessor actionProcessor = actionProcessors().get(actionType);
        if (actionProcessor != null) {
            return actionProcessor.isAllowed(gameState, player, additionalDataMap);
        }
        return false;
    }

    @Override
    public void processAction(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {

        gameState.writeLine("" + player.getPlayerId() + " doing action: " + actionType);

        ActionProcessor actionProcessor = actionProcessors().get(actionType);
        if (actionProcessor != null) {
            actionProcessor.process(gameState, player, additionalDataMap);
        }
    }

    @Override
    public boolean actionCanAccept(ActionType actionType, List<Follower> followers) {
        Assert.notNull(actionType, "actionType may not be null");
        Assert.notNull(followers, "followers may not be null");

        List<Follower> sanitizedFollowers = sanitizeFollowers(followers);

        Grouping<Follower> neededFollowers = actionMappings().get(actionType);
        Grouping<Follower> testFollowers = new Grouping<>(sanitizedFollowers);
        return testFollowers.canFitInto(neededFollowers);
    }

    @Override
    public boolean canPlaceIntoAction(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready) {
        if (placedInActionAlready == null || placedInActionAlready.isEmpty() && actionCanAccept(actionType, followersToPlace))
            return true;

        // returns a copy
        List<Follower> template = actionMappings().get(actionType).getTemplate();
        for (Follower placed : placedInActionAlready) {
            template.remove(placed);
        }

        List<Follower> sanitizedFollowers = sanitizeFollowers(followersToPlace);
        for (Follower toPlace : sanitizedFollowers) {
            if (template.contains(toPlace)) template.remove(toPlace);
            else return false;
        }

        return true;
    }

    @Override
    public boolean actionIsFull(ActionType actionType, List<Follower> followers, Follower techToken) {
        //TODO techToken

        List<Follower> sanitizedFollowers = sanitizeFollowers(followers);

        List<Follower> template = actionMappings().get(actionType).getTemplate();
        for (Follower placed : sanitizedFollowers) {
            template.remove(placed);
        }

        return template.isEmpty();
    }

    protected List<Follower> sanitizeFollowers(List<Follower> followers) {
        List<Follower> sanitizedFollowers = new ArrayList<>();
        for (Follower follower : followers) {
            if (follower == Follower.StarterBoatman) sanitizedFollowers.add(Follower.Boatman);
            else if (follower == Follower.StarterCraftsman) sanitizedFollowers.add(Follower.Craftsman);
            else if (follower == Follower.StarterFarmer) sanitizedFollowers.add(Follower.Farmer);
            else if (follower == Follower.StarterTrader) sanitizedFollowers.add(Follower.Trader);
            else sanitizedFollowers.add(follower);
        }
        return sanitizedFollowers;
    }
}
