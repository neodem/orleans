package com.neodem.orleans.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class GameStateTest {
    private static final String PLAYER1 = "player1";
    private static final String PLAYER2 = "player2";
    private static final String PLAYER3 = "player3";
    private static final String PLAYER4 = "player4";

    private PlayerState p1;
    private PlayerState p2;
    private PlayerState p3;
    private PlayerState p4;
    private GameState gameState;

    private class TestableGameState extends GameState {

        public TestableGameState(String gameId, int playerCount) {
            super(gameId, playerCount);
        }

        @Override
        public void initGame(int playerCount) {
            //noop
        }
    }

    @BeforeEach
    void setUp() {
        gameState = new TestableGameState("abc", 3);
        p1 = new OriginalPlayerState(PLAYER1, PlayerColor.Blue);
        gameState.addPlayer(p1);
        p2 = new OriginalPlayerState(PLAYER2, PlayerColor.Blue);
        gameState.addPlayer(p2);
        p3 = new OriginalPlayerState(PLAYER3, PlayerColor.Blue);
        gameState.addPlayer(p3);
        p4 = new OriginalPlayerState(PLAYER4, PlayerColor.Blue);
        gameState.addPlayer(p4);
    }

    @AfterEach
    void tearDown() {
        gameState = null;
        p4 = null;
        p3 = null;
        p2 = null;
        p1 = null;
    }

    @Test
    void leastFarmersShouldReturnNullForAllOnZero() {
        setupFarmers(0, 0, 0, 0);
        String result = gameState.leastFarmers();
        assertThat(result).isNull();
    }

    @Test
    void leastFarmersShouldReturnNullForATieForLowest() {
        setupFarmers(0, 1, 1, 0);
        String result = gameState.leastFarmers();
        assertThat(result).isNull();
    }

    @Test
    void leastFarmersShouldReturnMin() {
        setupFarmers(2, 3, 2, 1);
        String result = gameState.leastFarmers();
        assertThat(result).isEqualTo(PLAYER4);
    }

    @Test
    void leastFarmersShouldReturnNullFor2PlayerGame() {
        gameState = new TestableGameState("abc", 3);
        p1 = new OriginalPlayerState(PLAYER1, PlayerColor.Blue);
        gameState.addPlayer(p1);
        p2 = new OriginalPlayerState(PLAYER2, PlayerColor.Blue);
        gameState.addPlayer(p2);
        String result = gameState.leastFarmers();
        assertThat(result).isNull();
    }

    @Test
    void maxFarmersShouldReturnNullForAllOnZero() {
        setupFarmers(0, 0, 0, 0);
        String result = gameState.mostFarmers();
        assertThat(result).isNull();
    }

    @Test
    void maxFarmersShouldReturnMaxAlways() {
        setupFarmers(1, 1, 3, 0);
        String result = gameState.mostFarmers();
        assertThat(result).isEqualTo(PLAYER3);
    }

    @Test
    void maxFarmersShouldReturnNullForATieForMax() {
        setupFarmers(0, 3, 3, 1);
        String result = gameState.mostFarmers();
        assertThat(result).isNull();
    }

    @Test
    void maxFarmersShouldReturnMax() {
        setupFarmers(0, 3, 2, 1);
        String result = gameState.mostFarmers();
        assertThat(result).isEqualTo(PLAYER2);
    }

    @Test
    void maxFarmersShouldReturnMaxAtEnd() {
        setupFarmers(0, 1, 2, 3);
        String result = gameState.mostFarmers();
        assertThat(result).isEqualTo(PLAYER4);
    }

    private void setupFarmers(int count1, int count2, int count3, int count4) {
        p1.getTracks().put(Track.Farmers, count1);
        p2.getTracks().put(Track.Farmers, count2);
        p3.getTracks().put(Track.Farmers, count3);
        p4.getTracks().put(Track.Farmers, count4);
    }
}
