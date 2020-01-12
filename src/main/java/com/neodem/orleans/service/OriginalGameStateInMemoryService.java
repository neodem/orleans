package com.neodem.orleans.service;

import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.original.model.OriginalGameState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/12/20
 */
public class OriginalGameStateInMemoryService extends InMemoryGameStateService {

    @Override
    public OriginalGameState leaseGameState(String gameId) {
        GameState gameState = super.leaseGameState(gameId);
        return (OriginalGameState) gameState;
    }

    @Override
    public OriginalGameState waitAndLeaseGameState(String gameId) {
        GameState gameState = super.waitAndLeaseGameState(gameId);
        return (OriginalGameState) gameState;
    }
}
