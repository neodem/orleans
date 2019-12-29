package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public abstract class ActionProcessorBase implements ActionProcessor {

    @Override
    public final boolean isAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) throws ActionProcessorException {
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
        validateMap(additionalDataMap);
    }

    protected void validateMap(Map<AdditionalDataType, String> additionalDataMap) {
        Collection<AdditionalDataType> requiredTypes = requiredTypes();
        Collection<String> missingTypes = new HashSet<>();

        if (requiredTypes != null && !requiredTypes.isEmpty()) {
            for (AdditionalDataType requiredType : requiredTypes) {
                if (!additionalDataMap.containsKey(requiredType)) missingTypes.add(requiredType.name());
            }
        }

        if (!missingTypes.isEmpty()) {
            throw new ActionProcessorException("This Action is missing the required aditional data keys: " + String.join(",", missingTypes));
        }
    }

    protected TokenLocation getLocationFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType additionalDataType) {
        String locationName = additionalDataMap.get(additionalDataType);
        return getLocationFromName(locationName);
    }

    protected TokenLocation getLocationFromName(String locationName) {
        TokenLocation location;
        try {
            location = TokenLocation.valueOf(locationName);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine Location from '" + locationName +"'");
        }
        return location;
    }

    protected Follower getFollowerFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType additionalDataType) {
        String followerName = additionalDataMap.get(additionalDataType);
        return getFollowerFromName(followerName);
    }

    protected Follower getFollowerFromName(String followerName) {
        Follower followerType;
        try {
            followerType = Follower.valueOf(followerName);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine Follower Type from '" + followerName +"'");
        }
        return followerType;
    }

}
