package com.neodem.orleans.service;

import com.neodem.orleans.engine.core.model.GameState;

/**
 * This service exists so that we can (if needed) fire up multiple game hosts and then be able to get the gameStates in a
 * way that is concurrent safe.
 * <p>
 * It works by leasing the gameState objects
 * <p>
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/12/20
 */
public interface GameStateService<G extends GameState> {

    /**
     * get the GameState exclusively for some period of time. This lease will expire if the gameState is saved first
     *
     * @param gameId
     * @return the item or throw exception
     * @throws IllegalStateException if lease is held on an item
     */
    G leaseGameState(String gameId);

    /**
     * will lease and return the gameState or wait until it's available and _then_ lease it
     *
     * @param gameId
     * @return
     */
    G waitAndLeaseGameState(String gameId);

    /**
     * cancel a lease on a gameState
     *
     * @param gameId
     */
    void cancelLease(String gameId);

    /**
     * return true if state is saved in the service
     *
     * @param gameId
     * @return
     */
    boolean gameStateExists(String gameId);

    /**
     * save the gameState and expire any leases.
     *
     * @param gameState
     */
    void saveGameState(G gameState);


}
