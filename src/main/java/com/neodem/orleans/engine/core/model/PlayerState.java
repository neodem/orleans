package com.neodem.orleans.engine.core.model;

import com.google.common.base.Objects;
import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.Loggable;
import com.neodem.orleans.engine.original.model.CitizenType;
import com.neodem.orleans.engine.original.model.PlaceTile;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public abstract class PlayerState {

    /**
     * the name/id of the player (should be unique in the game)
     */
    protected final String playerId;

    /**
     * color of the player
     */
    protected final PlayerColor playerColor;

    /**
     * if the player is complete the current phase
     */
    private boolean phaseComplete = false;

    /**
     * the bag the player has
     */
    protected final FollowerBag bag = new FollowerBag();

    /**
     * actions the player has Followers in are called 'plans'
     */
    protected final Map<ActionType, FollowerTrack> plans = new HashMap<>();

    protected TokenLocation merchantLocation;

    private int coinCount = 5;

    // internal state
    protected final Market market = new Market();
    protected final Map<Track, Integer> tracks = new HashMap<>();
    protected final Map<GoodType, Integer> goodCounts = new HashMap<>();
    private final Map<ActionType, Integer> techTileMap = new HashMap<>();
    private final Collection<CitizenType> claimedCitizens = new HashSet<>();
    private final Collection<PlaceTile> placeTiles = new HashSet<>();
    private final Collection<TokenLocation> tradingStationLocations = new ArrayList<>();
    private final Collection<FollowerType> bathhouseChoices = new HashSet<>();

    private Loggable log;
    private final ActionHelper actionHelper;

    public PlayerState(String playerId, PlayerColor playerColor, ActionHelper actionHelper) {
        Assert.notNull(playerId, "playerId may not be null");
        Assert.notNull(playerColor, "playerColor may not be null");
        this.playerId = playerId;
        this.playerColor = playerColor;
        this.actionHelper = actionHelper;
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

    public Integer getTechTileSlot(ActionType actionType) {
        return getTechTileSlot(actionType);
    }

    public int getCoinCount() {
        return coinCount;
    }

    public Map<ActionType, FollowerTrack> getPlans() {
        return plans;
    }

    public Market getMarket() {
        return market;
    }

    public Map<GoodType, Integer> getGoodCounts() {
        return goodCounts;
    }

    public Map<ActionType, Integer> getTechTileMap() {
        return techTileMap;
    }

    public Collection<CitizenType> getClaimedCitizens() {
        return claimedCitizens;
    }

    public Collection<PlaceTile> getPlaceTiles() {
        return placeTiles;
    }

    public Collection<FollowerType> getBathhouseChoices() {
        return bathhouseChoices;
    }

    public boolean isPhaseComplete() {
        return phaseComplete;
    }

    public void setPhaseComplete(boolean phaseComplete) {
        this.phaseComplete = phaseComplete;
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

    public int getTrackValue(Track track) {
        return tracks.get(track);
    }

    public void addToBag(Follower followerType) {
        this.bag.add(followerType);
    }

    public TokenLocation getMerchantLocation() {
        return merchantLocation;
    }

    public void setMerchantLocation(TokenLocation merchantLocation) {
        this.merchantLocation = merchantLocation;
    }

    public void addPlaceTile(PlaceTile placeTile) {
        this.placeTiles.add(placeTile);
    }

    public Collection<TokenLocation> getTradingStationLocations() {
        return tradingStationLocations;
    }

    public void addTradingHallToCurrentLocation() {
        if (tradingStationLocations.size() == tradingStationMax()) {
            tradingStationLocations.add(merchantLocation);
        } else {
            throw new IllegalStateException("Out of trading stations");
        }
    }

    public int getTradingStationCount() {
        return tradingStationLocations.size();
    }

    /**
     * return the number of trading stations the player has in their personal supply
     *
     * @return
     */
    public int tradingStationMax() {
        return 10;
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

    public FollowerBag getBag() {
        return bag;
    }

    /**
     * will draw x followers from the bag and put on the market
     *
     * @param drawCount
     */
    public void drawFollowersFromBagToMarket(int drawCount) {
        for (int i = 0; i < drawCount; i++) {
            if (market.hasSpace()) {
                Follower follower = bag.take();
                if (follower != null) {
                    int slot = market.addToMarket(follower);
                    log.writeLine("" + playerId + " draws " + follower + " from her bag and adds to her market (slot " + slot + ")");
                }
            } else {
                log.writeLine("" + playerId + " cannot draw more followers since her market is full!");
                break;
            }
        }
    }

    public Follower removeFromMarket(int slot) {
        Follower follower = market.remove(slot);
        if (follower != null) {
            log.writeLine("" + playerId + " removes " + follower + " from her market");
        } else {
            log.writeLine("" + playerId + " tried to remove a follower from an empty slot in her market. slot= " + slot);
        }
        return follower;
    }

    public void addLog(Loggable log) {
        this.log = log;
    }

    public void unPlan(ActionType actionType) {
        FollowerTrack followerTrack = plans.get(actionType);
        Collection<Follower> removedFollowers = followerTrack.removeAllFollowers();

        for (Follower follower : removedFollowers) {
            addToBag(follower);
        }
        plans.remove(actionType);
    }

    public int getClaimedCitizenCount() {
        return claimedCitizens.size();
    }

    public void addCitizen(CitizenType citizenType) {
        claimedCitizens.add(citizenType);
    }


    public void addTechTile(ActionType actionType, int techPosition) {
        techTileMap.put(actionType, techPosition);
    }

    public int getFullGoodCount() {
        int goodsCount = 0;

        for (GoodType g : goodCounts.keySet()) {
            goodsCount += goodCounts.get(g);
        }

        return goodsCount;
    }

    public boolean isFoodAvailable() {
        return leastValuableFoodAvailable() != null;
    }

    public GoodType leastValuableFoodAvailable() {
        if (goodCounts.get(GoodType.Grain) > 0) return GoodType.Grain;
        if (goodCounts.get(GoodType.Cheese) > 0) return GoodType.Cheese;
        if (goodCounts.get(GoodType.Wine) > 0) return GoodType.Wine;
        return null;
    }

    /**
     * decide if the player can add a token to the given slot on the action. This is complex since they may have a School or Herb Garden..
     *
     * @param actionType
     * @param actionSlot
     * @param followerToken
     * @return
     */
    public boolean canAddToAction(ActionType actionType, int actionSlot, Follower followerToken) {
        FollowerTrack followerTrack = plans.get(actionType);
        if (followerTrack == null) {
            // if there is no followerTrack, init one from the template in the actionHelper
            followerTrack = actionHelper.getFollowerTrack(actionType);
            plans.put(actionType, followerTrack);
        }

        if (followerToken.getType() == FollowerType.Scholar && techTileMap.containsKey(ActionType.School)) {
            followerToken.addAlias(FollowerType.Craftsman);
            followerToken.addAlias(FollowerType.Trader);
            followerToken.addAlias(FollowerType.Farmer);
            followerToken.addAlias(FollowerType.Boatman);
            followerToken.addAlias(FollowerType.Knight);
        }

        if (followerToken.getType() == FollowerType.Boatman && techTileMap.containsKey(ActionType.HerbGarden)) {
            followerToken.addAlias(FollowerType.Craftsman);
            followerToken.addAlias(FollowerType.Trader);
            followerToken.addAlias(FollowerType.Farmer);
        }

        return followerTrack.canAdd(followerToken, actionSlot);
    }

    public void addTokenToAction(ActionType actionType, int actionSlot, Follower followerToken) {
        FollowerTrack followerTrack = plans.get(actionType);
        followerTrack.add(followerToken, actionSlot);
    }

    public FollowerType getBathhouseChoice() {
        if (bathhouseChoices.isEmpty()) return null;
        return bathhouseChoices.iterator().next();
    }

    public void resetBathhouseChoice() {
        this.bathhouseChoices.clear();

    }

    public void setBathhouseChoices(Collection<FollowerType> choices) {
        this.bathhouseChoices.clear();
        this.bathhouseChoices.addAll(choices);
    }

    public boolean hasTechForAction(ActionType actionType) {
        return techTileMap.containsKey(actionType);
    }

    public boolean isPlayingSacristy() {
        FollowerTrack followerTrack = plans.get(ActionType.Sacristy);
        return followerTrack != null && followerTrack.isReady(null);
    }

    public FollowerTrack getPlan(ActionType actionType) {
        return plans.get(actionType);
    }

    public boolean hasPlaceTile(PlaceTile bathhouse) {
        return placeTiles.contains(bathhouse);
    }

    public int getTrackLocation(Track track) {
        return tracks.get(track);
    }

    public int getAvailableMarketSlots() {
        return market.getAvailableSlots();
    }

    public void addToMarket(Follower follower) {
        market.addToMarket(follower);
    }

    public boolean isMarketSlotFilled(int marketSlot) {
        return market.isSlotFilled(marketSlot);
    }

    public void setTrackIndex(Track track, int trackIndex) {
        tracks.put(track, trackIndex);
    }

    protected Map<Track, Integer> getTracks() {
        return tracks;
    }

    public int getGoodCount(GoodType goodType) {
        return goodCounts.get(goodType);
    }
}
