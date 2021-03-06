package com.neodem.orleans.engine.original;

import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.model.CitizenType;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class DevelopmentHelper {
    public static final int MAXTRACK = 30;

    public static boolean isCoinSlot(int trackIndex) {
        return trackIndex == 7 || trackIndex == 12 || trackIndex == 25;
    }

    public static void processReward(int previousTrackIndex, int trackIndex, GameState gameState, PlayerState player) {
        if (trackIndex > 3 && !gameState.isCitizenClaimed(CitizenType.Dev1)) {
            gameState.citizenClaimed(CitizenType.Dev1);
            player.addCitizen(CitizenType.Dev1);
        }
        if (trackIndex > 18 && !gameState.isCitizenClaimed(CitizenType.Dev2)) {
            gameState.citizenClaimed(CitizenType.Dev2);
            player.addCitizen(CitizenType.Dev2);
        }
        if (trackIndex > 28 && !gameState.isCitizenClaimed(CitizenType.Dev3)) {
            gameState.citizenClaimed(CitizenType.Dev3);
            player.addCitizen(CitizenType.Dev3);
        }

        if (trackIndex >= 7 && previousTrackIndex < 7) {
            player.addCoin(3);
        }

        if (trackIndex >= 12 && previousTrackIndex < 12) {
            player.addCoin(4);
        }

        if (trackIndex >= 25 && previousTrackIndex < 25) {
            player.addCoin(5);
        }
    }

    public static int getLevel(int index) {
        int level = 1;

        if (index >= 4) level = 2;
        else if (index >= 9) level = 3;
        else if (index >= 15) level = 4;
        else if (index >= 22) level = 5;
        else if (index == 30) level = 6;

        return level;
    }
}
