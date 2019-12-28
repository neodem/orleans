package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.engine.core.Loggable;
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
    private final int playerCount;
    protected String gameId;

    protected int round;
    protected GamePhase gamePhase;

    protected final List<PlayerState> players = new ArrayList<>();
    protected int startPlayer = -1;

    protected BoardState boardState;

    protected final Map<GoodType, Integer> goodsInventory = new HashMap<>();
    protected final Map<Follower, Integer> followerInventory = new HashMap<>();

    protected final Collection<PlaceTile> placeTiles1 = new HashSet<>();
    protected final Collection<PlaceTile> placeTiles2 = new HashSet<>();

    protected HourGlassTile currentHourGlass;

    // TODO hide this from JSON
    protected final List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
    protected final List<HourGlassTile> usedHourGlassTiles = new ArrayList<>();

    protected final List<String> gameLog = new ArrayList<>();

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

    @Override
    public void writeLine(String line) {
        gameLog.add(line);
    }

    public HourGlassTile getCurrentHourGlass() {
        return currentHourGlass;
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

    public void setCurrentHourGlass(HourGlassTile currentHourGlass) {
        this.currentHourGlass = currentHourGlass;
        writeLine("HourGlass changed to: " + currentHourGlass);
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public Map<Follower, Integer> getFollowerInventory() {
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
}
