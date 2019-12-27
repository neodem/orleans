package com.neodem.orleans.service;

import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.FollowerType;
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

    private final ActionService actionService;

    public DefaultGameMaster(ActionService actionService) {
        this.actionService = actionService;
    }

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
                    if(doStartPlayerPhase(gameState)) {
                        gameState.setGamePhase(GamePhase.HourGlass);
                    } else {
                        // at game end
                    }
                    break;
                case HourGlass:
                    doHourGlassPhase(gameState);
                    gameState.setGamePhase(GamePhase.Census);
                    break;
                case Census:
                    doCensusPhase(gameState);
                    gameState.setGamePhase(GamePhase.Followers);
                    break;
                case Followers:
                    doFollowersPhase(gameState);
                    gameState.setGamePhase(GamePhase.Planning);
                    break;
                case Planning:
                    if(doPlanningPhase(gameState)) gameState.setGamePhase(GamePhase.Actions);
                    break;

            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId=" + gameId);
        }
        return gameState;
    }

    @Override
    public GameState addToPlan(String gameId, String playerId, ActionType actionType, List<FollowerType> followerTypes) {

        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if(player != null) {
                // 1) validate followers can fit on the action type
                if(actionService.validAction(actionType, followerTypes)) {
                    if(player.availableInMarket(followerTypes)) {
                        player.removeFromMarket(followerTypes);
                        player.addToPlan(actionType, followerTypes);
                    } else {
                        throw new IllegalArgumentException("Player playerId='" + playerId + "' does not have one or more of these followers in their market");
                    }
                } else {
                    throw new IllegalArgumentException("Player playerId='" + playerId + "' has applied the incorrect followers to this action");
                }
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }
        return gameState;
    }

    @Override
    public GameState planSet(String gameId, String playerId) {
        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if(player != null) {
                player.lockPlan();
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }
        return gameState;
    }

    private boolean doPlanningPhase(GameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        boolean planningNeeded = false;
        for(PlayerState playerState : players) {
            if(!playerState.isPlanSet()) {
                gameState.gameLog("" + playerState.getPlayerId() + " has not completed their planning!");
                planningNeeded = true;
            }
        }

        return !planningNeeded;
    }

    private void doFollowersPhase(GameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        for(PlayerState playerState : players) {
            int knightTrackLocation = playerState.getTracks().get(Track.Knights);
            int drawCount = determineDrawFromKnight(knightTrackLocation);
            playerState.drawFollowers(drawCount);
        }
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

    /**
     *
     * @param gameState
     * @return false if we should not proceed
     */
    private boolean doStartPlayerPhase(GameState gameState) {
        gameState.advancePlayer();

        int round = gameState.getRound();
        if (round == 18) { // we are done
            gameState.gameLog("We are at the end of the game!");
            return false;
        } else {
            gameState.setRound(++round);
        }

        return true;
    }

    private void doHourGlassPhase(GameState gameState) {
        HourGlassTile currentHourGlass = gameState.getCurrentHourGlass();
        if (currentHourGlass != null) gameState.getUsedHourGlassTiles().add(currentHourGlass);
        gameState.setCurrentHourGlass(gameState.getHourGlassStack().get(0));
        gameState.getHourGlassStack().remove(0);
    }

    private int determineDrawFromKnight(int knightTrackLocation) {
        switch (knightTrackLocation) {
            case 0:
                return 4;
            case 1:
                return 5;
            case 2:
                return 6;
            case 3:
            case 4:
                return 7;
            case 5:
                return 8;
        }
        return 0;
    }
}
