package com.neodem.orleans.engine.original.model;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public enum BenefitName {
    BuildingCityWall, PapalConclave, DefeatingPlague, Astronomy, FoundingBoatmenGuild, BuildingCathedral, PeaceTreaty, Canalisation;

    @JsonCreator
    public static BenefitName fromValue(String v) {
        return BenefitName.valueOf(v);
    }
}
