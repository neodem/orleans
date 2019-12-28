package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public interface ActionProcessor {
    boolean isAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);
    void process(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap);
}
