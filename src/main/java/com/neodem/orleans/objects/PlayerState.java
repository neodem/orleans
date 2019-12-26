package com.neodem.orleans.objects;

import java.util.Collection;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class PlayerState {
    private String playerId;
    private int coinCount;
    private Collection<TrackInfo> tracks;
    private Bag<Token> bag;
    private TokenLocation tokenLocation;
    private Collection<PlaceTile> placeTiles;

    private Collection<TokenLocation> tradingStationLocations;
    private Map<GoodType, Integer> goodCounts;
}
