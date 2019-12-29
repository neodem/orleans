package com.neodem.orleans.engine.core;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.original.model.PlaceTile;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public interface ActionHelper {
    /**
     * return true if the given action accepts all the types in the followerTypes list
     *
     * @param actionType
     * @param followers
     * @return
     */
    boolean actionCanAccept(ActionType actionType, List<Follower> followers);

    /**
     * return true if the given action accepts all the types in the followerTypes list and
     * is 'complete'
     *
     * @param actionType
     * @param followers
     * @param techToken  if there is a tech token override
     * @return
     */
    boolean actionIsFull(ActionType actionType, List<Follower> followers, Follower techToken);

    /**
     * determine if we can place all followers into an action with some placed already
     *
     * @param actionType
     * @param followersToPlace
     * @param placedInActionAlready
     * @return
     */
    boolean canPlaceIntoAction(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready);

    boolean isActionAllowed(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);

    /**
     * @param actionType
     * @param gameState
     * @param player
     */
    void processAction(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);

    boolean isCommonAction(ActionType actionType);
    boolean isPlaceTileAction(ActionType actionType);
    PlaceTile getPlaceTile(ActionType actionType);

    /**
     *
     * @param actionType
     * @return
     */
    Grouping getGrouping(ActionType actionType);
}
