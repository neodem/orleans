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
        GameState gameState = send("/game/init", "playerNames", "Bob,Tony");
        assertThat(gameState).isNotNull();
        assertThat(gameState.getGameId()).isNotNull();
        assertThat(gameState.getRound()).isEqualTo(0);
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Setup);

        List<PlayerState> players = gameState.getPlayers();
        assertThat(players).hasSize(2);
        assertThat(players).extracting(p -> p.getPlayerId()).contains("Bob", "Tony");

        PlayerState bob = gameState.getPlayer("Bob");
        assertThat(bob.getTrackValue(Track.Farmers)).isEqualTo(0);

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
    public void nextPhaseAfterInitShouldTakeToPlanning() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", "Bob,Tony");
        String gameId = gameState.getGameId();
        gameState = send("/game/" + gameId + "/nextPhase");
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Planning);
    }

    @Test
    public void firstPlayerShouldBeAbleToPlanFarmHouseAfterInit() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", "Bob,Tony");
        String gameId = gameState.getGameId();
        gameState = send("/game/" + gameId + "/nextPhase");

        int boatmanSlot = findSlotInMarket(gameState, "Bob", FollowerType.StarterBoatman);
        int craftsmanSlot = findSlotInMarket(gameState, "Bob", FollowerType.StarterCraftsman);

        send("/game/" + gameId + "/Bob/plan", "action", "FarmHouse", "marketSlot", "" + boatmanSlot, "actionSlot", "0");
        gameState = send("/game/" + gameId + "/Bob/plan", "action", "FarmHouse", "marketSlot", "" + craftsmanSlot, "actionSlot", "1");

        Map<ActionType, FollowerTrack> bobsplans = gameState.getPlayer("Bob").getPlans();
        FollowerTrack track = bobsplans.get(ActionType.FarmHouse);

        assertThat(track).isNotNull();
        assertThat(track.getFilledSpotsCount()).isEqualTo(2);
        assertThat(track.isReady(null)).isTrue();
    }

    @Test
    public void secondPlayerShouldBeAbleToPlanCastleAfterInit() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", "Bob,Tony");
        String gameId = gameState.getGameId();
        gameState = send("/game/" + gameId + "/nextPhase");

        int boatmanSlot = findSlotInMarket(gameState, "Tony", FollowerType.StarterBoatman);
        int traderSlot = findSlotInMarket(gameState, "Tony", FollowerType.StarterTrader);
        int farmerSlot = findSlotInMarket(gameState, "Tony", FollowerType.StarterFarmer);

        send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + farmerSlot, "actionSlot", "0");
        send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + boatmanSlot, "actionSlot", "1");
        gameState = send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + traderSlot, "actionSlot", "2");

        Map<ActionType, FollowerTrack> plans = gameState.getPlayer("Tony").getPlans();
        FollowerTrack track = plans.get(ActionType.Castle);

        assertThat(track).isNotNull();
        assertThat(track.getFilledSpotsCount()).isEqualTo(3);
        assertThat(track.isReady(null)).isTrue();
    }

    @Test
    public void afterPlanningWeGetToActions() throws JsonProcessingException {
        GameState gameState = send("/game/init", "playerNames", "Bob,Tony");
        String gameId = gameState.getGameId();
        gameState = send("/game/" + gameId + "/nextPhase");
        send("/game/" + gameId + "/Bob/plan", "action", "FarmHouse", "marketSlot", "" + findSlotInMarket(gameState, "Bob", FollowerType.StarterBoatman), "actionSlot", "0");
        send("/game/" + gameId + "/Bob/plan", "action", "FarmHouse", "marketSlot", "" + findSlotInMarket(gameState, "Bob", FollowerType.StarterCraftsman), "actionSlot", "1");
        send("/game/" + gameId + "/Bob/planSet");
        send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, "Tony", FollowerType.StarterFarmer), "actionSlot", "0");
        send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, "Tony", FollowerType.StarterBoatman), "actionSlot", "1");
        send("/game/" + gameId + "/Tony/plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, "Tony", FollowerType.StarterTrader), "actionSlot", "2");
        send("/game/" + gameId + "/Tony/planSet");
        gameState = send("/game/" + gameId + "/nextPhase");

        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Actions);

        // do bobs FarmHouse
        assertThat(gameState.getPlayer("Bob").getTrackValue(Track.Farmers)).isEqualTo(0);
        assertThat(gameState.getPlayer("Bob").getGoodCount(GoodType.Grain)).isEqualTo(0);

        gameState = send("/game/" + gameId + "/Bob/action", "action", "FarmHouse");

        assertThat(gameState.getPlayer("Bob").getTrackValue(Track.Farmers)).isEqualTo(1);
        assertThat(gameState.getPlayer("Bob").getGoodCount(GoodType.Grain)).isEqualTo(1);

        // do tonys Castle
        assertThat(gameState.getPlayer("Tony").getTrackValue(Track.Knights)).isEqualTo(0);
        gameState = send("/game/" + gameId + "/Tony/action", "action", "Castle");
        assertThat(gameState.getPlayer("Tony").getTrackValue(Track.Knights)).isEqualTo(1);
    }


    private int findSlotInMarket(GameState gameState, String playerId, FollowerType type) {
        PlayerState player = gameState.getPlayer(playerId);
        Market market = player.getMarket();
        Follower[] marketSlots = market.getMarket();
        for (int i = 0; i < marketSlots.length - 1; i++) {
            if (type == marketSlots[i].getFollowerType()) return i;
        }
        return -1;
    }

    private GameState send(String uri, String... params) throws JsonProcessingException {
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
        JsonNode jsonNode = objectMapper.readValue(json, JsonNode.class);

        return new OriginalGameState(jsonNode);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }
}
