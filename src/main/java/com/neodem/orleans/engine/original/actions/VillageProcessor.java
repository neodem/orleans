package com.neodem.orleans.engine.original.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class VillageProcessor extends ActionProcessorBase {

    @Override
    protected Collection<AdditionalDataType> requiredTypes() {
        return Sets.newHashSet(AdditionalDataType.follower);
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        Follower desiredFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower);
        return gameState.getFollowerInventory().get(desiredFollower) > 0;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        Follower desiredFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower);
        gameState.removeFollowerFromInventory(desiredFollower);
        player.addToBag(desiredFollower);
    }

}
