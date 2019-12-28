package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.CitizenType;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class CoinBumpProcessor implements ActionProcessor {

    private final int bumpSize;

    public CoinBumpProcessor(int bumpSize) {
        this.bumpSize = bumpSize;
    }

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        return true;
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
        player.addCoin(bumpSize);
    }
}
