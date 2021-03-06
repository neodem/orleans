package com.neodem.orleans.engine.original.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.TechTileHelper;
import com.neodem.orleans.engine.original.model.CitizenType;
import com.neodem.orleans.engine.original.model.OriginalGameState;
import com.neodem.orleans.engine.original.model.PlaceTile;

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
        FollowerType desiredFollowerType = Util.getFollowerFromADMap(additionalDataMap, AdditionalDataType.follower);

        boolean trackOk;
        switch (desiredFollowerType) {
            case Boatman:
                trackOk = player.getTrackValue(Track.Boatmen) < 5;
                trackOk = trackOk && gameState.getFollowerInventory().get(FollowerType.Boatman) != 0;
                break;
            case Craftsman:
                validateMap(additionalDataMap, Sets.newHashSet(AdditionalDataType.techAction, AdditionalDataType.position));
                if (gameState.getNumberTechTilesAvailable() == 0) {
                    throw new ActionProcessorException("There are no more tech tiles available");
                }

                ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
                int position = Util.getIntegerFromMap(additionalDataMap, AdditionalDataType.position);
                FollowerType followerType = actionHelper.getTypeForAction(actionType, position);

                if (followerType == FollowerType.Monk)
                    throw new ActionProcessorException("You may never replace a Monk Space with a tech tile");

                trackOk = player.getTrackValue(Track.Craftsmen) < 5;
                trackOk = trackOk && gameState.getFollowerInventory().get(FollowerType.Craftsman) != 0;
                break;
            case Trader:
                validateMap(additionalDataMap, Sets.newHashSet(AdditionalDataType.placeTile));
                trackOk = player.getTrackValue(Track.Traders) < 5;
                trackOk = trackOk && gameState.getFollowerInventory().get(FollowerType.Trader) != 0;
                break;
            default:
                throw new ActionProcessorException("desired follower " + desiredFollowerType + "is not a Boatman, Craftsman or Trader");
        }

        return trackOk && gameState.getFollowerInventory().get(desiredFollowerType) > 0;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        FollowerType desiredFollowerType = Util.getFollowerFromADMap(additionalDataMap, AdditionalDataType.follower);
        gameState.removeFollowerFromInventory(desiredFollowerType);
        player.addToBag(new Follower(desiredFollowerType));

        switch (desiredFollowerType) {
            case Boatman:
                handleBoatman(gameState, player);
                break;
            case Craftsman:
                handleCraftsman(gameState, player, additionalDataMap);
                break;
            case Trader:
                handleTrader((OriginalGameState) gameState, player, additionalDataMap);
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
        player.bumpTrack(Track.Craftsmen);
        ActionType actionType = Util.getActionTypeFromADMap(additionalDataMap, AdditionalDataType.techAction);
        int position = Util.getIntegerFromMap(additionalDataMap, AdditionalDataType.position);
        TechTileHelper.addTechTileToPlayer(gameState, player, position, actionType, actionHelper);
    }

    private void handleTrader(OriginalGameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.bumpTrack(Track.Traders);

        PlaceTile desiredPlaceTile = Util.getPlaceTileFromADMap(additionalDataMap, AdditionalDataType.placeTile);
        Collection<PlaceTile> placeTiles = gameState.getPlaceTiles1();
        if (trackIndex > 1) {
            placeTiles.addAll(gameState.getPlaceTiles2());
        }
        if (!placeTiles.contains(desiredPlaceTile)) {
            throw new ActionProcessorException("PlaceTile " + desiredPlaceTile + " is not available");
        }

        //
        if (gameState.getPlaceTiles1().contains(desiredPlaceTile)) {
            gameState.getPlaceTiles1().remove(desiredPlaceTile);
        } else {
            gameState.getPlaceTiles2().remove(desiredPlaceTile);
        }

        if (desiredPlaceTile == PlaceTile.Bathhouse) {
            gameState.setPlayerHasBathhouse(player.getPlayerId());
        }

        player.addPlaceTile(desiredPlaceTile);
    }
}
