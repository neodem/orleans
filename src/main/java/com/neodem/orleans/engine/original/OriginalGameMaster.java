package com.neodem.orleans.engine.original;

import com.google.common.collect.Sets;
import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
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

import static com.neodem.orleans.engine.core.model.AdditionalDataType.follower;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class OriginalGameMaster implements GameMaster {

    private final ActionHelper actionHelper;
    private Map<String, OriginalGameState> storedGames = new HashMap<>();

    public OriginalGameMaster(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
    }

    @Override
    public GameState makeGame(String gameId, List<String> playerNames, GameVersion gameVersion) {

        OriginalGameState gameState;

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
    public GameState getGameState(String gameId) {
        return storedGames.get(gameId);
    }

    @Override
    public GameState startGame(String gameId) {
        return advance(gameId);
    }

    /**
     * advance the game until we get to a point where a player decision needs to be made
     *
     * @param gameId
     * @return
     */
    protected GameState advance(String gameId) {
        OriginalGameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            boolean moveToNextPhase;
            do {
                moveToNextPhase = nextPhase(gameState);
            } while (moveToNextPhase);
        }
        return gameState;
    }

    /**
     * @param gameState
     * @return true if we should move to the next phase
     */
    protected boolean nextPhase(OriginalGameState gameState) {
        GamePhase gamePhase = gameState.getGamePhase();
        boolean phaseComplete = true;
        switch (gamePhase) {
            case Setup:
            case StartPlayer:
                phaseComplete = doStartPlayerPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.HourGlass);
                    resetPhaseCompleteFlags(gameState);
                }
                break;
            case HourGlass:
                phaseComplete = doHourGlassPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Census);
                    resetPhaseCompleteFlags(gameState);
                }
            case Census:
                phaseComplete = doCensusPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Followers);
                    resetPhaseCompleteFlags(gameState);
                }
            case Followers:
                phaseComplete = doFollowersPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Planning);
                    resetPhaseCompleteFlags(gameState);
                }
                break;
            case Planning:
                phaseComplete = doPlanningPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Actions);
                    resetPhaseCompleteFlags(gameState);
                }
                break;
            case Actions:
                phaseComplete = doActionPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Event);
                    resetPhaseCompleteFlags(gameState);
                }
                break;
            case Event:
                phaseComplete = doEventPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.StartPlayer);
                    resetPhaseCompleteFlags(gameState);
                }
                break;
            case Scoring:
                phaseComplete = doScoringPhase(gameState);
                if (phaseComplete) {
                    gameState.setGamePhase(GamePhase.Over);
                }
                break;
        }
        return phaseComplete;
    }

    private boolean doScoringPhase(OriginalGameState gameState) {
        return true;
    }

    private void resetPhaseCompleteFlags(OriginalGameState gameState) {
        // reset the phase complete flags
        for (PlayerState playerState : gameState.getPlayers()) {
            playerState.setPhaseComplete(false);
        }
    }

    /**
     * @param gameState
     * @return false if we should not proceed
     */
    private boolean doStartPlayerPhase(OriginalGameState gameState) {
        gameState.advancePlayer();
        gameState.syncActionPlayer();
        int round = gameState.getRound();
        gameState.setRound(++round);
        return true;
    }

    private boolean doHourGlassPhase(OriginalGameState gameState) {
        HourGlassTile currentHourGlass = gameState.getCurrentHourGlass();
        if (currentHourGlass != null) gameState.getUsedHourGlassTiles().add(currentHourGlass);
        gameState.setCurrentHourGlass(gameState.getHourGlassStack().get(0));
        gameState.getHourGlassStack().remove(0);
        return true;
    }

    private boolean doCensusPhase(OriginalGameState gameState) {
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
        return true;
    }

    private boolean doFollowersPhase(OriginalGameState gameState) {

        // if a player has the bathhouse, we do their follower phase differently
        String playerWithBathHouse = gameState.getPlayerHasBathhouse();
        boolean bathhouseCompleted = false;
        List<PlayerState> players = gameState.getPlayers();
        for (PlayerState playerState : players) {
            if (!playerState.isPhaseComplete()) {
                int knightTrackLocation = playerState.getTrackLocation(Track.Knights);
                int desiredDrawCount = determineDrawFromKnight(knightTrackLocation);

                playerState.drawFollowersFromBagToMarket(desiredDrawCount);

                if (playerWithBathHouse != null && playerWithBathHouse.equals(playerState.getPlayerId()) && !bathhouseCompleted) {
                    int availableMarketSlots = playerState.getAvailableMarketSlots();
                    if (availableMarketSlots > 0) {
                        FollowerType followerType = playerState.getBathhouseChoice();
                        if (followerType != null) {
                            Follower follower = playerState.getBag().takeOfType(followerType);
                            playerState.addToMarket(follower);
                            playerState.resetBathhouseChoice();
                            bathhouseCompleted = true;
                        } else {
                            gameState.writeLine("" + playerState.getPlayerId() + " has the bathhouse and an available slot.");
                            Follower choice1 = playerState.getBag().take();
                            Follower choice2 = playerState.getBag().take();
                            if (choice1 == null && choice2 == null) {
                                gameState.writeLine("" + playerState.getPlayerId() + " unfortunately has nothing in her bag.");
                                bathhouseCompleted = true;
                            } else {
                                if (choice1 == null || choice2 == null) {
                                    gameState.writeLine("" + playerState.getPlayerId() + " has only one follower in her back. It will be added as their Bathhouse choice.");
                                    if (choice1 != null) playerState.addToMarket(choice1);
                                    else playerState.addToMarket(choice2);
                                    bathhouseCompleted = true;
                                } else {
                                    gameState.writeLine("" + playerState.getPlayerId() + " has to choose which follower to assign from:" + choice1 + " and " + choice2);
                                    playerState.setBathhouseChoices(Sets.newHashSet(choice1.getFollowerType(), choice2.getFollowerType()));
                                }
                            }
                        }
                    } else {
                        gameState.writeLine("" + playerState.getPlayerId() + " has the bathhouse but no available slot in her market.");
                        playerState.setPhaseComplete(true);
                        bathhouseCompleted = true;
                    }
                } else {
                    playerState.setPhaseComplete(true);
                }
            }
        }

        return !bathhouseCompleted;
    }

    private boolean doPlanningPhase(OriginalGameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        boolean planningNeeded = false;
        for (PlayerState playerState : players) {
            if (!playerState.isPhaseComplete()) {
                gameState.writeLine("" + playerState.getPlayerId() + " has not completed their planning!");
                planningNeeded = true;
            }
        }

        return !planningNeeded;
    }

    private boolean doActionPhase(OriginalGameState gameState) {
        List<PlayerState> players = gameState.getPlayers();
        boolean actionNeeded = false;
        for (PlayerState player : players) {
            if (!player.isPhaseComplete()) {
                gameState.writeLine("Waiting for " + player.getPlayerId() + " to execute an action");
                actionNeeded = true;
            }
        }

        return !actionNeeded;
    }

    private boolean doEventPhase(OriginalGameState gameState) {
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

        return true;
    }

    private void handleEvent(GameState gameState, BiConsumer<GameState, PlayerState> eventHandler) {
        List<PlayerState> players = gameState.getPlayers();
        for (PlayerState player : players) {
            if (!player.isPlayingSacristy()) {
                eventHandler.accept(gameState, player);
            } else {
                gameState.writeLine("player " + player.getPlayerId() + " has activated Sacristy so this event skips them");
                player.unPlan(ActionType.Sacristy);
            }
        }
    }

    private BiConsumer<GameState, PlayerState> handlePlagueEvent = (gameState, playerState) -> {
        // TODO deal with an empty bag
        Follower take = playerState.getBag().take();
        Util.mapInc(gameState.getFollowerInventory(), take.getFollowerType());
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
            GoodType goodType = playerState.leastValuableFoodAvailable();
            gameState.writeLine("player " + playerState.getPlayerId() + " looses " + goodType + " due to the Harvest Event");
            playerState.removeGood(goodType);
            gameState.addGoodToInventory(goodType);
        } else {
            gameState.writeLine("player " + playerState.getPlayerId() + " looses 5 coins due to the Harvest Event");
            playerState.removeCoin(5);
            //TODO check for torture
        }
    };


    @Override
    public GameState doAction(String gameId, String playerId, ActionType
            actionType, Map<AdditionalDataType, String> additionalDataMap) {
        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {

                if (actionType == ActionType.Bathhouse && player.hasPlaceTile(PlaceTile.Bathhouse)) {
                    if (gameState.getGamePhase() == GamePhase.Followers) {
                        FollowerType followerType = ActionProcessorBase.getFollowerFromMap(additionalDataMap, follower);
                        if (followerType != null) {
                            player.setBathhouseChoices(Sets.newHashSet(followerType));
                        }
                    }
                }

                if (gameState.getGamePhase() == GamePhase.Actions) {
                    if (gameState.getCurrentActionPlayer().equals(player.getPlayerId())) {

                        // 1) get the plan
                        FollowerTrack followerTrack = player.getPlan(actionType);

                        if (followerTrack != null) {

                            // 2) get the techToken slot (if any)
                            Integer techSlot = player.getTechTileSlot(actionType);

                            // 3) check if the action is ready
                            if (followerTrack.isReady(techSlot)) {

                                if (actionHelper.isActionValid(actionType, gameState, player, additionalDataMap)) {

                                    // do action
                                    actionHelper.processAction(actionType, gameState, player, additionalDataMap);

                                    // send back to bag and remove plan
                                    player.unPlan(actionType);

                                    // move to the next unpassed player
                                    gameState.advanceActionPlayer();
                                } else {
                                    throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do action " + actionType + " but it's not allowed!");
                                }
                            } else {
                                throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to do action " + actionType + " but it's not filled!");
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
    public GameState addToPlan(String gameId, String playerId, ActionType actionType, int marketSlot, int actionSlot) {

        GameState gameState = storedGames.get(gameId);
        if (gameState != null) {
            PlayerState player = gameState.getPlayer(playerId);
            if (player != null) {
                if (gameState.getGamePhase() == GamePhase.Planning) {
                    if (!player.isPhaseComplete()) {

                        //1) does the player have a follower in that market slot?
                        if (!player.isMarketSlotFilled(marketSlot)) {
                            throw new IllegalArgumentException("Player playerId='" + playerId + "' does not have an available follower in market slot " + marketSlot + " of her market");
                        }

                        //2) is this action available to the player (on their base board or as an additional place?)
                        if (actionHelper.isPlaceTileAction(actionType)) {
                            PlaceTile placeTile = actionHelper.getPlaceTile(actionType);
                            if (!player.hasPlaceTile(placeTile)) {
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
                            player.addToMarket(followerToken);
                            throw new IllegalArgumentException("Player playerId='" + playerId + "' cannot place a " + followerToken + " onto slot " + actionSlot + " of their " + actionType + " action");
                        }
                    } else {
                        throw new IllegalArgumentException("Player playerId='" + playerId + "' has completd their planning phase.");
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
                player.setPhaseComplete(true);
                gameState.writeLine("player " + player.getPlayerId() + " has completed planning");
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        if (gameState.isPhaseComplete()) {
            gameState = advance(gameId);
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
                    player.setPhaseComplete(true);
                } else {
                    throw new IllegalStateException("Player playerId='" + playerId + "' is attempting to pass but the current Phase is: " + gameState.getGamePhase());
                }
            } else {
                throw new IllegalArgumentException("No player exists for playerId='" + playerId + "' in gameId='" + gameId + "'");
            }
        } else {
            throw new IllegalArgumentException("No game exists for gameId='" + gameId + "'");
        }

        if (gameState.isPhaseComplete()) {
            gameState = advance(gameId);
        }

        return gameState;
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
