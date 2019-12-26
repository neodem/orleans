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
import static com.neodem.orleans.objects.PathType.Sea;
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

        addPath(boardState, Etampes, Montargis, Land, goodsInventory);

        addPath(boardState, LeMans, Chateaudun, Land, goodsInventory);
        addPath(boardState, LeMans, Venedome, Land, goodsInventory);
        addPath(boardState, LeMans, Tours, Land, goodsInventory);

        addPath(boardState, Chateaudun, Venedome, Land, goodsInventory);
        addPath(boardState, Chateaudun, Orleans, Land, goodsInventory);

        addPath(boardState, Orleans, Montargis, Land, goodsInventory);
        addPath(boardState, Orleans, Vierzon, Land, goodsInventory);
        addPath(boardState, Orleans, Briare, Sea, goodsInventory);
        addPath(boardState, Orleans, Blois, Sea, goodsInventory);

        addPath(boardState, Venedome, Blois, Land, goodsInventory);

        addPath(boardState, Montargis, Briare, Land, goodsInventory);

        addPath(boardState, Briare, Sancerre, Sea, goodsInventory);

        addPath(boardState, Blois, Tours, Sea, goodsInventory);
        addPath(boardState, Blois, Vierzon, Land, goodsInventory);

        addPath(boardState, Tours, Montrichard, Sea, goodsInventory);
        addPath(boardState, Tours, Loches, Sea, goodsInventory);
        addPath(boardState, Tours, Chinon, Sea, goodsInventory);

        addPath(boardState, Chinon, Chatelleraut, Sea, goodsInventory);
        addPath(boardState, Chinon, LeBlanc, Sea, goodsInventory);
        addPath(boardState, Chinon, Chatelleraut, Land, goodsInventory);

        addPath(boardState, Montrichard, Vierzon, Sea, goodsInventory);
        addPath(boardState, Montrichard, Loches, Land, goodsInventory);

        addPath(boardState, Vierzon, Chateauroux, Land, goodsInventory);
        addPath(boardState, Vierzon, Bourges, Sea, goodsInventory);
        addPath(boardState, Vierzon, SAmandMontrond, Sea, goodsInventory);

        addPath(boardState, Sancerre, Bourges, Land, goodsInventory);
        addPath(boardState, Sancerre, Nevers, Sea, goodsInventory);

        addPath(boardState, Loches, Chateauroux, Land, goodsInventory);

        addPath(boardState, Chatelleraut, ArgentonSurCreuse, Land, goodsInventory);

        addPath(boardState, LeBlanc, ArgentonSurCreuse, Sea, goodsInventory);
        addPath(boardState, LeBlanc, Chinon, Sea, goodsInventory);
        addPath(boardState, LeBlanc, Chateauroux, Land, goodsInventory);

        addPath(boardState, ArgentonSurCreuse, LaChatre, Land, goodsInventory);

        addPath(boardState, LaChatre, SAmandMontrond, Land, goodsInventory);

        addPath(boardState, Bourges, SAmandMontrond, Land, goodsInventory);
        addPath(boardState, Bourges, Nevers, Land, goodsInventory);

        addPath(boardState, Chateauroux, Vierzon, Land, goodsInventory);
        addPath(boardState, Chateauroux, Loches, Land, goodsInventory);
        addPath(boardState, Chateauroux, LeBlanc, Land, goodsInventory);
        addPath(boardState, Chateauroux, ArgentonSurCreuse, Land, goodsInventory);
        addPath(boardState, Chateauroux, LaChatre, Land, goodsInventory);

        addPath(boardState, SAmandMontrond, Nevers, Land, goodsInventory);

        return boardState;
    }

    private void addPath(BoardState boardState, TokenLocation from, TokenLocation to, PathType pathType, Map<GoodType, Integer> goodsInventory) {
        if(!boardState.doesPathExist(from, to, pathType)) {
            GoodType goodType = getRandomGoodFromInventory(goodsInventory);
            if (goodType != null) {
                Path path = new Path(from, to, pathType);
                path.addGood(goodType);
                boardState.addPath(path);
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
