package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public interface ActionProcessor {
    void process(GameState gameState, PlayerState player);
}
