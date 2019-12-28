package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.actions.DevelopmentBumpProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class WindmillProcessor extends DevelopmentBumpProcessor implements ActionProcessor {

    public WindmillProcessor() {
        super(1);
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
        super.process(gameState, player);
        player.addCoin(2);
    }
}
