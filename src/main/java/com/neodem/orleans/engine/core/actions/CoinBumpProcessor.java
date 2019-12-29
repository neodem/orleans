package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class CoinBumpProcessor extends ActionProcessorBase {

    private final int bumpSize;

    public CoinBumpProcessor(int bumpSize) {
        this.bumpSize = bumpSize;
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        return true;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        player.addCoin(bumpSize);
    }

}
