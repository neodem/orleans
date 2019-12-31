package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;

import java.util.Map;

import static com.neodem.orleans.engine.original.DevelopmentHelper.MAXTRACK;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class DevelopmentBumpProcessor extends ActionProcessorBase {

    private final int bumpSize;

    public DevelopmentBumpProcessor(int bumpSize) {
        this.bumpSize = bumpSize;
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Development);
        return trackIndex != MAXTRACK;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Development);

        trackIndex += bumpSize;
        if (trackIndex > MAXTRACK) trackIndex = MAXTRACK;

        DevelopmentHelper.processReward(trackIndex - bumpSize, trackIndex, gameState, player);

        player.setTrackIndex(Track.Development, trackIndex);
    }
}
