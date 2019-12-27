package com.neodem.orleans.service;

import com.neodem.orleans.model.GamePhase;
import com.neodem.orleans.model.GameState;
import com.neodem.orleans.model.GameVersion;
import com.neodem.orleans.model.HourGlassTile;
import com.neodem.orleans.model.OriginalGameState;
import com.neodem.orleans.model.OriginalPlayerState;
import com.neodem.orleans.model.PlayerColor;
import com.neodem.orleans.model.PlayerState;
import com.neodem.orleans.model.Track;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class DefaultGameMaster implements GameMaster {

    private Map<String, GameState> storedGames = new HashMap<>();

    @Override
    public GameState makeGame(String gameId, List<String> playerNames, GameVersion gameVersion) {

        GameState gameState;

        if (storedGames.containsKey(gameId)) {
            throw new IllegalArgumentException("Game with id = " + gameId + " exists already!");
        }

        if (!playerNames.isEmpty() && playerNames.size() > 1 && playerNames.size() <= 4) {

            if (gameVersion == GameVersion.Original) {
                gameState = new OriginalGameState(gameId, playerNames.size());
            } else {
                throw new IllegalArgumentException("Only Original game is implemented");
            }

            Set<PlayerColor> playerColors = new HashSet<>();
            do {
                playerColors.add(PlayerColor.randomColor());
            } while (playerColors.size() != playerNames.size());
            Iterator<PlayerColor> pci = playerColors.iterator();

            for (String playerName : playerNames) {
                PlayerState playerState = new OriginalPlayerState(playerName, pci.next());
                gameState.addPlayer(playerState);
            }

            storedGames.put(gameId, gameState);
        } else {
            throw new IllegalArgumentException("we must have 2-4 players to init game");
        }

        return gameState;
    }

    @Override
    public GameState nextPhase(String gameId) {
        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            GamePhase gamePhase = gameState.getGamePhase();
            switch (gamePhase) {
                case Setup:
                case StartPlayer:
                    doStartPlayerPhase(gameState);
                    gameState.setGamePhase(GamePhase.HourGlass);
                    break;
                case HourGlass:
                    doHourGlassPhase(gameState);
                    gameState.setGamePhase(GamePhase.Census);
                    break;
                case Census:
                    doCensusPhase(gameState);
                    gameState.setGamePhase(GamePhase.Followers);
                    break;

            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId=" + gameId);
        }
        return gameState;
    }

    private void doCensusPhase(GameState gameState) {
        String most = gameState.mostFarmers();
        if(most != null) {
            gameState.getPlayer(most).addCoin();
            gameState.gameLog("" + most + " gets a coin for being the farthest on the Census/Farmer track");
        } else {
            gameState.gameLog("No one gets a coin for the Census.");
        }

        String least = gameState.leastFarmers();
        if(least != null) {
            gameState.getPlayer(least).removeCoin();
            gameState.gameLog("" + least + " pays a coin for being the least far on the Census/Farmer track");

            int coinCount = gameState.getPlayer(least).getCoinCount();
            if(coinCount < 0) {
                gameState.gameLog("" + least + " doesn't have enough money to pay for the Census, they need to be tortured!");
                //TODO torture
            }
        }
    }

    private void doStartPlayerPhase(GameState gameState) {
        gameState.advancePlayer();

        int round = gameState.getRound();
        if (round == 18) { // we are done
            gameState.gameLog("We are at the end of the game!");
        }
    }

    private void doHourGlassPhase(GameState gameState) {

        int round = gameState.getRound();
        if (round == 18) { // we are done
            // TODO
        } else {
            gameState.setRound(++round);
        }

        HourGlassTile currentHourGlass = gameState.getCurrentHourGlass();
        if (currentHourGlass != null) gameState.getUsedHourGlassTiles().add(currentHourGlass);
        gameState.setCurrentHourGlass(gameState.getHourGlassStack().get(0));
        gameState.getHourGlassStack().remove(0);
    }
}
