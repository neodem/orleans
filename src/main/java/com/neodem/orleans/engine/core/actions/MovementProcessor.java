package com.neodem.orleans.engine.core.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.model.*;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class MovementProcessor extends ActionProcessorBase {

    private final PathType pathType;

    public MovementProcessor(PathType pathType) {
        this.pathType = pathType;
    }

    @Override
    protected Collection<AdditionalDataType> requiredTypes() {
        return Sets.newHashSet(AdditionalDataType.from, AdditionalDataType.to, AdditionalDataType.good);
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {

        TokenLocation from = getLocationFromMap(additionalDataMap, AdditionalDataType.from);
        TokenLocation merchantLocation = player.getMerchantLocation();
        if (merchantLocation == from) {
            TokenLocation to = getLocationFromMap(additionalDataMap, AdditionalDataType.to);

            PathBetween pathBetween = new PathBetween(from, to);

            BoardState board = gameState.getBoardState();
            Path path = board.getPathBetween(pathBetween, pathType);

            if (path == null) {
                throw new ActionProcessorException("" + pathType + " path does not exist between " + from + " and " + to);
            }

            GoodType desiredGood = getGoodFromMap(additionalDataMap, AdditionalDataType.good);
            if (!path.goodAvailable(desiredGood)) {
                throw new ActionProcessorException("The good: " + desiredGood + " is not on the path from " + from + " to " + to + ".");

            }
        } else {
            throw new ActionProcessorException("Players token is on " + merchantLocation + " and action is saying to move from " + from);
        }

        return true;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        TokenLocation from = getLocationFromMap(additionalDataMap, AdditionalDataType.from);
        TokenLocation to = getLocationFromMap(additionalDataMap, AdditionalDataType.to);
        PathBetween pathBetween = new PathBetween(from, to);
        BoardState board = gameState.getBoardState();
        Path path = board.getPathBetween(pathBetween, pathType);

        GoodType desiredGood = getGoodFromMap(additionalDataMap, AdditionalDataType.good);
        if (path.goodAvailable(desiredGood)) {
            path.removeGoodFromPath(desiredGood);
            player.addGood(desiredGood);
        }

        player.setMerchantLocation(to);
    }
}
