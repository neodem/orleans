package com.neodem.orleans.engine.original.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum PlaceTile {
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
    public static PlaceTile fromValue(String v) {
        return PlaceTile.valueOf(v);
    }
}
