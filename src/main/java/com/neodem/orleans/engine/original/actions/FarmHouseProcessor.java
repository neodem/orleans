package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class FarmHouseProcessor extends ActionProcessorBase {

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int farmerIndex = player.getTrackValue(Track.Farmers);

        switch (farmerIndex) {
            case 0:
            case 1:
                return gameState.isGoodAvailable(GoodType.Grain);
            case 2:
            case 3:
                return gameState.isGoodAvailable(GoodType.Cheese);
            case 4:
            case 5:
                return gameState.isGoodAvailable(GoodType.Wine);
            case 6:
                return gameState.isGoodAvailable(GoodType.Wool);
            case 7:
                return gameState.isGoodAvailable(GoodType.Brocade);
            case 8:
                return false;
        }

        return false;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.bumpTrack(Track.Farmers);
        switch (trackIndex) {
            case 1:
            case 2:
                gameState.removeGoodFromInventory(GoodType.Grain);
                player.addGood(GoodType.Grain);
                break;
            case 3:
            case 4:
                gameState.removeGoodFromInventory(GoodType.Cheese);
                player.addGood(GoodType.Cheese);
                break;
            case 5:
            case 6:
                gameState.removeGoodFromInventory(GoodType.Wine);
                player.addGood(GoodType.Wine);
                break;
            case 7:
                gameState.removeGoodFromInventory(GoodType.Wool);
                player.addGood(GoodType.Wool);
                break;
            case 8:
                gameState.removeGoodFromInventory(GoodType.Brocade);
                player.addGood(GoodType.Brocade);
                break;

        }
    }
}
