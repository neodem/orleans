package com.neodem.orleans.model;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public abstract class PlayerState {
    protected final String playerId;
    protected final PlayerColor playerColor;
    protected final Map<Track, Integer> tracks = new HashMap<>();
    protected final Map<GoodType, Integer> goodCounts= new HashMap<>();
    private final Bag<FollowerType> bag = new Bag<>();
    private final List<FollowerType> market = new ArrayList<>();

    private final Collection<PlaceTile> placeTiles = new HashSet<>();
    private final Collection<TokenLocation> tradingStationLocations = new ArrayList<>();
    protected TokenLocation tokenLocation;
    private int coinCount = 5;
    private int tradingStationCount = 10;
    private boolean planLocked = false;

    public PlayerState(String playerId, PlayerColor playerColor) {
        Assert.notNull(playerId, "playerId may not be null");
        Assert.notNull(playerColor, "playerColor may not be null");
        this.playerId = playerId;
        this.playerColor = playerColor;
        initState();
    }

    protected abstract void initState();

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

    public PlayerColor getPlayerColor() {
        return playerColor;
    }

    public String getPlayerId() {
        return playerId;
    }

    public List<FollowerType> getMarket() {
        return market;
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

    public void addToBag(FollowerType followerType) {
        this.bag.add(followerType);
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

    public Bag<FollowerType> getBag() {
        return bag;
    }

    public int getTradingStationCount() {
        return tradingStationCount;
    }

    /**
     * will draw x followers from the bag and put on the market
     *
     * @param drawCount
     */
    public void drawFollowers(int drawCount) {
        for(int i=0;i<drawCount;i++) {
            FollowerType follower = bag.take();
            if(follower != null) {
                market.add(follower);
            }
        }
    }

    public boolean isPlanSet() {
        return planLocked;
    }

    public void addToPlan(ActionType actionType, List<FollowerType> followerTypes) {

    }

    public void removeFromMarket(List<FollowerType> followerTypes) {
        for(FollowerType follower : followerTypes) {
            market.remove(follower);
            // TODO game log
        }
    }

    public boolean availableInMarket(List<FollowerType> followerTypes) {
        boolean result = false;

        // TODO

        return result;
    }

    public void lockPlan() {
        planLocked = true;
    }

    public void resetPlan() {
        planLocked = false;
        //TODO
    }
}
