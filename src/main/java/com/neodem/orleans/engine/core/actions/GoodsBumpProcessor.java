package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class GoodsBumpProcessor implements ActionProcessor {

    private final GoodType type;

    public GoodsBumpProcessor(GoodType type) {
        this.type = type;
    }

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        return gameState.getGoodsInventory().get(type) > 0;
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
       int amount = gameState.getGoodsInventory().get(type);
       gameState.getGoodsInventory().put(type, --amount);
       player.addGood(type);
    }
}
