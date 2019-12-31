package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.BenefitTracker;
import com.neodem.orleans.engine.core.Loggable;
import com.neodem.orleans.engine.original.model.CitizenType;
import com.neodem.orleans.engine.original.model.PlaceTile;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public abstract class GameState implements Loggable {
    protected final List<PlayerState> players = new ArrayList<>();
    protected BoardState boardState;
    protected BenefitTracker benefitTracker;

    protected final Map<GoodType, Integer> goodsInventory = new HashMap<>();
    protected final Map<FollowerType, Integer> followerInventory = new HashMap<>();

    protected int techTilesAvailable = 0;
    protected final Collection<PlaceTile> placeTiles1 = new HashSet<>();
    protected final Collection<PlaceTile> placeTiles2 = new HashSet<>();
    // TODO hide this from JSON
    protected final List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
    protected final List<HourGlassTile> usedHourGlassTiles = new ArrayList<>();
    protected final List<String> gameLog = new ArrayList<>();
    private final int playerCount;
    private final Collection<CitizenType> claimedCitizens = new HashSet<>();
    protected String gameId;
    protected int round;
    protected GamePhase gamePhase;
    protected int startPlayer = -1;

    protected HourGlassTile currentHourGlass;
    int currentActionPlayerIndex = 0;

    public GameState(String gameId, int playerCount) {
        Assert.isTrue(playerCount > 1 && playerCount < 5, "playerCount should be 2-4");
        this.gameId = gameId;
        this.playerCount = playerCount;
        initGame(playerCount);
    }

    protected void initForPlayerCount(int playerCount) {
        if (playerCount == 2) initFor2Players();
        else if (playerCount == 3) initFor3Players();
        else initFor4Players();
    }

    protected abstract void initFor4Players();

    protected abstract void initFor3Players();

    protected abstract void initFor2Players();

    public BenefitTracker getBenefitTracker() {
        return benefitTracker;
    }

    public int getTechTilesAvailable() {
        return techTilesAvailable;
    }

    public void setTechTilesAvailable(int techTilesAvailable) {
        this.techTilesAvailable = techTilesAvailable;
    }

    public Map<TokenLocation, Collection<String>> getAllTradingStations() {
        Map<TokenLocation, Collection<String>> allTradingStations = new HashMap<>();

        for (PlayerState player : players) {
            Collection<TokenLocation> locsForPlayer = player.getTradingStationLocations();
            for (TokenLocation location : locsForPlayer) {
                Collection<String> names = allTradingStations.get(location);
                if (names == null) names = new HashSet<>();
                names.add(player.getPlayerId());
                allTradingStations.put(location, names);
            }
        }

        return allTradingStations;
    }

    @Override
    public void writeLine(String line) {
        gameLog.add(line);
    }

    public void removeFollowerFromInventory(FollowerType desiredFollower) {
        int followerCount = followerInventory.get(desiredFollower);
        followerInventory.put(desiredFollower, --followerCount);
    }

    public void removeGoodFromInventory(GoodType goodType) {
        Util.mapDec(goodsInventory, goodType);
    }

    public HourGlassTile getCurrentHourGlass() {
        return currentHourGlass;
    }

    public void setCurrentHourGlass(HourGlassTile currentHourGlass) {
        this.currentHourGlass = currentHourGlass;
        writeLine("HourGlass changed to: " + currentHourGlass);
    }

    public abstract void initGame(int playerCount);

    public String getStartPlayer() {
        return players.get(startPlayer).getPlayerId();
    }

    public void advancePlayer() {
        startPlayer++;
        if (startPlayer == players.size()) startPlayer = 0;
        writeLine("Start Player set to: " + getStartPlayer());
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Map<FollowerType, Integer> getFollowerInventory() {
        return followerInventory;
    }

    public List<String> getGameLog() {
        return gameLog;
    }

    /**
     * determine who has the most farmers. In a tie, we return null
     *
     * @return
     */
    public String mostFarmers() {
        String maxId = null;
        int max = -1;
        int tie = -1;

        for (PlayerState player : players) {
            Integer index = player.getTracks().get(Track.Farmers);
            if (index == max) {
                tie = index;
            }
            if (index > max) {
                max = index;
                maxId = player.getPlayerId();
            }
        }

        if (tie == max) return null;

        return maxId;
    }

    /**
     * determine who has the least farmers. In a tie, we return null. In a 2p game, we return null
     *
     * @return
     */
    public String leastFarmers() {
        if (playerCount == 2) return null;

        String minId = null;
        int min = 100;
        int tie = -1;

        for (PlayerState player : players) {
            Integer index = player.getTracks().get(Track.Farmers);
            if (index == min) {
                tie = index;
            }
            if (index < min) {
                min = index;
                minId = player.getPlayerId();
            }
        }

        if (tie == min) return null;

        return minId;
    }

    public String getGameId() {
        return gameId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        writeLine("Round " + round + " started");
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        writeLine("Phase: " + gamePhase);
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerState player) {
        player.addLog(this);
        this.players.add(player);
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public Map<GoodType, Integer> getGoodsInventory() {
        return goodsInventory;
    }

    public Collection<PlaceTile> getPlaceTiles1() {
        return placeTiles1;
    }

    public Collection<PlaceTile> getPlaceTiles2() {
        return placeTiles2;
    }

    public List<HourGlassTile> getHourGlassStack() {
        return hourGlassTileStack;
    }

    public List<HourGlassTile> getUsedHourGlassTiles() {
        return usedHourGlassTiles;
    }

    public PlayerState getPlayer(String playerId) {
        for (PlayerState playerState : players) {
            if (playerState.getPlayerId().equals(playerId)) return playerState;
        }
        return null;
    }

    public boolean isCitizenClaimed(CitizenType citizenType) {
        return claimedCitizens.contains(citizenType);
    }

    public void citizenClaimed(CitizenType citizenType) {
        claimedCitizens.add(citizenType);
    }

    public String getCurrentActionPlayer() {
        return players.get(currentActionPlayerIndex).getPlayerId();
    }

    public void advanceActionPlayer() {
        int count = 1;
        do {
            currentActionPlayerIndex++;
            count++;
        } while (players.get(currentActionPlayerIndex).isPassed() && count == playerCount);
    }

    public void removeTechTileFromInventory() {
        techTilesAvailable--;
    }


    public boolean isGoodAvailable(GoodType goodType) {
        return goodsInventory.get(goodType) > 0;
    }

    public void addGoodToInventory(GoodType goodType) {
        Util.mapInc(goodsInventory, goodType);
    }
}
