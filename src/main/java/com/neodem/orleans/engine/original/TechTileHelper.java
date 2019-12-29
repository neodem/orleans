package com.neodem.orleans.engine.original;

import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TechTile;
import com.neodem.orleans.engine.core.model.Track;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class TechTileHelper {

    public static void addTechTileToPlayer(GameState gameState, PlayerState player, int techPosition, ActionType actionType, ActionHelper actionHelper) {
        int trackIndex = player.getTrackValue(Track.Craftsmen);

        if (trackIndex == 1 && techFollowerType != FollowerType.Farmer) {
            throw new ActionProcessorException("for the first tech track location you can only choose to place a Farmer");
        }

        TechTile techTile = new TechTile(actionType, techFollowerType);

        Map<ActionType, TechTile> techTileMap = player.getTechTileMap();
        if (techTileMap.containsKey(actionType)) {
            throw new ActionProcessorException("You already have a tech tile on the action: " + actionType);
        }

        if (actionHelper.getGrouping(actionType).size() == 1) {
            throw new ActionProcessorException("Tech tiles may not be placed on Actions with only one slot.");
        }

        gameState.removeTechTileFromInventory();

        player.addTechTile(techTile);
    }
}
