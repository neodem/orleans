package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.Util;
import com.neodem.orleans.engine.core.BenefitTracker;
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
 * Created on 12/26/19
 */
public abstract class GameState implements Loggable {
    protected final List<PlayerState> players = new ArrayList<>();
    protected BoardState boardState;
    protected BenefitTracker benefitTracker;

    protected Map<GoodType, Integer> goodsInventory = new HashMap<>();
    protected Map<FollowerType, Integer> followerInventory = new HashMap<>();

    protected int techTilesAvailable = 0;
    protected Collection<PlaceTile> placeTiles1 = new HashSet<>();
    protected Collection<PlaceTile> placeTiles2 = new HashSet<>();
    // TODO hide this from JSON
    protected List<HourGlassTile> hourGlassTileStack = new ArrayList<>();
    protected List<HourGlassTile> usedHourGlassTiles = new ArrayList<>();
    protected List<String> gameLog = new ArrayList<>();
    private int playerCount;
    private final Collection<CitizenType> claimedCitizens = new HashSet<>();
    protected String gameId;
    protected int round;
    protected GamePhase gamePhase;
    protected int startPlayer = -1;

    protected HourGlassTile currentHourGlass;
    int currentActionPlayerIndex = 0;

    protected GameState(JsonNode node) {
        ObjectMapper mapper = new ObjectMapper();

        try {
            JsonNode players = node.get("players");
            if (players != null) {
                for (JsonNode player : players) {
                    this.players.add(makePlayerFromJson(player));
                }
            }
            this.boardState = makeBoardStateFromJson(node.get("boardState"));
            this.benefitTracker = makeBenefitTrackerFromJson(node.get("benefitTracker"));

            JsonNode goodsInInventoryNode = node.get("goodsInventory");
            if (goodsInInventoryNode != null) {
                TypeReference<HashMap<GoodType, Integer>> goodCountsRef = new TypeReference<>() {
                };
                this.goodsInventory = mapper.readValue(goodsInInventoryNode.toString(), goodCountsRef);
            }

            TypeReference<HashMap<FollowerType, Integer>> followerInventoryRef = new TypeReference<>() {
            };
            this.followerInventory = mapper.readValue(node.get("followerInventory").toString(), followerInventoryRef);

            TypeReference<ArrayList<PlaceTile>> placeTileRef = new TypeReference<>() {
            };
            this.placeTiles1 = mapper.readValue(node.get("placeTiles1").toString(), placeTileRef);
            this.placeTiles2 = mapper.readValue(node.get("placeTiles2").toString(), placeTileRef);

            TypeReference<ArrayList<HourGlassTile>> usedHourGlassTilesRef = new TypeReference<>() {
            };
            this.usedHourGlassTiles = mapper.readValue(node.get("usedHourGlassTiles").toString(), usedHourGlassTilesRef);
            this.hourGlassTileStack = mapper.readValue(node.get("hourGlassStack").toString(), usedHourGlassTilesRef);

            this.gameLog = mapper.readValue(node.get("gameLog").toString(), List.class);
            this.playerCount = node.get("playerCount").intValue();

            this.gameId = node.get("gameId").textValue();
            this.round = node.get("round").intValue();
            this.gamePhase = GamePhase.fromValue(node.get("gamePhase").textValue());

            String startPlayer = node.get("startPlayer").textValue();
            int i = 0;
            for (PlayerState playerState : this.players) {
                if (startPlayer.equals(playerState.getPlayerId())) break;
                i++;
            }
            this.startPlayer = i;

            String currentHourGlass = node.get("currentHourGlass").textValue();
            if (currentHourGlass != null) {
                this.currentHourGlass = HourGlassTile.fromValue(node.get("currentHourGlass").textValue());
            }
            this.techTilesAvailable = node.get("numberTechTilesAvailable").intValue();

            String currentActionPlayer = node.get("currentActionPlayer").textValue();
            if (currentActionPlayer != null) {
                i = 0;
                for (PlayerState playerState : this.players) {
                    if (currentActionPlayer.equals(playerState.getPlayerId())) break;
                    i++;
                }
                this.currentActionPlayerIndex = i;
            }

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    protected abstract BenefitTracker makeBenefitTrackerFromJson(JsonNode benefitTracker);

    protected abstract BoardState makeBoardStateFromJson(JsonNode boardState);

    protected abstract PlayerState makePlayerFromJson(JsonNode player);

    public GameState(String gameId, int playerCount) {
        Assert.isTrue(playerCount > 1 && playerCount < 5, "playerCount should be 2-4");
        this.gameId = gameId;
        this.playerCount = playerCount;
        initGame(playerCount);
    }

    protected void initForPlayerCount(int playerCount) {
        if (playerCount == 2) initFor2Players();
        else if (playerCount == 3) initFor3Players();
        else initFor4Players();
    }

    protected abstract void initFor4Players();

    protected abstract void initFor3Players();

    protected abstract void initFor2Players();

    public BenefitTracker getBenefitTracker() {
        return benefitTracker;
    }

    public int getNumberTechTilesAvailable() {
        return techTilesAvailable;
    }

    public void removeTechTileFromInventory() {
        techTilesAvailable--;
    }

    public Map<TokenLocation, Collection<String>> getAllTradingStations() {
        Map<TokenLocation, Collection<String>> allTradingStations = new HashMap<>();

        for (PlayerState player : players) {
            Collection<TokenLocation> locsForPlayer = player.getTradingStationLocations();
            for (TokenLocation location : locsForPlayer) {
                Collection<String> names = allTradingStations.get(location);
                if (names == null) names = new HashSet<>();
                names.add(player.getPlayerId());
                allTradingStations.put(location, names);
            }
        }

        return allTradingStations;
    }

    @Override
    public void writeLine(String line) {
        gameLog.add(line);
    }

    public void removeFollowerFromInventory(FollowerType desiredFollower) {
        int followerCount = followerInventory.get(desiredFollower);
        followerInventory.put(desiredFollower, --followerCount);
    }

    public void removeGoodFromInventory(GoodType goodType) {
        Util.mapDec(goodsInventory, goodType);
    }

    public HourGlassTile getCurrentHourGlass() {
        return currentHourGlass;
    }

    public void setCurrentHourGlass(HourGlassTile currentHourGlass) {
        this.currentHourGlass = currentHourGlass;
        writeLine("HourGlass changed to: " + currentHourGlass);
    }

    public abstract void initGame(int playerCount);

    public String getStartPlayer() {
        return players.get(startPlayer).getPlayerId();
    }


    public int getPlayerCount() {
        return playerCount;
    }

    public Map<FollowerType, Integer> getFollowerInventory() {
        return followerInventory;
    }

    public List<String> getGameLog() {
        return gameLog;
    }

    /**
     * determine who has the most farmers. In a tie, we return null
     *
     * @return
     */
    public String mostFarmers() {
        String maxId = null;
        int max = -1;
        int tie = -1;

        for (PlayerState player : players) {
            int index = player.getTrackLocation(Track.Farmers);
            if (index == max) {
                tie = index;
            }
            if (index > max) {
                max = index;
                maxId = player.getPlayerId();
            }
        }

        if (tie == max) return null;

        return maxId;
    }

    /**
     * determine who has the least farmers. In a tie, we return null. In a 2p game, we return null
     *
     * @return
     */
    public String leastFarmers() {
        if (playerCount == 2) return null;

        String minId = null;
        int min = 100;
        int tie = -1;

        for (PlayerState player : players) {
            int index = player.getTrackLocation(Track.Farmers);
            if (index == min) {
                tie = index;
            }
            if (index < min) {
                min = index;
                minId = player.getPlayerId();
            }
        }

        if (tie == min) return null;

        return minId;
    }

    public String getGameId() {
        return gameId;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
        writeLine("Round " + round + " started");
    }

    public GamePhase getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(GamePhase gamePhase) {
        this.gamePhase = gamePhase;
        writeLine("Phase: " + gamePhase);
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerState player) {
        if (players.contains(player)) {
            throw new IllegalArgumentException("Player " + player.getPlayerId() + " is in the game already!");
        }
        player.addLog(this);
        this.players.add(player);
    }

    public BoardState getBoardState() {
        return boardState;
    }

    public Map<GoodType, Integer> getGoodsInventory() {
        return goodsInventory;
    }

    public Collection<PlaceTile> getPlaceTiles1() {
        return placeTiles1;
    }

    public Collection<PlaceTile> getPlaceTiles2() {
        return placeTiles2;
    }

    public List<HourGlassTile> getHourGlassStack() {
        return hourGlassTileStack;
    }

    public List<HourGlassTile> getUsedHourGlassTiles() {
        return usedHourGlassTiles;
    }

    public PlayerState getPlayer(String playerId) {
        for (PlayerState playerState : players) {
            if (playerState.getPlayerId().equals(playerId)) return playerState;
        }
        return null;
    }

    public boolean isCitizenClaimed(CitizenType citizenType) {
        return claimedCitizens.contains(citizenType);
    }

    public void citizenClaimed(CitizenType citizenType) {
        claimedCitizens.add(citizenType);
    }

    public String getCurrentActionPlayer() {
        return players.get(currentActionPlayerIndex).getPlayerId();
    }

    public void advancePlayer() {
        startPlayer++;
        if (startPlayer == players.size()) startPlayer = 0;
        writeLine("Start Player set to: " + getStartPlayer());
    }

    public void syncActionPlayer() {
        this.currentActionPlayerIndex = startPlayer;
    }

    public void advanceActionPlayer() {
        int count = 1;
        do {
            currentActionPlayerIndex++;
            if (currentActionPlayerIndex == playerCount) currentActionPlayerIndex = 0;
            count++;
        } while (players.get(currentActionPlayerIndex).isPhaseComplete() && count == playerCount);
    }

    public boolean isGoodAvailable(GoodType goodType) {
        return goodsInventory.get(goodType) > 0;
    }

    public void addGoodToInventory(GoodType goodType) {
        Util.mapInc(goodsInventory, goodType);
    }

    public Collection<String> getTradingStationOwners(TokenLocation location) {
        Map<TokenLocation, Collection<String>> allTradingStations = getAllTradingStations();
        return allTradingStations.get(location);
    }

    public boolean isPhaseComplete() {
        boolean complete = true;
        for (PlayerState playerState : players) {
            complete = complete && playerState.isPhaseComplete();
        }
        return complete;
    }
}
