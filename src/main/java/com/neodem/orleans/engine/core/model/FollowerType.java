package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum FollowerType {
    Farmer,
    StarterFarmer,
    Boatman,
    StarterBoatman,
    Craftsman,
    StarterCraftsman,
    Trader,
    StarterTrader,
    Scholar,
    Knight,
    Monk,
    Any,
    None;

    @JsonCreator
    public static FollowerType fromValue(String v) {
        return FollowerType.valueOf(v);
    }
}
