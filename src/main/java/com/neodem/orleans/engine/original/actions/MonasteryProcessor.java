package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.HourGlassTile;
import com.neodem.orleans.engine.core.model.PlayerState;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class MonasteryProcessor implements ActionProcessor {
    @Override
    public boolean isAllowed(GameState gameState, PlayerState player) {
        return gameState.getFollowerInventory().get(Follower.Monk) > 0 && gameState.getCurrentHourGlass() != HourGlassTile.Pilgrimage;
    }

    @Override
    public void process(GameState gameState, PlayerState player) {
        player.addToBag(Follower.Monk);
        int monkCount = gameState.getFollowerInventory().get(Follower.Monk);
        gameState.getFollowerInventory().put(Follower.Monk, --monkCount);
    }
}
