package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/11/20
 */
public enum TortureType {
    TradingStation,
    Follower,
    DevelopmentPoint,
    GoodsTile,
    PlaceTile,
    TechTile;

    @JsonCreator
    public static TortureType fromValue(String v) {
        return TortureType.valueOf(v);
    }
}
