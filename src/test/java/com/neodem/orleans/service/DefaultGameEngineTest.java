package com.neodem.orleans.service;

import com.neodem.orleans.objects.GameState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
