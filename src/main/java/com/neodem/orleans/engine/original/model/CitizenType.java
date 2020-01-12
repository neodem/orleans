package com.neodem.orleans.engine.original.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public enum CitizenType {
    Dev1, Dev2, Dev3, KnightTrack, BoatTrack, BenefitTrack, TradingStationBonus;

    @JsonCreator
    public static CitizenType fromValue(String v) {
        return CitizenType.valueOf(v);
    }

}
