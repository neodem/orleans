package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class HospitalProcessor implements ActionProcessor {

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        return true;
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
        int index = player.getTrackValue(Track.Development);
    }
}