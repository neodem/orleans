package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum HourGlassTile {
    Pilgrimage,
    Plague,
    Taxes,
    TradingDay,
    Income,
    Harvest;

    @JsonCreator
    public static HourGlassTile fromValue(String v) {
        return HourGlassTile.valueOf(v);
    }
}
