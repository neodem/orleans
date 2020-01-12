package com.neodem.orleans.engine.original;

import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerBag;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.core.model.TortureType;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.PlaceTile;

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
                valid = player.getBag().containsANonStarterFollower();
                break;
            case TechTile:
                ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
                valid = player.getTechTileMap().containsKey(actionType);
                break;
            case GoodsTile:
                GoodType good = Util.getGoodFromADMap(additionalDataMap, AdditionalDataType.good);
                valid = player.getGoodCount(good) > 0;
                break;
            case PlaceTile:
                PlaceTile placeTile = Util.getPlaceTileFromADMap(additionalDataMap, AdditionalDataType.placeTile);
                valid = player.getPlaceTiles().contains(placeTile);
                break;
            case TradingStation:
                if (additionalDataMap.containsKey(AdditionalDataType.from)) {
                    TokenLocation from = Util.getLocationFromADMap(additionalDataMap, AdditionalDataType.from);
                    valid = player.getTradingStationLocations().contains(from);
                } else {
                    valid = player.getTradingStationCount() != player.getMaxAllowableTradingStations();
                }
                break;
            case DevelopmentPoint:
                int trackValue = player.getTrackValue(Track.Development);
                valid = trackValue > 0 && !DevelopmentHelper.isCoinSlot(trackValue) && !DevelopmentHelper.isCoinSlot(trackValue - 1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tortureType);
        }

        return valid;
    }

    public static void applyPlan(PlayerState player, TortureType tortureType, Map<AdditionalDataType, String> additionalDataMap) {
        switch (tortureType) {
            case Follower:
                FollowerBag bag = player.getBag();
                Follower f = null;
                do {
                    bag.add(f);
                    f = bag.take();
                } while (f.isStarter());
                player.addCoin();
                player.writeLog("pulled a " + f + " from their bag and lost them due to torture.");
                break;
            case TechTile:
                ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
                player.removeTechTile(actionType);
                player.addCoin();
                player.writeLog("lost their tech tile on " + actionType + " due to torture.");
                break;
            case GoodsTile:
                GoodType good = Util.getGoodFromADMap(additionalDataMap, AdditionalDataType.good);
                player.removeGood(good);
                player.addCoin();
                player.writeLog("lost a " + good + " due to torture.");
                break;
            case PlaceTile:
                PlaceTile placeTile = Util.getPlaceTileFromADMap(additionalDataMap, AdditionalDataType.placeTile);
                player.removePlaceTile(placeTile);
                player.addCoin();
                player.writeLog("lost " + placeTile + " due to torture.");
                break;
            case TradingStation:
                if (additionalDataMap.containsKey(AdditionalDataType.from)) {
                    TokenLocation from = Util.getLocationFromADMap(additionalDataMap, AdditionalDataType.from);
                    player.removeTradingStationFromLocation(from);
                    player.addCoin();
                    player.writeLog("lost a trading station at " + from + " due to torture.");
                } else {
                    player.decrementMaxAllowableTradingStations();
                    player.addCoin();
                    player.writeLog("lost a trading station from their supply due to torture.");
                }
                break;
            case DevelopmentPoint:
                player.decrementTrack(Track.Development);
                player.addCoin();
                player.writeLog("lost a development point due to torture.");
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + tortureType);
        }
    }
}
