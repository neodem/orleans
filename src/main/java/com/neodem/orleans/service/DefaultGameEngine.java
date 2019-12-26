package com.neodem.orleans.service;

import com.google.common.collect.Sets;
import com.neodem.orleans.objects.BoardState;
import com.neodem.orleans.objects.BoardType;
import com.neodem.orleans.objects.GamePhase;
import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.GoodType;
import com.neodem.orleans.objects.HourGlassTile;
import com.neodem.orleans.objects.Path;
import com.neodem.orleans.objects.PathType;
import com.neodem.orleans.objects.TokenLocation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.objects.HourGlassTile.*;
import static com.neodem.orleans.objects.PathType.Land;
import static com.neodem.orleans.objects.PlaceTile.*;
import static com.neodem.orleans.objects.TokenLocation.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class DefaultGameEngine implements GameEngine {

    @Override
    public GameState initializeGame(String gameId) {
        GameState gameState = new GameState(gameId);

        gameState.setRound(0);
        gameState.setGamePhase(GamePhase.Setup);

        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);
        gameState.setGoodsInventory(goodsInventory);

        BoardState boardState = initializeBoard(BoardType.Standard, gameState);
        gameState.setBoardState(boardState);

        gameState.setPlaceTiles1(Sets.newHashSet(Hayrick, WoolManufacturer, CheeseFactory, Winery, Brewery, Sacristy, HerbGarden, Bathhouse, Windmill, Library, Hospital, TailorShop));
        gameState.setPlaceTiles2(Sets.newHashSet(GunpowderTower, Cellar, Office, School, Pharmacy, HorseWagon, ShippingLine, Laboratory));

        List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
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
        hourGlassTileStack.add(Pilgrimage);
        gameState.setHourGlassStack(hourGlassTileStack);
        gameState.setUsedHourGlassTiles(new ArrayList<>());

        return gameState;
    }

    @Override
    public BoardState initializeBoard(BoardType boardType, GameState gameState) {
        BoardState boardState = new BoardState();

        Map<GoodType, Integer> goodsInventory = gameState.getGoodsInventory();

        // set up paths and add one good per path (others may be added after init)
        addPath(boardState, Chartres, Etampes, Land, goodsInventory);
        addPath(boardState, Chartres, Orleans, Land, goodsInventory);
        addPath(boardState, Chartres, Chateaudun, Land, goodsInventory);
        addPath(boardState, Chartres, LeMans, Land, goodsInventory);

        addPath(boardState, Etampes, Chartres, Land, goodsInventory);
        addPath(boardState, Etampes, Montargis, Land, goodsInventory);

        return boardState;
    }

    private void addPath(BoardState boardState, TokenLocation from, TokenLocation to, PathType pathType, Map<GoodType, Integer> goodsInventory) {
        if(!boardState.doesPathExist(from, to, pathType)) {
            GoodType goodType = getRandomGoodFromInventory(goodsInventory);
            if (goodType != null) {
                Path path = new Path(from, to, pathType);
                path.addGood(goodType);
                boardState.addPath(from, path);
            } else {
                throw new RuntimeException("trying to init path with no goods available");
            }
        }
    }

    private GoodType getRandomGoodFromInventory(Map<GoodType, Integer> goodsInventory) {
        GoodType result = null;

        if(goodsAvailable(goodsInventory)) {
            do {
                GoodType candidate = GoodType.randomGood();
                int amountAvailable = goodsInventory.get(candidate);
                if (amountAvailable > 0) {
                    goodsInventory.put(candidate, amountAvailable - 1);
                    result = candidate;
                }
            } while (result == null);
        }

        return result;
    }

    private boolean goodsAvailable(Map<GoodType, Integer> goodsInventory) {
        for(GoodType goodType : GoodType.values()) {
            if(goodsInventory.get(goodType) > 0) {
                return true;
            }
        }
        return false;
    }
}
