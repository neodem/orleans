package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.model.PlayerColor;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TortureType;
import com.neodem.orleans.engine.original.model.OriginalPlayerState;
import com.neodem.orleans.service.GameStateService;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/11/20
 */
public abstract class BaseGameMaster<G extends GameState> implements GameMaster {

    protected final ActionHelper actionHelper;
    private final GameStateService<G> gameStateService;

    public BaseGameMaster(ActionHelper actionHelper, GameStateService<G> gameStateService) {
        this.actionHelper = actionHelper;
        this.gameStateService = gameStateService;
    }

    /**
     * make and init a GameState Object
     *
     * @param gameId
     * @param playerCount
     * @param gameVersion
     * @return
     */
    protected abstract G makeGame(String gameId, int playerCount, GameVersion gameVersion);

    /**
     * return the max number of players for a game
     *
     * @return
     */
    protected abstract int playersMax();

    /**
     * return the min number of players for a game
     *
     * @return
     */
    protected abstract int playersMin();

    @Override
    public G makeGame(String gameId, List<String> playerNames, GameVersion gameVersion) {

        if (gameStateService.gameStateExists(gameId)) {
            throw new IllegalArgumentException("Game with id = " + gameId + " exists already!");
        }

        G gameState;
        if (!playerNames.isEmpty() && playerNames.size() >= playersMin() && playerNames.size() <= playersMax()) {

            gameState = makeGame(gameId, playerNames.size(), gameVersion);

            Set<PlayerColor> playerColors = new HashSet<>();
            do {
                playerColors.add(PlayerColor.randomColor());
            } while (playerColors.size() != playerNames.size());
            Iterator<PlayerColor> pci = playerColors.iterator();

            for (String playerName : playerNames) {
                PlayerState playerState = new OriginalPlayerState(playerName, pci.next(), actionHelper);
                gameState.addPlayer(playerState);
            }

            gameStateService.saveGameState(gameState);
        } else {
            throw new IllegalArgumentException("we must have the correct number of players to init game. This game type supports from " + playersMin() + " to " + playersMax() + " players");
        }

        return gameState;
    }

    @Override
    public G getGameState(String gameId) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);
        gameStateService.cancelLease(gameId);
        return gameState;
    }

    @Override
    public G startGame(String gameId) {
        return advance(gameId);
    }

    /**
     * advance the game until we get to a point where a player decision needs to be made
     *
     * @param gameId
     * @return
     */
    protected G advance(String gameId) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);
        if (gameState != null) {
            boolean moveToNextPhase;
            do {
                moveToNextPhase = nextPhase(gameState);
            } while (moveToNextPhase);
        }
        gameStateService.saveGameState(gameState);
        return gameState;
    }

    protected abstract boolean nextPhase(G gameState);

    @Override
    public G doAction(String gameId, String playerId, ActionType actionType, Map<AdditionalDataType, String> additionalDataMap) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);

        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {

                gameState = doActionForPlayer(gameState, player, actionType, additionalDataMap);

            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        gameStateService.saveGameState(gameState);
        return gameState;
    }

    protected abstract G doActionForPlayer(G gameState, PlayerState player, ActionType actionType, Map<AdditionalDataType, String> additionalDataMap);

    @Override
    public G addToPlan(String gameId, String playerId, ActionType actionType, int marketSlot, int actionSlot) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);

        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {

                gameState = doAddToPlanForPlayer(gameState, player, actionType, marketSlot, actionSlot);

            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        gameStateService.saveGameState(gameState);
        return gameState;
    }

    protected abstract G doAddToPlanForPlayer(G gameState, PlayerState player, ActionType actionType, int marketSlot, int actionSlot);

    @Override
    public G pass(String gameId, String playerId) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);

        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {
                player.setPhaseComplete(true);
                gameState.writeLine("player " + player.getPlayerId() + " has completed their turn (plan/action)");
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        if (gameState.isPhaseComplete()) {
            gameStateService.cancelLease(gameId);
            gameState = advance(gameId);
        }

        gameStateService.saveGameState(gameState);
        return gameState;
    }

    @Override
    public G torturePlan(String gameId, String playerId, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap) {
        G gameState = gameStateService.waitAndLeaseGameState(gameId);

        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {
                if (player.isBeingTortured()) {
                    doEndureTortureForPlayer(player, tortureType, additionalDataMap);
                } else {
                    throw new IllegalArgumentException("playerId='" + playerId + "is trying to submit a torture plan but isn't being tortured!");
                }
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        //TODO continue game if torture is over, else stay paused/interrupted

        gameStateService.saveGameState(gameState);
        return gameState;
    }

    protected abstract void doEndureTortureForPlayer(PlayerState player, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap);
}
