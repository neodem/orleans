package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.original.model.BenefitName;
import com.neodem.orleans.engine.original.model.PlaceTile;

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
                throw new ActionProcessorException("This Action is missing the required aditional data keys: " + String.join(",", missingTypes));
            }
        }
    }

    public static TokenLocation getLocationFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getLocationFromName(value);
    }

    public static TokenLocation getLocationFromName(String stringValue) {
        TokenLocation location;
        try {
            location = TokenLocation.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine Location from '" + stringValue + "'");
        }
        return location;
    }

    public static GoodType getGoodFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getGoodFromName(value);
    }

    public static FollowerType getFollowerFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getFollowerFromName(value);
    }

    public static PlaceTile getPlaceTileFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getPlaceTileFromName(value);
    }

    public static ActionType getActionTypeFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getActionTypeFromName(value);
    }

    public static BenefitName getBenefitNameFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getBenefitNameFromValue(value);
    }

    public static int getIntegerFromMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        int integer;
        try {
            integer = Integer.parseInt(value);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine integer from '" + value + "'");
        }
        return integer;
    }

    public static BenefitName getBenefitNameFromValue(String stringValue) {
        if (stringValue == null) return null;
        BenefitName type;
        try {
            type = BenefitName.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine BenefitName from '" + stringValue + "'");
        }
        return type;
    }

    public static GoodType getGoodFromName(String stringValue) {
        if (stringValue == null) return null;
        GoodType type;
        try {
            type = GoodType.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine GoodType from '" + stringValue + "'");
        }
        return type;
    }

    public static PlaceTile getPlaceTileFromName(String stringValue) {
        if (stringValue == null) return null;
        PlaceTile type;
        try {
            type = PlaceTile.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine PlaceTile from '" + stringValue + "'");
        }
        return type;
    }

    public static ActionType getActionTypeFromName(String stringValue) {
        if (stringValue == null) return null;
        ActionType type;
        try {
            type = ActionType.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine ActionType from '" + stringValue + "'");
        }
        return type;
    }

    public static FollowerType getFollowerFromName(String stringValue) {
        if (stringValue == null) return null;
        FollowerType followerType;
        try {
            followerType = FollowerType.valueOf(stringValue);
        } catch (IllegalArgumentException e) {
            throw new ActionProcessorException("Cannot determine Follower Type from '" + stringValue + "'");
        }
        return followerType;
    }

}
