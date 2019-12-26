package com.neodem.orleans.objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public enum GoodType {
    Grain,
    Cheese,
    Wine,
    Wool,
    Brocade;

    // cache of the types
    private static final List<GoodType> VALUES = Collections.unmodifiableList(Arrays.asList(values()));
    private static final int SIZE = VALUES.size();
    private static final Random RANDOM = new Random();

    public static GoodType randomGood()  {
        return VALUES.get(RANDOM.nextInt(SIZE));
    }
}
