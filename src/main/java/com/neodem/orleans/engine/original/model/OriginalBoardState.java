package com.neodem.orleans.engine.original.model;

import com.neodem.orleans.engine.core.model.BoardState;
import com.neodem.orleans.engine.core.model.GoodType;

import java.util.Map;

import static com.neodem.orleans.engine.core.model.PathType.Land;
import static com.neodem.orleans.engine.core.model.PathType.Sea;
import static com.neodem.orleans.engine.core.model.TokenLocation.*;

/**
 * BoardState for the original game
 * <p>
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalBoardState extends BoardState {
    public OriginalBoardState(Map<GoodType, Integer> goodsInventory, int playerCount) {
        super(goodsInventory, playerCount);
    }

    @Override
    protected void init(Map<GoodType, Integer> goodsInventory, int playerCount) {
        if (playerCount == 4) {
            addPath(Chartres, LeMans, Land, goodsInventory, 2);
            addPath(Etampes, Montargis, Land, goodsInventory, 2);
            addPath(Chatelleraut, ArgentonSurCreuse, Land, goodsInventory, 2);
            addPath(SAmandMontrond, Nevers, Land, goodsInventory, 2);
        } else {
            addPath(Chartres, LeMans, Land, goodsInventory);
            addPath(Etampes, Montargis, Land, goodsInventory);
            addPath(Chatelleraut, ArgentonSurCreuse, Land, goodsInventory);
            addPath(SAmandMontrond, Nevers, Land, goodsInventory);
        }

        if (playerCount == 3) {
            addPath(LeMans, Tours, Land, goodsInventory, 2);
            addPath(Orleans, Briare, Sea, goodsInventory,2);
            addPath(Briare, Sancerre, Sea, goodsInventory, 2);
            addPath(Orleans, Vierzon, Land, goodsInventory, 2);
            addPath(Chinon, LeBlanc, Sea, goodsInventory, 2);
        } else {
            addPath(LeMans, Tours, Land, goodsInventory);
            addPath(Orleans, Briare, Sea, goodsInventory);
            addPath(Briare, Sancerre, Sea, goodsInventory);
            addPath(Orleans, Vierzon, Land, goodsInventory);
            addPath(Chinon, LeBlanc, Sea, goodsInventory);
        }

        // set up paths and add one good per path (others may be added after init)
        addPath(Chartres, Etampes, Land, goodsInventory);
        addPath(Chartres, Orleans, Land, goodsInventory);
        addPath(Chartres, Chateaudun, Land, goodsInventory);
        addPath(LeMans, Chateaudun, Land, goodsInventory);
        addPath(LeMans, Venedome, Land, goodsInventory);
        addPath(Chateaudun, Venedome, Land, goodsInventory);
        addPath(Chateaudun, Orleans, Land, goodsInventory);
        addPath(Orleans, Blois, Sea, goodsInventory);
        addPath(Orleans, Montargis, Land, goodsInventory);
        addPath(Venedome, Blois, Land, goodsInventory);
        addPath(Montargis, Briare, Land, goodsInventory);
        addPath(Blois, Tours, Sea, goodsInventory);
        addPath(Blois, Vierzon, Land, goodsInventory);
        addPath(Tours, Montrichard, Sea, goodsInventory);
        addPath(Tours, Loches, Sea, goodsInventory);
        addPath(Tours, Chinon, Sea, goodsInventory);
        addPath(Chinon, Chatelleraut, Sea, goodsInventory);
        addPath(Chinon, Chatelleraut, Land, goodsInventory);
        addPath(Montrichard, Vierzon, Sea, goodsInventory);
        addPath(Montrichard, Loches, Land, goodsInventory);
        addPath(Vierzon, Chateauroux, Land, goodsInventory);
        addPath(Vierzon, Bourges, Sea, goodsInventory);
        addPath(Vierzon, SAmandMontrond, Sea, goodsInventory);
        addPath(Sancerre, Bourges, Land, goodsInventory);
        addPath(Sancerre, Nevers, Sea, goodsInventory);
        addPath(Loches, Chateauroux, Land, goodsInventory);
        addPath(LeBlanc, ArgentonSurCreuse, Sea, goodsInventory);
        addPath(LeBlanc, Chinon, Sea, goodsInventory);
        addPath(LeBlanc, Chateauroux, Land, goodsInventory);
        addPath(ArgentonSurCreuse, LaChatre, Land, goodsInventory);
        addPath(LaChatre, SAmandMontrond, Land, goodsInventory);
        addPath(Bourges, SAmandMontrond, Land, goodsInventory);
        addPath(Bourges, Nevers, Land, goodsInventory);
        addPath(Chateauroux, Vierzon, Land, goodsInventory);
        addPath(Chateauroux, Loches, Land, goodsInventory);
        addPath(Chateauroux, LeBlanc, Land, goodsInventory);
        addPath(Chateauroux, ArgentonSurCreuse, Land, goodsInventory);
        addPath(Chateauroux, LaChatre, Land, goodsInventory);
    }



}
