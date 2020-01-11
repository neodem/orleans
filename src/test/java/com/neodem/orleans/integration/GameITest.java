package com.neodem.orleans.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerTrack;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GamePhase;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.HourGlassTile;
import com.neodem.orleans.engine.core.model.Market;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.OriginalGameState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.engine.original.model.PlaceTile.*;
import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/10/20
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameITest {

    public static final String P1 = "Amanda";
    public static final String P2 = "Vincent";

    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders headers = new HttpHeaders();
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void initShouldFailInitAGameWithNoPlayers() {
        String url = createURLWithPort("/game/init");
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
        Map<String, String> params = new HashMap<>();
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class, params);
        assertThat(response.getStatusCode().is4xxClientError()).isTrue();
    }

    @Test
    public void initShouldSucceed() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);

        assertThat(gameState).isNotNull();
        assertThat(gameState.getGameId()).isNotNull();
        assertThat(gameState.getRound()).isEqualTo(0);
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Setup);

        List<PlayerState> players = gameState.getPlayers();
        assertThat(players).hasSize(2);
        assertThat(players).extracting(p -> p.getPlayerId()).contains(P1, P2);

        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Farmers)).isEqualTo(0);
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Farmers)).isEqualTo(0);

        assertThat(gameState.getPlayer(P1).getBag()).contains(new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman), new Follower(FollowerType.StarterTrader));
        assertThat(gameState.getPlayer(P2).getBag()).contains(new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman), new Follower(FollowerType.StarterTrader));

        Map<GoodType, Integer> goodsInventory = gameState.getGoodsInventory();
        // 5 good types
        assertThat(goodsInventory).hasSize(5);
        int totalGoods = 0;
        for (GoodType type : goodsInventory.keySet()) {
            totalGoods += goodsInventory.get(type);
        }
        // reflects 90- stuff assigned to board
        assertThat(totalGoods).isEqualTo(35);

        assertThat(gameState.getPlaceTiles1()).hasSize(12);
        assertThat(gameState.getPlaceTiles1()).contains(Hayrick, WoolManufacturer, CheeseFactory, Winery, Brewery, Sacristy, HerbGarden, Bathhouse, Windmill, Library, Hospital, TailorShop);

        assertThat(gameState.getPlaceTiles2()).hasSize(8);
        assertThat(gameState.getPlaceTiles2()).contains(GunpowderTower, Cellar, Office, School, Pharmacy, HorseWagon, ShippingLine, Laboratory);

        assertThat(gameState.getUsedHourGlassTiles()).hasSize(0);
        assertThat(gameState.getHourGlassStack()).hasSize(18);
        assertThat(gameState.getHourGlassStack().get(0)).isEqualTo(HourGlassTile.Pilgrimage);
    }

    @Test
    public void nextPhaseAfterStartGameShouldBePlanning() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);
        String gameId = gameState.getGameId();
        send("/game/" + gameId + "/startGame");

        gameState = getGameState(gameId);

        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Planning);

        assertThat(gameState.getStartPlayer()).isEqualTo(P1);
        assertThat(gameState.getCurrentActionPlayer()).isEqualTo(P1);

        assertThat(gameState.getPlayer(P1).getBag()).isEmpty();
        assertThat(gameState.getPlayer(P2).getBag()).isEmpty();

        assertThat(marketContains(gameState, P1, FollowerType.StarterBoatman, FollowerType.StarterCraftsman, FollowerType.StarterFarmer, FollowerType.StarterTrader)).isTrue();
        assertThat(marketContains(gameState, P2, FollowerType.StarterBoatman, FollowerType.StarterCraftsman, FollowerType.StarterFarmer, FollowerType.StarterTrader)).isTrue();
    }

    @Test
    public void firstPlayerShouldBeAbleToPlanFarmHouseAfterStartGame() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);
        String gameId = gameState.getGameId();
        send("/game/" + gameId + "/startGame");

        gameState = getGameState(gameId);

        int boatmanSlot = findSlotInMarket(gameState, P1, FollowerType.StarterBoatman);
        int craftsmanSlot = findSlotInMarket(gameState, P1, FollowerType.StarterCraftsman);

        sendForPlayer(gameId, P1, "plan", "action", "FarmHouse", "marketSlot", "" + boatmanSlot, "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "FarmHouse", "marketSlot", "" + craftsmanSlot, "actionSlot", "1");

        gameState = getGameState(gameId);

        Map<ActionType, FollowerTrack> bobsplans = gameState.getPlayer(P1).getPlans();
        FollowerTrack track = bobsplans.get(ActionType.FarmHouse);

        assertThat(track).isNotNull();
        assertThat(track.getFilledSpotsCount()).isEqualTo(2);
        assertThat(track.isReady(null)).isTrue();
    }

    @Test
    public void secondPlayerShouldBeAbleToPlanCastleAfterInit() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);
        String gameId = gameState.getGameId();
        send("/game/" + gameId + "/startGame");

        gameState = getGameState(gameId);

        int boatmanSlot = findSlotInMarket(gameState, P2, FollowerType.StarterBoatman);
        int traderSlot = findSlotInMarket(gameState, P2, FollowerType.StarterTrader);
        int farmerSlot = findSlotInMarket(gameState, P2, FollowerType.StarterFarmer);

        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + farmerSlot, "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + boatmanSlot, "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + traderSlot, "actionSlot", "2");

        gameState = getGameState(gameId);

        Map<ActionType, FollowerTrack> plans = gameState.getPlayer(P2).getPlans();
        FollowerTrack track = plans.get(ActionType.Castle);

        assertThat(track).isNotNull();
        assertThat(track.getFilledSpotsCount()).isEqualTo(3);
        assertThat(track.isReady(null)).isTrue();
    }

    @Test
    public void aSampleGame() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);
        String gameId = gameState.getGameId();
        send("/game/" + gameId + "/startGame");
        gameState = getGameState(gameId);

        sendForPlayer(gameId, P1, "plan", "action", "FarmHouse", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterBoatman), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "FarmHouse", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterCraftsman), "actionSlot", "1");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        gameState = getGameState(gameId);

        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Actions);

        // do bobs FarmHouse
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Farmers)).isEqualTo(0);
        assertThat(gameState.getPlayer(P1).getGoodCount(GoodType.Grain)).isEqualTo(0);

        sendForPlayer(gameId, P1, "action", "action", "FarmHouse");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Farmers)).isEqualTo(1);
        assertThat(gameState.getPlayer(P1).getGoodCount(GoodType.Grain)).isEqualTo(1);
        assertThat(gameState.getPlayer(P1).getBag()).contains(new Follower(FollowerType.Farmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman));

        // do tonys Castle
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Knights)).isEqualTo(0);

        sendForPlayer(gameId, P2, "action", "action", "Castle");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Knights)).isEqualTo(1);
        assertThat(gameState.getPlayer(P2).getBag()).contains(new Follower(FollowerType.Knight), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterTrader));

        sendForPlayer(gameId, P1, "pass");
        sendForPlayer(gameId, P2, "pass");

        gameState = getGameState(gameId);
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Planning);

        // check market of each player.. should be filled properly!
        assertThat(marketContains(gameState, P1, FollowerType.Farmer, FollowerType.StarterBoatman, FollowerType.StarterCraftsman, FollowerType.StarterFarmer, FollowerType.StarterTrader)).isTrue();
        assertThat(marketContains(gameState, P2, FollowerType.Knight, FollowerType.StarterBoatman, FollowerType.StarterCraftsman, FollowerType.StarterFarmer, FollowerType.StarterTrader)).isTrue();

        assertThat(gameState.getStartPlayer()).isEqualTo(P2);

        sendForPlayer(gameId, P2, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterCraftsman), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterCraftsman), "actionSlot", "2");
        sendForPlayer(gameId, P1, "planSet");

        gameState = getGameState(gameId);

        // do tonys Village : boatman should add boatman, +2 coints and bump boat track to 1
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Boatmen)).isEqualTo(0);
        int coinCountTony = gameState.getPlayer(P2).getCoinCount();

        sendForPlayer(gameId, P2, "action", "action", "Village", "follower", "Boatman");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Boatmen)).isEqualTo(1);
        assertThat(gameState.getPlayer(P2).getBag()).contains(new Follower(FollowerType.Boatman), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman));
        assertThat(marketContains(gameState, P2, FollowerType.Knight, FollowerType.StarterTrader)).isTrue();
        assertThat(gameState.getPlayer(P2).getCoinCount()).isEqualTo(coinCountTony + 2);

        // do bobs Village : Craftsman should add chraftsman and a tech tile
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Craftsmen)).isEqualTo(0);

        sendForPlayer(gameId, P1, "action", "action", "Village", "follower", "Craftsman", "techAction", "Castle", "position", "0");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Craftsmen)).isEqualTo(1);
        assertThat(gameState.getPlayer(P1).getTechTileSlot(ActionType.Castle)).isEqualTo(0);
        assertThat(gameState.getPlayer(P1).getBag()).contains(new Follower(FollowerType.Craftsman), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman));
        assertThat(marketContains(gameState, P1, FollowerType.Farmer, FollowerType.StarterTrader)).isTrue();
    }

    // helpers

    private int findSlotInMarket(GameState gameState, String playerId, FollowerType type) {
        PlayerState player = gameState.getPlayer(playerId);
        Market market = player.getMarket();
        Follower[] marketSlots = market.getMarket();
        for (int i = 0; i < marketSlots.length; i++) {
            if (type == marketSlots[i].getFollowerType()) return i;
        }
        return -1;
    }

    private GameState sendForPlayer(String gameId, String playerId, String command, String... params) {
        return send("/game/" + gameId + "/" + playerId + "/" + command, params);
    }

    private GameState send(String uri, String... params) {
        String url = createURLWithPort(uri);
        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url);

        if (params != null) {
            for (int i = 0; i < params.length - 1; i = i + 2) {
                builder.queryParam(params[i], params[i + 1]);
            }
        }
        String urlString = builder.toUriString();
        ResponseEntity<String> response = restTemplate.exchange(urlString, HttpMethod.GET, requestEntity, String.class);
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        String json = response.getBody();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readValue(json, JsonNode.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new OriginalGameState(jsonNode);
    }

    private GameState getGameState(String gameId) {
        return send("/game/" + gameId + "/gameState");
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    /**
     * return true if players market contains exactly these followers.
     *
     * @param gameState
     * @param playerId
     * @param followerTypes
     * @return
     */
    private boolean marketContains(GameState gameState, String playerId, FollowerType... followerTypes) {
        PlayerState player = gameState.getPlayer(playerId);
        Market market = player.getMarket();
        Follower[] actualMarket = market.getMarket();
        if (followerTypes.length != market.getFilledSlotCount()) return false;
        for (FollowerType followerType : followerTypes) {
            boolean result = false;
            for (Follower f : actualMarket) {
                if (f.getFollowerType() == followerType) {
                    result = true;
                    break;
                }
            }
            if (!result) return false;
        }

        return true;
    }
}
