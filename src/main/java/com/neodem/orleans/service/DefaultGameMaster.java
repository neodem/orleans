package com.neodem.orleans.service;

import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.GameVersion;
import com.neodem.orleans.objects.OriginalGameState;
import com.neodem.orleans.objects.OriginalPlayerState;
import com.neodem.orleans.objects.PlayerState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class DefaultGameMaster implements GameMaster {

    private Map<String, GameState> storedGames = new HashMap<>();

    @Override
    public GameState makeGame(String gameId, List<String> playerNames, GameVersion gameVersion) {

        if(storedGames.containsKey(gameId)) {
            throw new IllegalArgumentException("Game with id = " + gameId + " exists already!");
        }

        GameState gameState;
        if (gameVersion == GameVersion.Original) {
            gameState = new OriginalGameState(gameId);
        } else {
            throw new IllegalArgumentException("Only Original game is implemented");
        }

        for(String playerName : playerNames) {
            PlayerState playerState = new OriginalPlayerState(playerName);
            gameState.addPlayer(playerState);
        }

        storedGames.put(gameId, gameState);

        return gameState;
    }
}
