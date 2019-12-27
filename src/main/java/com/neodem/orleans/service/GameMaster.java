package com.neodem.orleans.service;

import com.neodem.orleans.model.GameVersion;
import com.neodem.orleans.model.GameState;

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
}
