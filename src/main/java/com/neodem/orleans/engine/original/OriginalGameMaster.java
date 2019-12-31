package com.neodem.orleans.engine.original;

import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.core.model.*;
import com.neodem.orleans.engine.original.model.OriginalGameState;
import com.neodem.orleans.engine.original.model.OriginalPlayerState;
import com.neodem.orleans.engine.original.model.PlaceTile;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class OriginalGameMaster implements GameMaster {

    private final ActionHelper actionHelper;
    private Map<String, GameState> storedGames = new HashMap<>();

    public OriginalGameMaster(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
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
                PlayerState playerState = new OriginalPlayerState(playerName, pci.next(), actionHelper);
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
                    if (doStartPlayerPhase(gameState)) {
                        gameState.setGamePhase(GamePhase.HourGlass);
                    } else {
                        gameState.setGamePhase(GamePhase.Scoring);
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
                    if (doPlanningPhase(gameState)) gameState.setGamePhase(GamePhase.Actions);
                    break;
                case Actions:
                    if (doActionPhase(gameState)) gameState.setGamePhase(GamePhase.Event);
                    break;
                case Event:
                    doEventPhase(gameState);
                    gameState.setGamePhase(GamePhase.StartPlayer);
                case Scoring:
                    //TODO
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId=" + gameId);
        }
        return gameState;
    }

    private void doEventPhase(GameState gameState) {
        HourGlassTile currentHourGlass = gameState.getCurrentHourGlass();
        switch (currentHourGlass) {
            case Plague:
                handleEvent(gameState, handlePlagueEvent);
                break;
            case Taxes:
                handleEvent(gameState, handleTaxesEvent);
                break;
            case TradingDay:
                handleEvent(gameState, handleTradingDayEvent);
                break;
            case Income:
                handleEvent(gameState, handleIncomeEvent);
                break;
            case Harvest:
                handleEvent(gameState, handleHarvestEvent);
                break;
        }
    }

    private void handleEvent(GameState gameState, BiConsumer<GameState, PlayerState> eventHandler) {
        List<PlayerState> players = gameState.getPlayers();
        for (PlayerState player : players) {
            if (!isPlayingSacristy(player)) {
                eventHandler.accept(gameState, player);
            } else {
                gameState.writeLine("player " + player.getPlayerId() + " has activated Sacristy so this event skips them");
                player.unPlan(ActionType.Sacristy);
            }
        }
    }

    private boolean isPlayingSacristy(PlayerState player) {
        FollowerTrack followerTrack = player.getPlans().get(ActionType.Sacristy);
        return followerTrack != null && followerTrack.isReady(null);
    }

    private BiConsumer<GameState, PlayerState> handlePlagueEvent = (gameState, playerState) -> {
        // TODO deal with an empty bag
        Follower take = playerState.getBag().take();
        Util.mapInc(gameState.getFollowerInventory(), take.getType());
        gameState.writeLine("player " + playerState.getPlayerId() + " lost " + take + " due to the Plague  ");
    };

    private BiConsumer<GameState, PlayerState> handleIncomeEvent = (gameState, playerState) -> {
        int devTrackValue = playerState.getTradingStationCount();
        gameState.writeLine("player " + playerState.getPlayerId() + " gains " + devTrackValue + " due to the Income Event");
        playerState.addCoin(devTrackValue);
    };

    private BiConsumer<GameState, PlayerState> handleTradingDayEvent = (gameState, playerState) -> {
        int devTrackValue = playerState.getTrackValue(Track.Development);
        int devLevel = DevelopmentHelper.getLevel(devTrackValue);
        gameState.writeLine("player " + playerState.getPlayerId() + " gains " + devLevel + " due to the Trading Day Event");
        playerState.addCoin(devLevel);
    };

    private BiConsumer<GameState, PlayerState> handleTaxesEvent = (gameState, playerState) -> {
        int goodCount = playerState.getFullGoodCount();
        int tax = goodCount / 3;
        gameState.writeLine("player " + playerState.getPlayerId() + " looses " + tax + " due to the Taxes Event");
        playerState.removeCoin(tax);
        //TODO check for torture
    };

    private BiConsumer<GameState, PlayerState> handleHarvestEvent = (gameState, playerState) -> {
        if (playerState.isFoodAvailable()) {
            GoodType goodType = playerState.leastValuableFoodavailable();
            gameState.writeLine("player " + playerState.getPlayerId() + " looses " + goodType + " due to the Harvest Event");
            Util.mapDec(playerState.getGoodCounts(), goodType);
            gameState.addGoodToInventory(goodType);
        } else {
            gameState.writeLine("player " + playerState.getPlayerId() + " looses 5 coins due to the Harvest Event");
            playerState.removeCoin(5);
            //TODO check for torture
        }
    };


    private boolean doActionPhase(GameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        boolean actionNeeded = false;
        for (PlayerState player : players) {
            if (!player.isPassed()) {
                gameState.writeLine("Waiting for " + player.getPlayerId() + " to execute an action");
                actionNeeded = true;
            }
        }

        return !actionNeeded;
    }

    @Override
    public GameState doAction(String gameId, String playerId, ActionType actionType, Map<AdditionalDataType, String> additionalDataMap) {
        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {
                if (gameState.getGamePhase() == GamePhase.Actions) {
                    if (gameState.getCurrentActionPlayer().equals(player.getPlayerId())) {

                        // 1) get the plan
                        FollowerTrack followerTrack = player.getPlans().get(actionType);

                        // 2) get the techToken slot (if any)
                        Integer techSlot = player.getTechTileMap().get(actionType);

                        // 3) check if the action is ready
                        if (followerTrack.isReady(techSlot)) {

                            if (actionHelper.isActionValid(actionType, gameState, player, additionalDataMap)) {
                                // send back to bag and remove plan
                                player.unPlan(actionType);

                                // do action
                                actionHelper.processAction(actionType, gameState, player, additionalDataMap);

                                // move to the next unpassed player
                                gameState.advanceActionPlayer();
                            } else {
                                throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do action " + actionType + " but it's not allowed!");
                            }
                        } else {
                            throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do action " + actionType + " but it's not filled!");
                        }
                    } else {
                        throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do an action but it's not their turn");
                    }
                } else {
                    throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do an action but the current Phase is: " + gameState.getGamePhase());
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
    public GameState pass(String gameId, String playerId) {
        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {
                if (gameState.getGamePhase() == GamePhase.Actions) {
                    player.passActionPhase();
                } else {
                    throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to pass but the current Phase is: " + gameState.getGamePhase());
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
    public GameState addToPlan(String gameId, String playerId, ActionType actionType, int marketSlot, int actionSlot) {

        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {

                if (gameState.getGamePhase() == GamePhase.Planning) {

                    //1) does the player have a follower in that market slot?
                    if (player.getMarket().isSlotFilled(marketSlot)) {
                        throw new IllegalArgumentException("Player playerId='" + playerId + "' does not have an available in slot " + marketSlot + " of her market");
                    }

                    //2) is this action available to the player (on their base board or as an additional place?)
                    if (actionHelper.isPlaceTileAction(actionType)) {
                        PlaceTile placeTile = actionHelper.getPlaceTile(actionType);
                        if (!player.getPlaceTiles().contains(placeTile)) {
                            throw new IllegalArgumentException("Player playerId='" + playerId + "' does not have the available PlaceTile to place on" + actionType);
                        }
                    }

                    //3) get the follower from the market
                    Follower followerToken = player.removeFromMarket(marketSlot);

                    //4) can the token go into the track?
                    if (player.canAddToAction(actionType, actionSlot, followerToken)) {
                        // 5) add the token
                        player.addTokenToAction(actionType, actionSlot, followerToken);
                    } else {
                        throw new IllegalArgumentException("Player playerId='" + playerId + "' cannot place a " + followerToken + " onto slot " + actionSlot + " of their " + actionType + " action");
                    }

                } else {
                    throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to plan but the current Phase is: " + gameState.getGamePhase());
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
            if (player != null) {
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
        for (PlayerState playerState : players) {
            if (!playerState.isPlanSet()) {
                gameState.writeLine("" + playerState.getPlayerId() + " has not completed their planning!");
                planningNeeded = true;
            }
        }

        return !planningNeeded;
    }

    private void doFollowersPhase(GameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        for (PlayerState playerState : players) {
            int knightTrackLocation = playerState.getTracks().get(Track.Knights);
            int desiredDrawCount = determineDrawFromKnight(knightTrackLocation);
            int availableMarketSlots = playerState.getMarket().getAvailableSlots();

            if (availableMarketSlots == 0) {
                gameState.writeLine("" + playerState.getPlayerId() + " can't draw any followers since they have no slots available in their market");
            } else {
                if (desiredDrawCount > availableMarketSlots) desiredDrawCount = availableMarketSlots;
                playerState.drawFollowers(desiredDrawCount);
            }
        }
    }


    private void doCensusPhase(GameState gameState) {
        String most = gameState.mostFarmers();
        if (most != null) {
            gameState.getPlayer(most).addCoin();
            gameState.writeLine("" + most + " gets a coin for being the farthest on the Census/Farmer track");
        } else {
            gameState.writeLine("No one gets a coin for the Census.");
        }

        String least = gameState.leastFarmers();
        if (least != null) {
            gameState.getPlayer(least).removeCoin();
            gameState.writeLine("" + least + " pays a coin for being the least far on the Census/Farmer track");

            int coinCount = gameState.getPlayer(least).getCoinCount();
            if (coinCount < 0) {
                gameState.writeLine("" + least + " doesn't have enough money to pay for the Census, they need to be tortured!");
                //TODO torture
            }
        }
    }

    /**
     * @param gameState
     * @return false if we should not proceed
     */
    private boolean doStartPlayerPhase(GameState gameState) {
        gameState.advancePlayer();

        int round = gameState.getRound();
        if (round == 18) { // we are done
            gameState.writeLine("We are at the end of the game!");
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
