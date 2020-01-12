package com.neodem.orleans.engine.original;

import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.core.model.TortureType;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.OriginalGameState;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/11/20
 */
public class TortureHelper {

    /**
     * a plan is valid if the player can pay the thing they are offering
     *
     * @param player
     * @param tortureType
     * @param additionalDataMap
     * @return
     */
    public static boolean isValidPlan(PlayerState player, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap) {
        boolean valid;

        switch (tortureType) {
            case Follower:
                valid = !player.getBag().isEmpty();
                break;
            case TechTile:
                valid = !player.getTechTileMap().isEmpty();
                break;
            case GoodsTile:
                GoodType good = Util.getGoodFromADMap(additionalDataMap, AdditionalDataType.good);
                valid = player.getGoodCount(good) > 0;
                break;
            case PlaceTile:
                valid = !player.getPlaceTiles().isEmpty();
                break;
            case TradingStation:
                if (additionalDataMap.containsKey(AdditionalDataType.from)) {
                    TokenLocation from = Util.getLocationFromADMap(additionalDataMap, AdditionalDataType.from);
                    valid = player.getTradingStationLocations().contains(from);
                } else {
                    valid = player.getTradingStationCount() != player.tradingStationMax();
                }
                break;
            case DevelopmentPoint:
                valid = player.getTrackValue(Track.Development) > 0;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tortureType);
        }

        return valid;
    }

    // TODO
    public static void applyPlan(OriginalGameState gameState, PlayerState player, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap) {
        switch (tortureType) {
            case Follower:
                break;
            case TechTile:
                break;
            case GoodsTile:
                break;
            case PlaceTile:
                break;
            case TradingStation:
                break;
            case DevelopmentPoint:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tortureType);
        }
    }
}
