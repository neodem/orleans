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
 * Created on 12/27/19
 */
public interface ActionHelper {
//    /**
//     * return true if the given action accepts all the followers in the followers list
//     *
//     * @param actionType
//     * @param followers
//     * @return
//     */
//    boolean actionCanAccept(ActionType actionType, List<Follower> followers);

//    /**
//     * return true if the given actionis ready to fire (eg. all followers in place (including tech token))
//     *
//     * @param actionType
//     * @param followerTrack
//     * @param techSlot  if there is a tech token in a slot
//     * @return
//     */
//    boolean actionIsReady(ActionType actionType, FollowerTrack followerTrack, Integer techSlot);

//    /**
//     * determine if we can place all followers into an action with some placed already
//     *
//     * @param actionType
//     * @param followersToPlace
//     * @param placedInActionAlready
//     * @return
//     */
//    boolean canPlaceIntoAction(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready);

    boolean isActionValid(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);

    /**
     * @param actionType
     * @param gameState
     * @param player
     */
    void processAction(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);

    boolean isPlaceTileAction(ActionType actionType);

    PlaceTile getPlaceTile(ActionType actionType);

    /**
     * return a new/empty follower track from the template
     *
     * @param actionType
     * @return
     */
    FollowerTrack getFollowerTrack(ActionType actionType);

    /**
     * determine the follower type for a given position in an action
     *
     * @param actionType
     * @param position
     * @return
     */
    FollowerType getTypeForAction(ActionType actionType, int position);
}
