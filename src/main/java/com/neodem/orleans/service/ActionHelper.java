package com.neodem.orleans.service;

import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;
import com.neodem.orleans.model.GameState;
import com.neodem.orleans.model.PlayerState;

import java.util.List;

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
    boolean validAction(ActionType actionType, List<Follower> followers);

    /**
     * return true if the given action accepts all the types in the followerTypes list and
     * is 'complete'
     *
     * @param actionType
     * @param followers
     * @param techToken  if there is a tech token override
     * @return
     */
    boolean fullAction(ActionType actionType, List<Follower> followers, Follower techToken);

    /**
     * determine if we can place all followers into an action with some placed already
     *
     * @param actionType
     * @param followersToPlace
     * @param placedInActionAlready
     * @return
     */
    boolean canPlace(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready);

    /**
     *
     * @param actionType
     * @param gameState
     * @param player
     */
    void processAction(ActionType actionType, GameState gameState, PlayerState player);
}
