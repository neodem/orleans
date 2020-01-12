package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    protected String playerId;

    /**
     * color of the player
     */
    protected PlayerColor playerColor;

    /**
     * if the player is complete the current phase
     */
    private boolean phaseComplete = false;

    /**
     * the bag the player has
     */
    protected FollowerBag bag = new FollowerBag();

    /**
     * actions the player has Followers in are called 'plans'
     */
    protected Map<ActionType, FollowerTrack> plans = new HashMap<>();

    protected TokenLocation merchantLocation;

    private int coinCount = 5;

    // internal state
    protected Market market = new Market();

    @JsonProperty(value = "tracks")
    protected Map<Track, Integer> tracks = new HashMap<>();
    protected Map<GoodType, Integer> goodCounts = new HashMap<>();

    // which slot has the tech tile in it?
    private Map<ActionType, Integer> techTileMap = new HashMap<>();
    private Collection<CitizenType> claimedCitizens = new HashSet<>();
    private Collection<PlaceTile> placeTiles = new HashSet<>();
    private Collection<TokenLocation> tradingStationLocations = new ArrayList<>();
    private Collection<FollowerType> bathhouseChoices = new HashSet<>();

    private Loggable log;
    private ActionHelper actionHelper;
    private boolean beingTortured;

    public PlayerState(String playerId, PlayerColor playerColor, ActionHelper actionHelper) {
        Assert.notNull(playerId, "playerId may not be null");
        Assert.notNull(playerColor, "playerColor may not be null");
        this.playerId = playerId;
        this.playerColor = playerColor;
        this.actionHelper = actionHelper;
        initState();
    }

    protected PlayerState(JsonNode json) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            this.playerId = json.get("playerId").textValue();
            this.playerColor = PlayerColor.fromValue(json.get("playerColor").textValue());
            this.phaseComplete = json.get("phaseComplete").booleanValue();
            this.beingTortured = json.get("beingTortured").booleanValue();
            this.bag = new FollowerBag(json.get("bag"));

            this.plans = jsonUnmarshalPlans(json.get("plans").toString(), mapper);

            this.merchantLocation = TokenLocation.fromValue(json.get("merchantLocation").textValue());
            this.coinCount = json.get("coinCount").intValue();

            this.market = new Market(json.get("market"));

            TypeReference<HashMap<Track, Integer>> tracksRef = new TypeReference<>() {
            };
            this.tracks = mapper.readValue(json.get("tracks").toString(), tracksRef);

            TypeReference<HashMap<GoodType, Integer>> goodCountsRef = new TypeReference<>() {
            };
            this.goodCounts = mapper.readValue(json.get("goodCounts").toString(), goodCountsRef);

            TypeReference<HashMap<ActionType, Integer>> techTileRef = new TypeReference<>() {
            };
            this.techTileMap = mapper.readValue(json.get("techTileMap").toString(), techTileRef);

            TypeReference<HashSet<CitizenType>> claimedCitizensRef = new TypeReference<>() {
            };
            this.claimedCitizens = mapper.readValue(json.get("claimedCitizens").toString(), claimedCitizensRef);

            TypeReference<HashSet<PlaceTile>> placeTilesRef = new TypeReference<>() {
            };
            this.placeTiles = mapper.readValue(json.get("placeTiles").toString(), placeTilesRef);

            TypeReference<ArrayList<TokenLocation>> tradingStationLocationsRef = new TypeReference<>() {
            };
            this.tradingStationLocations = mapper.readValue(json.get("tradingStationLocations").toString(), tradingStationLocationsRef);

            TypeReference<HashSet<FollowerType>> bathhouseChoicesRef = new TypeReference<>() {
            };
            this.bathhouseChoices = mapper.readValue(json.get("bathhouseChoices").toString(), bathhouseChoicesRef);

        } catch (JsonMappingException e) {

            // TODO better than this
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        return techTileMap.get(actionType);
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

    public void writeLog(String line) {
        log.writeLine(playerId + ": " + line);
    }

    public int removeCoin() {
        return removeCoin(1);
    }

    public int removeCoin(int coins) {
        writeLog("loses " + coins + " coin");
        coinCount -= coins;

        if (coinCount < 0) {
            int tortureAmount = Math.abs(coinCount);
            writeLog("is in debt by " + tortureAmount + "coins and must endure torture");
            setBeingTortured(true);
        }

        return coinCount;
    }

    public int addCoin() {
        return addCoin(1);
    }

    public int addCoin(int coins) {
        writeLog("gains " + coins + " coins");
        coinCount += coins;

        if (beingTortured && coinCount >= 0) {
            writeLog("has satisfied their debt and endured torture enough!");
            beingTortured = false;
        }

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
        writeLog("increased Development track by one. New value=" + trackIndex + ".");
        return trackIndex;
    }

    public int decrementTrack(Track track) {
        int trackIndex = Util.mapDec(tracks, track);
        writeLog("decreased Development track by one. New value=" + trackIndex + ".");
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
        writeLog("adds a place tile: " + placeTile);
    }

    public void removePlaceTile(PlaceTile placeTile) {
        this.placeTiles.remove(placeTile);
        writeLog("removes a place tile: " + placeTile);
    }

    public Collection<TokenLocation> getTradingStationLocations() {
        return tradingStationLocations;
    }

    public void addTradingStationToCurrentLocation() {
        if (tradingStationLocations.size() != getMaxAllowableTradingStations()) {
            tradingStationLocations.add(merchantLocation);
            writeLog("adds a trading station to " + merchantLocation);
        } else {
            throw new IllegalStateException("Out of trading stations");
        }
    }

    public void removeTradingStationFromLocation(TokenLocation location) {
        if (tradingStationLocations.contains(location)) {
            tradingStationLocations.remove(location);
            writeLog("removes a trading station from " + location);
        } else {
            throw new IllegalArgumentException("trading station doesn't exist at " + location);
        }
    }

    public int getTradingStationCount() {
        return tradingStationLocations.size();
    }

    private int maxAllowableTradingStations = 10;

    public int getMaxAllowableTradingStations() {
        return maxAllowableTradingStations;
    }

    public void decrementMaxAllowableTradingStations() {
        this.maxAllowableTradingStations--;
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
                    writeLog("draws " + follower + " from her bag and adds to her market (slot " + slot + ")");
                }
            } else {
                writeLog("cannot draw more followers since her market is full!");
                break;
            }
        }
    }

    public Follower removeFromMarket(int slot) {
        Follower follower = market.remove(slot);
        if (follower != null) {
            writeLog("removes " + follower + " from her market");
        } else {
            writeLog("tried to remove a follower from an empty slot in her market. slot= " + slot);
        }
        return follower;
    }

    public void connectLog(Loggable log) {
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
        writeLog("adds a TechTile to " + actionType + " on slot " + techPosition);
    }

    public void removeTechTile(ActionType actionType) {
        techTileMap.remove(actionType);
        writeLog("removes a TechTile from " + actionType + ".");
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

        if (followerToken.getFollowerType() == FollowerType.Scholar && techTileMap.containsKey(ActionType.School)) {
            followerToken.addAlias(FollowerType.Craftsman);
            followerToken.addAlias(FollowerType.Trader);
            followerToken.addAlias(FollowerType.Farmer);
            followerToken.addAlias(FollowerType.Boatman);
            followerToken.addAlias(FollowerType.Knight);
        }

        if (followerToken.getFollowerType() == FollowerType.Boatman && techTileMap.containsKey(ActionType.HerbGarden)) {
            followerToken.addAlias(FollowerType.Craftsman);
            followerToken.addAlias(FollowerType.Trader);
            followerToken.addAlias(FollowerType.Farmer);
        }

        return followerTrack.canAdd(followerToken, actionSlot);
    }

    public void addTokenToAction(ActionType actionType, int actionSlot, Follower followerToken) {
        FollowerTrack followerTrack = plans.get(actionType);
        followerTrack.add(followerToken, actionSlot);
        writeLog("adds " + followerToken + " to action: " + actionType + ". slot:" + actionSlot);
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

    protected void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    protected void setPlayerColor(PlayerColor playerColor) {
        this.playerColor = playerColor;
    }

    protected void setCoinCount(int coinCount) {
        this.coinCount = coinCount;
    }

    protected void setActionHelper(ActionHelper actionHelper) {
        this.actionHelper = actionHelper;
    }

    protected Map<ActionType, FollowerTrack> jsonUnmarshalPlans(String json, ObjectMapper mapper) throws JsonProcessingException {
        TypeReference<HashMap<ActionType, FollowerTrack>> plansRef = new TypeReference<>() {
        };
        return mapper.readValue(json, plansRef);
    }

    public boolean isBeingTortured() {
        return beingTortured;
    }

    public void setBeingTortured(boolean beingTortured) {
        this.beingTortured = beingTortured;
    }

}
