package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;

import java.util.Map;

import static com.neodem.orleans.engine.core.model.FollowerType.Scholar;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class UniversityProcessor extends ActionProcessorBase {
    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Scholars);
        return trackIndex != 5 && gameState.getFollowerInventory().get(Scholar) != 0;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Scholars);
        trackIndex++;
        player.setTrackIndex(Track.Scholars, trackIndex);

        int reward = trackIndex + 1;

        int devTrackIndex = player.getTrackValue(Track.Development);
        DevelopmentHelper.processReward(devTrackIndex, devTrackIndex + reward, gameState, player);
        player.setTrackIndex(Track.Development, trackIndex);

        gameState.removeFollowerFromInventory(Scholar);
        player.addToBag(new Follower(Scholar));
    }
}
