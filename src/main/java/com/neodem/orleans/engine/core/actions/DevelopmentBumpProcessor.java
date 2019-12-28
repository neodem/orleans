package com.neodem.orleans.engine.core.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;

import static com.neodem.orleans.engine.original.DevelopmentHelper.MAXTRACK;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class DevelopmentBumpProcessor implements ActionProcessor {

    private final int bumpSize;

    public DevelopmentBumpProcessor(int bumpSize) {
        this.bumpSize = bumpSize;
    }

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        int trackIndex = player.getTracks().get(Track.Development);
        return trackIndex != MAXTRACK;
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
        int trackIndex = player.getTrackValue(Track.Development);

        trackIndex += bumpSize;
        if (trackIndex > MAXTRACK) trackIndex = MAXTRACK;

        DevelopmentHelper.processReward(trackIndex - bumpSize, trackIndex, gameState, player);

        player.getTracks().put(Track.Development, trackIndex);
    }
}
