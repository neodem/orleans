package com.neodem.orleans.engine.original.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlaceTile;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TechTile;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.CitizenType;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class VillageProcessor extends ActionProcessorBase {

    private final ActionHelper actionHelper;

    public VillageProcessor(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
    }

    @Override
    protected Collection<AdditionalDataType> requiredTypes() {
        return Sets.newHashSet(AdditionalDataType.follower);
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        Follower desiredFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower);

        boolean trackOk;
        switch (desiredFollower) {
            case Boatman:
                trackOk = player.getTrackValue(Track.Boatmen) < 5;
                break;
            case Craftsman:
                validateMap(additionalDataMap, Sets.newHashSet(AdditionalDataType.techAction, AdditionalDataType.techFollower));

                Follower techFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.techFollower);
                if (techFollower == Follower.Monk)
                    throw new ActionProcessorException("You may never replace a Monk with a tech tile");

                trackOk = player.getTrackValue(Track.Craftsmen) < 5;
                break;
            case Trader:
                validateMap(additionalDataMap, Sets.newHashSet(AdditionalDataType.placeTile));
                trackOk = player.getTrackValue(Track.Traders) < 5;
                break;
            default:
                throw new ActionProcessorException("desired follower " + desiredFollower + "is not a Boatman, Craftsman or Trader");
        }

        return trackOk && gameState.getFollowerInventory().get(desiredFollower) > 0;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        Follower desiredFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower);
        gameState.removeFollowerFromInventory(desiredFollower);
        player.addToBag(desiredFollower);

        switch (desiredFollower) {
            case Boatman:
                handleBoatman(gameState, player);
                break;
            case Craftsman:
                handleCraftsman(gameState, player, additionalDataMap);
                break;
            case Trader:
                handleTrader(gameState, player, additionalDataMap);
                break;
        }
    }

    private void handleBoatman(GameState gameState, PlayerState player) {
        int trackIndex = player.bumpTrack(Track.Boatmen);

        if (trackIndex == 5 && !gameState.isCitizenClaimed(CitizenType.BoatTrack)) {
            gameState.citizenClaimed(CitizenType.BoatTrack);
            player.addCitizen(CitizenType.BoatTrack);
        } else {
            player.addCoin(trackIndex + 1);
        }
    }

    private void handleCraftsman(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.bumpTrack(Track.Craftsmen);
        Follower techFollower = getFollowerFromMap(additionalDataMap, AdditionalDataType.techFollower);

        if (trackIndex == 1 && techFollower != Follower.Farmer) {
            throw new ActionProcessorException("for the first tech track location you can only choose to place a Farmer");
        }

        ActionType actionType = getActionTypeFromMap(additionalDataMap, AdditionalDataType.techAction);

        TechTile techTile = new TechTile(actionType, techFollower);

        Map<ActionType, TechTile> techTileMap = player.getTechTileMap();
        if (techTileMap.containsKey(actionType)) {
            throw new ActionProcessorException("You already have a tech tile on the action: " + actionType);
        }

        if (actionHelper.getGrouping(actionType).size() == 1) {
            throw new ActionProcessorException("Tech tiles may not be placed on Actions with only one slot.");
        }

        player.addTechTile(techTile);
    }

    private void handleTrader(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.bumpTrack(Track.Traders);

        PlaceTile desiredPlaceTile = getPlaceTileFromMap(additionalDataMap, AdditionalDataType.placeTile);
        Collection<PlaceTile> placeTiles = gameState.getPlaceTiles1();
        if (trackIndex > 1) {
            placeTiles.addAll(gameState.getPlaceTiles2());
        }
        if (!placeTiles.contains(desiredPlaceTile)) {
            throw new ActionProcessorException("PlaceTile " + desiredPlaceTile + " is not available");
        }

        //
        if(gameState.getPlaceTiles1().contains(desiredPlaceTile)) {
            gameState.getPlaceTiles1().remove(desiredPlaceTile);
        } else {
            gameState.getPlaceTiles2().remove(desiredPlaceTile);
        }

        player.addPlaceTile(desiredPlaceTile);
    }
}
