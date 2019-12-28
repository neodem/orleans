package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class LaboratoryProcessor implements ActionProcessor {

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        return false;
    }
    @Override
    public void process(GameState gameState, PlayerState player) {

    }
}
