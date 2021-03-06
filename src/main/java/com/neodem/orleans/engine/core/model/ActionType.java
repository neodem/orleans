package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public enum ActionType {

    Ship,
    Wagon,
    GuildHall,
    Castle,
    Scriptorium,
    TownHall,
    Monastery,
    FarmHouse,
    Village,
    University,

    // from place tiles
    Hayrick,
    CheeseFactory,
    Winery,
    WoolManufacturer,
    TailorShop,
    ShippingLine,
    Brewery,
    Library,
    Windmill,
    Cellar,
    Pharmacy,
    Office,
    Bathhouse,
    Hospital,
    School,
    HorseWagon,
    HerbGarden,
    Sacristy,
    GunpowderTower,
    Laboratory;

    @JsonCreator
    public static ActionType fromValue(String v) {
        return ActionType.valueOf(v);
    }
}
