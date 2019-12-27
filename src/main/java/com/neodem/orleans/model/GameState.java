package com.neodem.orleans.model;

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
public abstract class GameState {
    protected String gameId;

    protected int round;
    protected GamePhase gamePhase;

    protected final List<PlayerState> players = new ArrayList<>();

    protected BoardState boardState;
    protected final Map<GoodType, Integer> goodsInventory = new HashMap<>();
    protected final Map<FollowerType, Integer> followerInventory = new HashMap<>();

    protected final Collection<PlaceTile> placeTiles1 = new HashSet<>();
    protected final Collection<PlaceTile> placeTiles2 = new HashSet<>();

    protected final List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
    protected final List<HourGlassTile> usedHourGlassTiles = new ArrayList<>();

    public GameState(String gameId, int playerCount) {
        Assert.isTrue(playerCount>1 && playerCount < 5, "playerCount should be 2-4");
        this.gameId = gameId;
        initGame(playerCount);
    }

    public abstract void initGame(int playerCount);

    public String getGameId() {
        return gameId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerState player) {
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
}
