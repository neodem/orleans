package com.neodem.orleans.objects;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class PlayerState {
    private final String playerId;
    private int coinCount = 5;
    private final Map<Track, Integer> tracks = new HashMap<>();
    private final Bag<Token> bag = new Bag<>();
    private TokenLocation tokenLocation;
    private final Collection<PlaceTile> placeTiles = new HashSet<>();
    private final Collection<TokenLocation> tradingStationLocations = new ArrayList<>();
    private int tradingStationCount = 10;
    private final Map<GoodType, Integer> goodCounts= new HashMap<>();

    public PlayerState(String playerId) {
        Assert.notNull(playerId, "playerId may not be null");
        this.playerId = playerId;

        tracks.put(Track.Farmers, 0);
        tracks.put(Track.Craftsmen, 0);
        tracks.put(Track.Traders, 0);
        tracks.put(Track.Boatmen, 0);
        tracks.put(Track.Knights, 0);
        tracks.put(Track.Scholars, 0);
        tracks.put(Track.Development, 0);

        addToBag(Token.StarterBoatmen);
        addToBag(Token.StarterCraftsman);
        addToBag(Token.StarterFarmer);
        addToBag(Token.StarterTrader);

        goodCounts.put(GoodType.Grain, 0);
        goodCounts.put(GoodType.Cheese, 0);
        goodCounts.put(GoodType.Wine, 0);
        goodCounts.put(GoodType.Wool, 0);
        goodCounts.put(GoodType.Brocade, 0);

        tokenLocation = TokenLocation.Orleans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerState that = (PlayerState) o;
        return Objects.equal(playerId, that.playerId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(playerId);
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getCoinCount() {
        return coinCount;
    }

    public int removeCoin() {
        return --coinCount;
    }

    public int addCoin() {
        return ++coinCount;
    }

    public int getTrackValue(Track track) {
        return tracks.get(track);
    }

    public void bumpTrack(Track track) {
        Integer value = tracks.get(track);
        tracks.put(track, ++value);
    }

    public Token pullFromBag() {
        Token token = bag.iterator().next();
        bag.remove(token);
        return token;
    }

    public void addToBag(Token token) {
        this.bag.add(token);
    }

    public TokenLocation getTokenLocation() {
        return tokenLocation;
    }

    public void setTokenLocation(TokenLocation tokenLocation) {
        this.tokenLocation = tokenLocation;
    }

    public Collection<PlaceTile> getPlaceTiles() {
        return placeTiles;
    }

    public void addPlaceTile(PlaceTile placeTile) {
        this.placeTiles.add(placeTile);
    }

    public Collection<TokenLocation> getTradingStationLocations() {
        return tradingStationLocations;
    }

    public void placeTradingStation(TokenLocation tradingStationLocation) {
        if(tradingStationCount > 0) {
            this.tradingStationLocations.add(tradingStationLocation);
            tradingStationCount--;
        } else {
            throw new IllegalStateException("Out of trading stations");
        }
    }

    public Map<GoodType, Integer> getGoodCounts() {
        return goodCounts;
    }

    public void addGood(GoodType goodType) {
        Integer count = this.goodCounts.get(goodType);
        this.goodCounts.put(goodType, ++count);
    }

    public void removeGood(GoodType goodType) {
        Integer count = this.goodCounts.get(goodType);
        if(count > 0) {
            this.goodCounts.put(goodType, --count);
        } else {
            throw new IllegalStateException("No good to remove of type: " + goodType);
        }
    }

    public Map<Track, Integer> getTracks() {
        return tracks;
    }

    public Bag<Token> getBag() {
        return bag;
    }

    public int getTradingStationCount() {
        return tradingStationCount;
    }
}
