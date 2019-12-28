package com.neodem.orleans.engine.original.model;

import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GamePhase;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;

import java.util.Collections;

import static com.neodem.orleans.engine.core.model.HourGlassTile.*;
import static com.neodem.orleans.engine.core.model.PlaceTile.*;

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

        initForPlayerCount(playerCount);

        writeLine("Original game is set up. Welcome!");
    }

    @Override
    protected void initFor2Players() {
        // 2p removes 12 random goods
        removeRandomGoods(12);

        boardState = new OriginalBoardState(goodsInventory, 2);

        followerInventory.put(Follower.Farmer, 12);
        followerInventory.put(Follower.Boatman, 6);
        followerInventory.put(Follower.Craftsman, 6);
        followerInventory.put(Follower.Trader, 6);
        followerInventory.put(Follower.Knight, 8);
        followerInventory.put(Follower.Scholar, 8);
        followerInventory.put(Follower.Monk, 8);
    }

    @Override
    protected void initFor3Players() {
        // 3p removes 6 random goods
        removeRandomGoods(6);

        boardState = new OriginalBoardState(goodsInventory, 3);

        followerInventory.put(Follower.Farmer, 14);
        followerInventory.put(Follower.Boatman, 8);
        followerInventory.put(Follower.Craftsman, 8);
        followerInventory.put(Follower.Trader, 8);
        followerInventory.put(Follower.Knight, 11);
        followerInventory.put(Follower.Scholar, 11);
        followerInventory.put(Follower.Monk, 11);
    }

    @Override
    protected void initFor4Players() {

        boardState = new OriginalBoardState(goodsInventory, 4);

        followerInventory.put(Follower.Farmer, 16);
        followerInventory.put(Follower.Boatman, 10);
        followerInventory.put(Follower.Craftsman, 10);
        followerInventory.put(Follower.Trader, 10);
        followerInventory.put(Follower.Knight, 14);
        followerInventory.put(Follower.Scholar, 14);
        followerInventory.put(Follower.Monk, 14);
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
