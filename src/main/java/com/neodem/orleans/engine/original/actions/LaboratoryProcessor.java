package com.neodem.orleans.engine.original.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.TechTileHelper;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class LaboratoryProcessor extends ActionProcessorBase {

    private final ActionHelper actionHelper;

    public LaboratoryProcessor(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        validateMap(additionalDataMap, Sets.newHashSet(AdditionalDataType.techAction, AdditionalDataType.position));

        if (gameState.getNumberTechTilesAvailable() == 0) {
            throw new ActionProcessorException("There are no more tech tiles available");
        }

        ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
        int position = Util.getIntegerFromMap(additionalDataMap, AdditionalDataType.position);
        FollowerType followerType = actionHelper.getTypeForAction(actionType, position);

        if (followerType == FollowerType.Monk)
            throw new ActionProcessorException("You may never replace a Monk with a tech tile");

        return true;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
        int position = Util.getIntegerFromMap(additionalDataMap, AdditionalDataType.position);
        TechTileHelper.addTechTileToPlayer(gameState, player, position, actionType, actionHelper);
    }
}
