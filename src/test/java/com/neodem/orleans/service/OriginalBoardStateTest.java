package com.neodem.orleans.service;

import com.neodem.orleans.objects.BoardState;
import com.neodem.orleans.objects.GoodType;
import com.neodem.orleans.objects.OriginalBoardState;
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
public class OriginalBoardStateTest {
    private OriginalBoardState board;

    @BeforeEach
    void setUp() {
        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        board = new OriginalBoardState(goodsInventory);
    }

    @AfterEach
    void tearDown() {
        board = null;
    }

    @Test
    void initializeBoardShouldCreateProperPaths() {

        spotCheck(board, TokenLocation.Chartres, 4);
        spotCheck(board, TokenLocation.Etampes, 2);
        spotCheck(board, TokenLocation.LeMans, 4);
        spotCheck(board, TokenLocation.Chateaudun, 4);
        spotCheck(board, TokenLocation.Venedome, 3);
        spotCheck(board, TokenLocation.Orleans, 4, 2);
        spotCheck(board, TokenLocation.Montargis, 3);
        spotCheck(board, TokenLocation.Tours, 1, 4);
        spotCheck(board, TokenLocation.Blois, 2, 2);
        spotCheck(board, TokenLocation.Briare, 1, 2);
        spotCheck(board, TokenLocation.Chinon, 1, 3);
        spotCheck(board, TokenLocation.Montrichard, 1, 2);
        spotCheck(board, TokenLocation.Vierzon, 3, 3);
        spotCheck(board, TokenLocation.Loches, 2, 1);
        spotCheck(board, TokenLocation.Bourges, 3, 1);
        spotCheck(board, TokenLocation.Sancerre, 1, 2);
        spotCheck(board, TokenLocation.Chatelleraut, 2, 1);
        spotCheck(board, TokenLocation.LeBlanc, 1, 2);
        spotCheck(board, TokenLocation.Chateauroux, 5);
        spotCheck(board, TokenLocation.Nevers, 2, 1);
        spotCheck(board, TokenLocation.ArgentonSurCreuse, 3, 1);
        spotCheck(board, TokenLocation.LaChatre, 3);
        spotCheck(board, TokenLocation.SAmandMontrond, 3, 1);

        Collection<Path> allPaths = board.getAllPaths();
        assertThat(allPaths).hasSize(43);

        Collection<Path> waterPaths = allPaths.stream().filter(p -> p.getPathType() == PathType.Sea).collect(Collectors.toSet());
        assertThat(waterPaths).hasSize(14);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land) {
        spotCheck(boardState, location, land, 0);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land, int sea) {
        Collection<Path> paths = boardState.getSpecificPaths(location);
        Collection<Path> waterPaths = paths.stream().filter(p -> p.getPathType() == PathType.Sea).collect(Collectors.toSet());
        assertThat(waterPaths).hasSize(sea);
        Collection<Path> landPaths = paths.stream().filter(p -> p.getPathType() == PathType.Land).collect(Collectors.toSet());
        assertThat(landPaths).hasSize(land);
    }

}
