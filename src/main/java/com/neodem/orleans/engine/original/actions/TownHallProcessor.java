package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class TownHallProcessor extends ActionProcessorBase {

    private final ActionType actionType;

    public TownHallProcessor(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        return false;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        Map<ActionType, List<Follower>> plans = player.getPlans();
        List<Follower> followers = plans.get(actionType);

    }
}
