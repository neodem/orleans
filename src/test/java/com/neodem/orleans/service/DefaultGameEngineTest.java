package com.neodem.orleans.service;

import com.neodem.orleans.objects.BoardState;
import com.neodem.orleans.objects.BoardType;
import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.GoodType;
import com.neodem.orleans.objects.Path;
import com.neodem.orleans.objects.PathType;
import com.neodem.orleans.objects.TokenLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class DefaultGameEngineTest {
    private DefaultGameEngine defaultGameEngine;

    @BeforeEach
    void setUp() {
        defaultGameEngine = new DefaultGameEngine();
    }

    @AfterEach
    void tearDown() {
        defaultGameEngine = null;
    }

    @Test
    void initializeGameShouldInitBoard() {
        GameState gameState = defaultGameEngine.initializeGame("gameId");
    }

    @Test
    void initializeBoardShouldCreateProperPaths() {
        GameState gameState = new GameState("gameid");

        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);
        gameState.setGoodsInventory(goodsInventory);

        BoardState boardState = defaultGameEngine.initializeBoard(BoardType.Standard, gameState);
        spotCheck(boardState, TokenLocation.Chartres, 4);
        spotCheck(boardState, TokenLocation.Etampes, 2);
        spotCheck(boardState, TokenLocation.LeMans, 4);
        spotCheck(boardState, TokenLocation.Chateaudun, 4);
        spotCheck(boardState, TokenLocation.Venedome, 3);
        spotCheck(boardState, TokenLocation.Orleans, 4,2);
        spotCheck(boardState, TokenLocation.Montargis, 3);
        spotCheck(boardState, TokenLocation.Tours, 1,4);
        spotCheck(boardState, TokenLocation.Blois, 2,2);
        spotCheck(boardState, TokenLocation.Briare, 1,2);
        spotCheck(boardState, TokenLocation.Chinon, 1,3);
        spotCheck(boardState, TokenLocation.Montrichard, 1,2);
        spotCheck(boardState, TokenLocation.Vierzon, 3,3);
        spotCheck(boardState, TokenLocation.Loches, 2,1);
        spotCheck(boardState, TokenLocation.Bourges, 3,1);
        spotCheck(boardState, TokenLocation.Sancerre, 1,2);
        spotCheck(boardState, TokenLocation.Chatelleraut, 2,1);
        spotCheck(boardState, TokenLocation.LeBlanc, 1,2);
        spotCheck(boardState, TokenLocation.Chateauroux, 5);
        spotCheck(boardState, TokenLocation.Nevers, 2,1);
        spotCheck(boardState, TokenLocation.ArgentonSurCreuse, 3,1);
        spotCheck(boardState, TokenLocation.LaChatre, 3);
        spotCheck(boardState, TokenLocation.SAmandMontrond, 3,1);

        Collection<Path> allPaths = boardState.getAllPaths();
        assertThat(allPaths).hasSize(43);

        Collection<Path> waterPaths = allPaths.stream().filter(p->p.getPathType()== PathType.Sea).collect(Collectors.toSet());
        assertThat(waterPaths).hasSize(14);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land) {
        spotCheck(boardState, location, land, 0);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land, int sea) {
        Collection<Path> paths = boardState.getSpecificPaths(location);
        Collection<Path> waterPaths = paths.stream().filter(p->p.getPathType()== PathType.Sea).collect(Collectors.toSet());
        assertThat(waterPaths).hasSize(sea);
        Collection<Path> landPaths = paths.stream().filter(p->p.getPathType()== PathType.Land).collect(Collectors.toSet());
        assertThat(landPaths).hasSize(land);
    }
}