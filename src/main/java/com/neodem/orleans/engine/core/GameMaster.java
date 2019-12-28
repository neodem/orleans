package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.model.GameState;

import java.util.List;

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
     * @param followers
     * @return
     */
    GameState addToPlan(String gameId, String playerId, ActionType actionType, List<Follower> followers);

    /**
     * player does an action
     * @param gameId
     * @param playerId
     * @param actionType
     * @return
     */
    GameState doAction(String gameId, String playerId, ActionType actionType);

    /**
     * player does an action
     * @param gameId
     * @param playerId
     * @return
     */
    GameState pass(String gameId, String playerId);
}
