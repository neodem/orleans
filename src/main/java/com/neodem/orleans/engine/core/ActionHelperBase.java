package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerTrack;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.model.PlaceTile;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public abstract class ActionHelperBase implements ActionHelper {

    protected abstract Map<ActionType, FollowerTrack> actionMappings();

    protected abstract Map<ActionType, ActionProcessor> actionProcessors();

    protected abstract Map<ActionType, PlaceTile> placeTileMap();

    public FollowerTrack getFollowerTrack(ActionType actionType) {
        FollowerTrack template = actionMappings().get(actionType);
        return new FollowerTrack(template);
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
    public boolean isActionValid(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        ActionProcessor actionProcessor = actionProcessors().get(actionType);
        if (actionProcessor != null) {
            return actionProcessor.isValid(gameState, player, additionalDataMap);
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
    public FollowerType getTypeForAction(ActionType actionType, int position) {
        return null;
    }

    //    @Override
//    public boolean actionCanAccept(ActionType actionType, List<Follower> followers) {
//        Assert.notNull(actionType, "actionType may not be null");
//        Assert.notNull(followers, "followers may not be null");
//
//        List<Follower> sanitizedFollowers = sanitizeFollowers(followers);
//        List<Follower> monksRemoved = removeMonks(sanitizedFollowerTypes);
//
//        Grouping<FollowerType> neededFollowers = actionMappings().get(actionType);
//        Grouping<FollowerType> testFollowers = new Grouping<>(monksRemoved);
//        return testFollowers.canFitInto(neededFollowers);
//    }


//    @Override
//    public boolean canPlaceIntoAction(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready) {
//        if (placedInActionAlready == null || placedInActionAlready.isEmpty() && actionCanAccept(actionType, followersToPlace))
//            return true;
//
//        // returns a copy
//        List<FollowerType> template = actionMappings().get(actionType).getTemplate();
//        for (FollowerType placed : placedInActionAlready) {
//            template.remove(placed);
//        }
//
//        List<FollowerType> sanitizedFollowerTypes = sanitizeFollowers(followersToPlace);
//        for (FollowerType toPlace : sanitizedFollowerTypes) {
//            if (template.contains(toPlace)) template.remove(toPlace);
//            else return false;
//        }
//
//        return true;
//    }


}
