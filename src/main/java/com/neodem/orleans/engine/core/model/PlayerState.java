package com.neodem.orleans.engine.core.model;

import com.google.common.base.Objects;
import com.neodem.orleans.Util;
import com.neodem.orleans.collections.Bag;
import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.Loggable;
import com.neodem.orleans.engine.original.model.CitizenType;
import com.neodem.orleans.engine.original.model.PlaceTile;
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
    protected final Map<GoodType, Integer> goodCounts = new HashMap<>();

    private final Map<ActionType, TechTile> techTileMap = new HashMap<>();

    // followers are either in the bag, market or plans
    private final Bag<Follower> bag = new Bag<>();
    private Grouping<Follower> market = new Grouping<>();
    private final Map<ActionType, List<Follower>> plans = new HashMap<>();

    private final Collection<CitizenType> claimedCitizens = new HashSet<>();
    private final Collection<PlaceTile> placeTiles = new HashSet<>();

    private final Collection<TokenLocation> tradingStationLocations = new ArrayList<>();
    protected TokenLocation merchantLocation;

    private int coinCount = 5;
    private int tradingStationCount = 10;
    private boolean planLocked = false;
    private boolean passed = false;
    private Loggable log;

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

    public Grouping<Follower> getMarket() {
        return market;
    }

    public Map<ActionType, TechTile> getTechTileMap() {
        return techTileMap;
    }

    public void addTechTile(TechTile techTile) {
        techTileMap.put(techTile.getActionType(), techTile);
    }

    public int getCoinCount() {
        return coinCount;
    }

    public int removeCoin() {
        log.writeLine("" + playerId + " loses 1 coin");
        return --coinCount;
    }

    public int removeCoin(int coins) {
        log.writeLine("" + playerId + " loses " + coins + " coins");
        coinCount -= coins;
        return coinCount;
    }

    public int addCoin() {
        log.writeLine("" + playerId + " gains 1 coin");
        return ++coinCount;
    }

    public int addCoin(int coins) {
        log.writeLine("" + playerId + " gains " + coins + " coins");
        coinCount += coins;
        return coinCount;
    }

    /**
     * bump a track and return the new trackIndex
     *
     * @param track
     * @return
     */
    public int bumpTrack(Track track) {
        int trackIndex = Util.mapInc(tracks, track);
        return trackIndex;
    }

    public Map<ActionType, List<Follower>> getPlans() {
        return plans;
    }

    public boolean isPlanLocked() {
        return planLocked;
    }

    public int getTrackValue(Track track) {
        return tracks.get(track);
    }

    public void addToBag(Follower follower) {
        this.bag.add(follower);
    }

    public TokenLocation getMerchantLocation() {
        return merchantLocation;
    }

    public void setMerchantLocation(TokenLocation merchantLocation) {
        this.merchantLocation = merchantLocation;
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
        if (tradingStationCount > 0) {
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
        Util.mapInc(goodCounts, goodType);
    }

    public void removeGood(GoodType goodType) {
        Integer count = this.goodCounts.get(goodType);
        if (count > 0) {
            Util.mapDec(goodCounts, goodType);
        } else {
            throw new IllegalStateException("No good to remove of type: " + goodType);
        }
    }

    public Map<Track, Integer> getTracks() {
        return tracks;
    }

    public Bag<Follower> getBag() {
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
        for (int i = 0; i < drawCount; i++) {
            Follower follower = bag.take();
            if (follower != null) {
                log.writeLine("" + playerId + " draws " + follower + " from her bag and adds to her market");
                market = newMarketWith(follower);
            }
        }
    }

    public boolean isPlanSet() {
        return planLocked;
    }

    public void addToPlan(ActionType actionType, List<Follower> followers) {
        List<Follower> allocatedFollowers = plans.get(actionType);
        if (allocatedFollowers == null) allocatedFollowers = new ArrayList<>();
        for (Follower follower : followers) {
            allocatedFollowers.add(follower);
            log.writeLine("" + playerId + " adds " + follower + " to plan for " + actionType);
        }
        plans.put(actionType, allocatedFollowers);
    }

    public void removeFromMarket(List<Follower> followers) {
        for (Follower follower : followers) {
            market = newMarketWithout(follower);
            log.writeLine("" + playerId + " removes " + follower + " from her market");
        }
    }

    public boolean availableInMarket(List<Follower> followers) {
        Grouping<Follower> toPullFromMaket = new Grouping<Follower>(followers);
        return toPullFromMaket.canFitInto(market);
    }

    public void lockPlan() {
        planLocked = true;
    }

    public void resetPlan() {
        planLocked = false;
        plans.clear();
    }

    public void addLog(Loggable log) {
        this.log = log;
    }

    private Grouping<Follower> newMarketWithout(Follower follower) {
        List<Follower> currentFollowers = market.getTemplate();
        currentFollowers.remove(follower);
        return new Grouping<>(currentFollowers);
    }

    private Grouping<Follower> newMarketWith(Follower follower) {
        List<Follower> currentFollowers = market.getTemplate();
        currentFollowers.add(follower);
        return new Grouping<>(currentFollowers);
    }

    public boolean isPassed() {
        return passed;
    }

    public void passActionPhase() {
        passed = true;
    }

    public void resetPass() {
        passed = false;
    }

    public void unPlan(ActionType actionType) {
        List<Follower> followers = plans.get(actionType);
        for (Follower follower : followers) {
            addToBag(follower);
        }
        plans.remove(actionType);
    }

    public Collection<CitizenType> getClaimedCitizens() {
        return claimedCitizens;
    }

    public void addCitizen(CitizenType citizenType) {
        claimedCitizens.add(citizenType);
    }

    public void addTradingHallToCurrentLocation() {
        tradingStationLocations.add(merchantLocation);
    }

}
