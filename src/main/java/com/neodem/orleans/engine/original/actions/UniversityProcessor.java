package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
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
public class UniversityProcessor implements ActionProcessor {
    @Override
    public boolean isAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTracks().get(Track.Scholars);
        return trackIndex != 5;
    }

    @Override
    public void process(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Scholars);
        trackIndex++;
        player.getTracks().put(Track.Scholars, trackIndex);

        int reward = trackIndex + 1;

        int devTrackIndex = player.getTrackValue(Track.Development);
        DevelopmentHelper.processReward(devTrackIndex, devTrackIndex + reward, gameState, player);
        player.getTracks().put(Track.Development, trackIndex);
    }
}
