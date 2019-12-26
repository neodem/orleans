package com.neodem.orleans.objects;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class GameState {
    private String gameId;

    private int round;
    private GamePhase gamePhase;

    private List<PlayerState> players;

    private BoardState boardState;
    private Map<GoodType, Integer> goodsInventory;

    private Collection<PlaceTile> placeTiles1;
    private Collection<PlaceTile> placeTiles2;

    private List<HourGlassTile> hourGlassStack;
    private List<HourGlassTile> usedHourGlassTiles;

    public GameState(String gameId) {
        this.gameId = gameId;
    }

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

    public void setPlayers(List<PlayerState> players) {
        this.players = players;
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public void setBoardState(BoardState boardState) {
        this.boardState = boardState;
    }

    public Map<GoodType, Integer> getGoodsInventory() {
        return goodsInventory;
    }

    public void setGoodsInventory(Map<GoodType, Integer> goodsInventory) {
        this.goodsInventory = goodsInventory;
    }

    public Collection<PlaceTile> getPlaceTiles1() {
        return placeTiles1;
    }

    public void setPlaceTiles1(Collection<PlaceTile> placeTiles1) {
        this.placeTiles1 = placeTiles1;
    }

    public Collection<PlaceTile> getPlaceTiles2() {
        return placeTiles2;
    }

    public void setPlaceTiles2(Collection<PlaceTile> placeTiles2) {
        this.placeTiles2 = placeTiles2;
    }

    public List<HourGlassTile> getHourGlassStack() {
        return hourGlassStack;
    }

    public void setHourGlassStack(List<HourGlassTile> hourGlassStack) {
        this.hourGlassStack = hourGlassStack;
    }

    public List<HourGlassTile> getUsedHourGlassTiles() {
        return usedHourGlassTiles;
    }

    public void setUsedHourGlassTiles(List<HourGlassTile> usedHourGlassTiles) {
        this.usedHourGlassTiles = usedHourGlassTiles;
    }
}
