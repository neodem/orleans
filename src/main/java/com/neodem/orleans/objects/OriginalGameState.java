package com.neodem.orleans.objects;

import com.google.common.collect.Sets;

import java.util.Collections;

import static com.neodem.orleans.objects.HourGlassTile.*;
import static com.neodem.orleans.objects.PlaceTile.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class OriginalGameState extends GameState {
    public OriginalGameState(String gameId) {
        super(gameId);
    }

    @Override
    public void initGame() {
        round = 1;
        gamePhase = GamePhase.HourGlass;

        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        boardState = new OriginalBoardState(goodsInventory);

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
}
