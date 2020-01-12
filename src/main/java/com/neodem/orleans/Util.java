package com.neodem.orleans;

import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.original.model.BenefitName;
import com.neodem.orleans.engine.original.model.PlaceTile;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class Util {

    public static final <K> int mapInc(Map<K, Integer> map, K key) {
        Integer value = map.get(key);
        if (value == null) value = 0;
        map.put(key, ++value);
        return value;
    }

    public static final <K> int mapDec(Map<K, Integer> map, K key) {
        Integer value = map.get(key);
        if (value == null) throw new IllegalStateException("No Value for Key: " + key);
        map.put(key, --value);
        return value;
    }

    public static GoodType getGoodFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getGoodFromName(value);
    }

    public static FollowerType getFollowerFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getFollowerFromName(value);
    }

    public static PlaceTile getPlaceTileFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getPlaceTileFromName(value);
    }

    public static ActionType getActionTypeFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getActionTypeFromName(value);
    }

    public static BenefitName getBenefitNameFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
        String value = additionalDataMap.get(key);
        return getBenefitNameFromValue(value);
    }

    public static TokenLocation getLocationFromADMap(Map<AdditionalDataType, String> additionalDataMap, AdditionalDataType key) {
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