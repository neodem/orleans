package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.CitizenType;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class CastleProcessor implements ActionProcessor {

    @Override
    public boolean isAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTracks().get(Track.Knights);
        return trackIndex != 5;
    }

    @Override
    public void process(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Knights);
        trackIndex++;

        if (trackIndex == 4 && !gameState.isCitizenClaimed(CitizenType.KnightTrack)) {
            gameState.citizenClaimed(CitizenType.KnightTrack);
            player.addCitizen(CitizenType.KnightTrack);
        }

        player.getTracks().put(Track.Knights, trackIndex);

    }
}
