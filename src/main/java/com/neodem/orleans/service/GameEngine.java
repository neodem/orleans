package com.neodem.orleans.service;

import com.neodem.orleans.objects.BoardState;
import com.neodem.orleans.objects.BoardType;
import com.neodem.orleans.objects.GameState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public interface GameEngine {

    GameState initializeGame(String gameId);

    /**
     * will set up the board paths for the board
     * and init tiles from the game inventory
     * @return
     */
    BoardState initializeBoard(BoardType boardType, GameState gameState);
}
