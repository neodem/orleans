package com.neodem.orleans.service;

import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.FollowerType;
import com.neodem.orleans.model.GameVersion;
import com.neodem.orleans.model.GameState;

import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public interface GameMaster {
    /**
     * will create and store (in memory) a game
     *
     * @param gameId
     * @param playerNames
     * @param gameVersion
     * @return
     */
    GameState makeGame(String gameId, List<String> playerNames, GameVersion gameVersion);

    /**
     * move to the next phase
     *
     * @param gameId
     * @return
     */
    GameState nextPhase(String gameId);

    /**
     * indicate that you are all good with your planning and ready to move forward to the next phase
     *
     * @param gameId
     * @param playerId
     * @return
     */
    GameState planSet(String gameId, String playerId);

    /**
     *
     * @param gameId
     * @param playerId
     * @param actionType
     * @param followerTypes
     * @return
     */
    GameState addToPlan(String gameId, String playerId, ActionType actionType, List<FollowerType> followerTypes);
}
