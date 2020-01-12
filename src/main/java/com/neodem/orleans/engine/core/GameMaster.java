package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.model.TortureType;

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

    GameState startGame(String gameId);

    /**
     * @param gameId
     * @param playerId
     * @param actionType
     * @param marketSlot
     * @param actionSlot
     * @return
     */
    GameState addToPlan(String gameId, String playerId, ActionType actionType, int marketSlot, int actionSlot);

    /**
     * player does an action
     *
     * @param gameId
     * @param playerId
     * @param actionType
     * @param additionalDataMap other data from the request
     * @return
     */
    GameState doAction(String gameId, String playerId, ActionType actionType, Map<AdditionalDataType, String> additionalDataMap);

    /**
     * indicate that you are all good with your planning or done actions and ready to move forward to the next phase
     *
     * @param gameId
     * @param playerId
     * @return
     */
    GameState pass(String gameId, String playerId);

    GameState getGameState(String gameId);

    /**
     * submit a plan to remove a single coin of torture
     *
     * @param gameId
     * @param playerId
     * @param tortureType
     * @param additionalDataMap
     * @return
     */
    GameState torturePlan(String gameId, String playerId, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap);
}
