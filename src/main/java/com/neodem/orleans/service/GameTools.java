package com.neodem.orleans.service;

import com.neodem.orleans.objects.BoardState;
import com.neodem.orleans.objects.BoardType;
import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public interface GameTools {

    /**
     * init a game with 0 players
     *
     * @param gameId
     * @return
     */
    GameState initializeGame(String gameId);

    PlayerState initializePlayer(String playerName);

    /**
     * will set up the board paths for the board
     * and init tiles from the game inventory
     * @return
     */
    BoardState initializeBoard(BoardType boardType, GameState gameState);
}
