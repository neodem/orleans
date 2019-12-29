package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.HourGlassTile;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class MonasteryProcessor extends ActionProcessorBase {
    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        return gameState.getFollowerInventory().get(FollowerType.Monk) > 0 && gameState.getCurrentHourGlass() != HourGlassTile.Pilgrimage;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        gameState.removeFollowerFromInventory(FollowerType.Monk);
        player.addToBag(Follower.makeMonk());
    }
}
