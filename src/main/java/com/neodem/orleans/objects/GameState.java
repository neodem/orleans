package com.neodem.orleans.objects;

import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.objects.HourGlassTile.*;
import static com.neodem.orleans.objects.PlaceTile.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class GameState {
    private String gameId;

    private int round = 1;
    private GamePhase gamePhase = GamePhase.HourGlass;

    private final List<PlayerState> players = new ArrayList<>();

    private final BoardState boardState;
    private final Map<GoodType, Integer> goodsInventory = new HashMap<>();

    private final Collection<PlaceTile> placeTiles1 = new HashSet<>();
    private final Collection<PlaceTile> placeTiles2 = new HashSet<>();

    private final List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
    private final List<HourGlassTile> usedHourGlassTiles = new ArrayList<>();

    public GameState(String gameId, BoardState boardState) {
        this.gameId = gameId;

        this.boardState = boardState;

        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);


        placeTiles1.addAll(Sets.newHashSet(Hayrick, WoolManufacturer, CheeseFactory, Winery, Brewery, Sacristy, HerbGarden, Bathhouse, Windmill, Library, Hospital, TailorShop));
        placeTiles2.addAll(Sets.newHashSet(GunpowderTower, Cellar, Office, School, Pharmacy, HorseWagon, ShippingLine, Laboratory));

        hourGlassTileStack.add(Pilgrimage);
        hourGlassTileStack.add(Pilgrimage);
        hourGlassTileStack.add(Plague);
        hourGlassTileStack.add(Plague);
        hourGlassTileStack.add(Plague);
        hourGlassTileStack.add(Taxes);
        hourGlassTileStack.add(Taxes);
        hourGlassTileStack.add(Taxes);
        hourGlassTileStack.add(TradingDay);
        hourGlassTileStack.add(TradingDay);
        hourGlassTileStack.add(TradingDay);
        hourGlassTileStack.add(Income);
        hourGlassTileStack.add(Income);
        hourGlassTileStack.add(Income);
        hourGlassTileStack.add(Harvest);
        hourGlassTileStack.add(Harvest);
        hourGlassTileStack.add(Harvest);
        Collections.shuffle(hourGlassTileStack);
        hourGlassTileStack.add(0, Pilgrimage);
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
