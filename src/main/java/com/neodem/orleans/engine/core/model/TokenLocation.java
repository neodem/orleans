package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum TokenLocation {
    PlayersHand,
    Orleans,
    Chartres,
    Etampes,
    LeMans,
    Chateaudun,
    Montargis,
    Venedome,
    Blois,
    Briare,
    Tours,
    Chinon,
    Montrichard,
    Vierzon,
    Loches,
    Bourges,
    Sancerre,
    Chatelleraut,
    LeBlanc,
    Chateauroux,
    Nevers,
    ArgentonSurCreuse,
    LaChatre,
    SAmandMontrond;

    @JsonCreator
    public static TokenLocation fromValue(String v) {
        return TokenLocation.valueOf(v);
    }

}
