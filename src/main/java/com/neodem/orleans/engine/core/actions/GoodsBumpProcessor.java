package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class GoodsBumpProcessor extends ActionProcessorBase {

    private final GoodType type;

    public GoodsBumpProcessor(GoodType type) {
        this.type = type;
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        return gameState.getGoodsInventory().get(type) > 0;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int amount = gameState.getGoodsInventory().get(type);
        gameState.getGoodsInventory().put(type, --amount);
        player.addGood(type);
    }
}
