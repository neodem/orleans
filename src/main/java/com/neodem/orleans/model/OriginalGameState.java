package com.neodem.orleans.model;

import com.google.common.collect.Sets;

import java.util.Collections;

import static com.neodem.orleans.model.HourGlassTile.*;
import static com.neodem.orleans.model.PlaceTile.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class OriginalGameState extends GameState {
    public OriginalGameState(String gameId, int playerCount) {
        super(gameId, playerCount);
    }

    @Override
    public void initGame(int playerCount) {
        round = 1;
        gamePhase = GamePhase.HourGlass;

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

        initForPlayerCount(playerCount);
    }

    protected void initForPlayerCount(int playerCount) {
        if (playerCount == 2) initFor2Players();
        else if (playerCount == 3) initFor3Players();
        else initFor4Players();
    }

    private void initFor2Players() {
        // 2p removes 12 random goods
        removeRandomGoods(12);

        boardState = new OriginalBoardState(goodsInventory, 2);

        followerInventory.put(FollowerType.Farmer, 12);
        followerInventory.put(FollowerType.Boatmen, 6);
        followerInventory.put(FollowerType.Craftsman, 6);
        followerInventory.put(FollowerType.Trader, 6);
        followerInventory.put(FollowerType.Knight, 8);
        followerInventory.put(FollowerType.Scholar, 8);
        followerInventory.put(FollowerType.Monk, 8);
    }

    private void initFor3Players() {
        // 3p removes 6 random goods
        removeRandomGoods(6);

        boardState = new OriginalBoardState(goodsInventory, 3);

        followerInventory.put(FollowerType.Farmer, 14);
        followerInventory.put(FollowerType.Boatmen, 8);
        followerInventory.put(FollowerType.Craftsman, 8);
        followerInventory.put(FollowerType.Trader, 8);
        followerInventory.put(FollowerType.Knight, 11);
        followerInventory.put(FollowerType.Scholar, 11);
        followerInventory.put(FollowerType.Monk, 11);
    }

    private void initFor4Players() {

        boardState = new OriginalBoardState(goodsInventory, 4);

        followerInventory.put(FollowerType.Farmer, 16);
        followerInventory.put(FollowerType.Boatmen, 10);
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
}
