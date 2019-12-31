package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
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
public class CastleProcessor extends ActionProcessorBase {

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.getTrackValue(Track.Knights);
        return trackIndex != 5;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        int trackIndex = player.bumpTrack(Track.Knights);

        if (trackIndex == 4 && !gameState.isCitizenClaimed(CitizenType.KnightTrack)) {
            gameState.citizenClaimed(CitizenType.KnightTrack);
            player.addCitizen(CitizenType.KnightTrack);
        }
    }
}
