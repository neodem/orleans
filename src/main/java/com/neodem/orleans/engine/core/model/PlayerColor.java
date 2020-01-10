package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public enum PlayerColor {
    Blue, Green, Yellow, Red;

    // cache of the types
    private static final List<PlayerColor> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static PlayerColor randomColor() {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }

    @JsonCreator
    public static PlayerColor fromValue(String v) {
        return PlayerColor.valueOf(v);
    }
}
