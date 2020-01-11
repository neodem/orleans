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
