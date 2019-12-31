package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class UniversityProcessor extends ActionProcessorBase {
    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Scholars);
        return trackIndex != 5;
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
    }
}
