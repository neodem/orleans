package com.neodem.orleans.engine.original.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.BenefitTracker;
import com.neodem.orleans.engine.core.model.BoardState;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GamePhase;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.OriginalBenefitTracker;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import static com.neodem.orleans.engine.core.model.HourGlassTile.*;
import static com.neodem.orleans.engine.original.model.PlaceTile.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class OriginalGameState extends GameState {
    public OriginalGameState(JsonNode jsonNode) {
        super(jsonNode);
    }

    @Override
    protected PlayerState makePlayerFromJson(JsonNode json) {
        return new OriginalPlayerState(json);
    }

    @Override
    protected BenefitTracker makeBenefitTrackerFromJson(JsonNode json) {
        return new OriginalBenefitTracker(json);
    }

    @Override
    protected BoardState makeBoardStateFromJson(JsonNode json) {
        return new OriginalBoardState(json);
    }

    public OriginalGameState(String gameId, int playerCount) {
        super(gameId, playerCount, "Original");
    }

    @Override
    public void initGame(int playerCount) {
        round = 0;
        gamePhase = GamePhase.Setup;

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

        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        currentHourGlass = null;
        startPlayer = playerCount - 1;

        techTilesAvailable = 16;

        benefitTracker = new OriginalBenefitTracker();
    }

    @Override
    protected void initFor2Players() {
        // 2p removes 12 random goods
        removeRandomGoods(12);

        boardState = new OriginalBoardState(goodsInventory, 2);

        followerInventory.put(FollowerType.Farmer, 12);
        followerInventory.put(FollowerType.Boatman, 6);
        followerInventory.put(FollowerType.Craftsman, 6);
        followerInventory.put(FollowerType.Trader, 6);
        followerInventory.put(FollowerType.Knight, 8);
        followerInventory.put(FollowerType.Scholar, 8);
        followerInventory.put(FollowerType.Monk, 8);
    }

    @Override
    protected void initFor3Players() {
        // 3p removes 6 random goods
        removeRandomGoods(6);

        boardState = new OriginalBoardState(goodsInventory, 3);

        followerInventory.put(FollowerType.Farmer, 14);
        followerInventory.put(FollowerType.Boatman, 8);
        followerInventory.put(FollowerType.Craftsman, 8);
        followerInventory.put(FollowerType.Trader, 8);
        followerInventory.put(FollowerType.Knight, 11);
        followerInventory.put(FollowerType.Scholar, 11);
        followerInventory.put(FollowerType.Monk, 11);
    }

    @Override
    protected void initFor4Players() {

        boardState = new OriginalBoardState(goodsInventory, 4);

        followerInventory.put(FollowerType.Farmer, 16);
        followerInventory.put(FollowerType.Boatman, 10);
        followerInventory.put(FollowerType.Craftsman, 10);
        followerInventory.put(FollowerType.Trader, 10);
        followerInventory.put(FollowerType.Knight, 14);
        followerInventory.put(FollowerType.Scholar, 14);
        followerInventory.put(FollowerType.Monk, 14);
    }

    protected void removeRandomGoods(int goodsToRemove) {
        for (int i = 0; i < goodsToRemove; i++) {
            Integer goodCount;
            GoodType goodType;
            do {
                goodType = GoodType.randomGood();
                goodCount = goodsInventory.get(goodType);
            } while (goodCount == 0);
            goodsInventory.put(goodType, --goodCount);
        }
    }

    public String getPlayerWithMostTradingStations() {
        Collection<String> leaders = new HashSet<>();
        int max = 1;
        for (PlayerState player : players) {
            int tsCount = player.getTradingStationLocations().size();
            if (tsCount >= max) {
                max = tsCount;
                leaders.add(player.getPlayerId());
            }
        }

        if (leaders.size() == 1) return leaders.iterator().next();
        return null;
    }

    private String playerHasBathhouse = null;

    public String getPlayerHasBathhouse() {
        return playerHasBathhouse;
    }

    public void setPlayerHasBathhouse(String playerHasBathhouse) {
        this.playerHasBathhouse = playerHasBathhouse;
    }
}
