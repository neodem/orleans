package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class GuildHallProcessor extends ActionProcessorBase {

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        TokenLocation merchantLocation = player.getMerchantLocation();

        if(merchantLocation != TokenLocation.Orleans) {
            Collection<String> names = gameState.getTradingStationOwners(merchantLocation);
            return names.isEmpty();

        }
        return true;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        player.addTradingHallToCurrentLocation();
    }
}
