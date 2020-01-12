package com.neodem.orleans.service;

import com.neodem.orleans.engine.core.model.GameState;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/12/20
 */
public abstract class InMemoryGameStateService implements GameStateService {

    private static int DEFAULT_LEASE_TIME_SECS = 5;
    private final long leaseTimeMs;

    private Map<String, GameState> data = new HashMap<>();
    private Map<String, Long> leases = new HashMap<>();

    public InMemoryGameStateService() {
        this.leaseTimeMs = DEFAULT_LEASE_TIME_SECS * 1000L;
    }

    public InMemoryGameStateService(long leaseTimeSec) {
        this.leaseTimeMs = leaseTimeSec * 1000L;
    }

    public GameState leaseGameState(String gameId) {
        Assert.hasText(gameId, "gameId may not be blank");
        Long lease = leases.get(gameId);
        if (lease == null) {
            leases.put(gameId, System.currentTimeMillis());
            return data.get(gameId);
        } else {
            long age = System.currentTimeMillis() - lease;
            if (age >= leaseTimeMs) {
                leases.remove(gameId);
                return data.get(gameId);
            }
        }
        throw new IllegalStateException("lease is held for this item");
    }

    public GameState waitAndLeaseGameState(String gameId) {
        Assert.hasText(gameId, "gameId may not be blank");
        GameState gameState = null;
        boolean waiting = true;
        do {
            try {
                gameState = leaseGameState(gameId);
                waiting = false;
            } catch (IllegalStateException e) {

                // noop
            }
        } while (waiting);

        return gameState;
    }

    @Override
    public void cancelLease(String gameId) {
        leases.remove(gameId);
    }

    @Override
    public void saveGameState(GameState gameState) {
        Assert.notNull(gameState, "gameState may not be null");
        Assert.hasText(gameState.getGameId(), "gameId may not be blank");
        data.put(gameState.getGameId(), gameState);
        leases.remove(gameState.getGameId());
    }

    @Override
    public boolean gameStateExists(String gameId) {
        return data.containsKey(gameId);
    }
}
