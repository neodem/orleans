package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public abstract class ActionProcessorBase implements ActionProcessor {

    @Override
    public final boolean isValid(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) throws ActionProcessorException {
        validateInputs(gameState, player, additionalDataMap);
        return doIsAllowed(gameState, player, additionalDataMap);
    }

    protected abstract boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) throws ActionProcessorException;

    @Override
    public final void process(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) throws ActionProcessorException {
        validateInputs(gameState, player, additionalDataMap);
        doProcess(gameState, player, additionalDataMap);
    }

    protected abstract void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) throws ActionProcessorException;

    /**
     * override if you have types to validate
     *
     * @return
     */
    protected Collection<AdditionalDataType> requiredTypes() {
        return new HashSet<>();
    }

    protected void validateInputs(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        if (gameState == null) throw new ActionProcessorException("gameState may not be null");
        if (player == null) throw new ActionProcessorException("player may not be null");
        validateMap(additionalDataMap, requiredTypes());
    }

    protected void validateMap(Map<AdditionalDataType, String> additionalDataMap, Collection<AdditionalDataType> requiredTypes) {
        if (requiredTypes != null && !requiredTypes.isEmpty()) {
            // check we have all keys we require
            Collection<String> missingTypes = new HashSet<>();
            for (AdditionalDataType requiredType : requiredTypes) {
                if (!additionalDataMap.containsKey(requiredType)) missingTypes.add(requiredType.name());
            }
            if (!missingTypes.isEmpty()) {
                throw new ActionProcessorException("This Action is missing the required additional data keys: " + String.join(",", missingTypes));
            }
        }
    }


}
