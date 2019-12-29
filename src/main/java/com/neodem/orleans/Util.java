package com.neodem.orleans;

import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class Util {

    public static final <K> int mapInc(Map<K, Integer> map, K key) {
        Integer value = map.get(key);
        if (value == null) value = 0;
        map.put(key, ++value);
        return value;
    }

    public static final <K> int mapDec(Map<K, Integer> map, K key) {
        Integer value = map.get(key);
        if (value == null) throw new IllegalStateException("No Value for Key: " + key);
        map.put(key, --value);
        return value;
    }
}