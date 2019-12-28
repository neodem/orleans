package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class FarmHouseProcessor implements ActionProcessor {

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int farmerIndex = player.getTracks().get(Track.Farmers);
        return farmerIndex != 8;
    }

    @Override
    public void process(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int farmerIndex = player.getTrackValue(Track.Farmers);
        farmerIndex++;
        player.getTracks().put(Track.Farmers, farmerIndex);
    }
}
