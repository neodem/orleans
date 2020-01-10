package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum GamePhase {
    HourGlass,
    Census,
    Followers,
    Planning,
    Actions,
    Event,
    StartPlayer,
    Setup,
    Scoring, Complete;

    @JsonCreator
    public static GamePhase fromValue(String v) {
        return GamePhase.valueOf(v);
    }
}
