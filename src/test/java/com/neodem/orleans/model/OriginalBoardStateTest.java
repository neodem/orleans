package com.neodem.orleans.model;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalBoardStateTest {

    @Test
    void initShouldCreateProperPathsFor2() {
        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        OriginalBoardState board = new OriginalBoardState(goodsInventory, 2);

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

        Collection<Path> waterPaths = Path.getPathsOfType(allPaths, PathType.Sea);
        assertThat(waterPaths).hasSize(14);
    }

    @Test
    void initShouldCreateProperPathsFor3() {
        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        OriginalBoardState board = new OriginalBoardState(goodsInventory, 3);

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

        Collection<Path> waterPaths = Path.getPathsOfType(allPaths, PathType.Sea);
        assertThat(waterPaths).hasSize(14);

        checkTwoGoods(board, TokenLocation.LeMans, TokenLocation.Tours, PathType.Land);
        checkTwoGoods(board, TokenLocation.Orleans, TokenLocation.Briare, PathType.Sea);
        checkTwoGoods(board, TokenLocation.Orleans, TokenLocation.Vierzon, PathType.Land);
        checkTwoGoods(board, TokenLocation.Sancerre, TokenLocation.Briare, PathType.Sea);
        checkTwoGoods(board, TokenLocation.Chinon, TokenLocation.LeBlanc, PathType.Sea);
    }

    @Test
    void initShouldCreateProperPathsFor4() {
        Map<GoodType, Integer> goodsInventory = new HashMap<>();
        goodsInventory.put(GoodType.Grain, 24);
        goodsInventory.put(GoodType.Cheese, 21);
        goodsInventory.put(GoodType.Wine, 18);
        goodsInventory.put(GoodType.Wool, 15);
        goodsInventory.put(GoodType.Brocade, 12);

        OriginalBoardState board = new OriginalBoardState(goodsInventory, 4);

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

        Collection<Path> waterPaths = Path.getPathsOfType(allPaths, PathType.Sea);
        assertThat(waterPaths).hasSize(14);

        checkTwoGoods(board, TokenLocation.Chatelleraut, TokenLocation.ArgentonSurCreuse, PathType.Land);
        checkTwoGoods(board, TokenLocation.Nevers, TokenLocation.SAmandMontrond, PathType.Land);
        checkTwoGoods(board, TokenLocation.LeMans, TokenLocation.Chartres, PathType.Land);
        checkTwoGoods(board, TokenLocation.Etampes, TokenLocation.Montargis, PathType.Land);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land) {
        spotCheck(boardState, location, land, 0);
    }

    private void spotCheck(BoardState boardState, TokenLocation location, int land, int sea) {
        Collection<Path> paths = boardState.getPathsFromTown().get(location);
        Collection<Path> waterPaths = Path.getPathsOfType(paths, PathType.Sea);
        assertThat(waterPaths).hasSize(sea);
        Collection<Path> landPaths = Path.getPathsOfType(paths, PathType.Land);
        assertThat(landPaths).hasSize(land);
    }

    private void checkTwoGoods(BoardState boardState, TokenLocation from, TokenLocation to, PathType pathType) {
        Path path = boardState.getPathBetween(new PathBetween(from, to), pathType);
        System.out.println(path);
        assertThat(path).isNotNull();
        assertThat(path.getGoods()).hasSize(2);
    }
}
