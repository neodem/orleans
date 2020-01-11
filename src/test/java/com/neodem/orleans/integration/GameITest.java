package com.neodem.orleans.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.engine.core.model.*;
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

import java.util.Collection;
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
    public void initShouldSucceed() {
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
    public void nextPhaseAfterStartGameShouldBePlanning() {
        String gameId = startTwoPlayerGame();
        GameState gameState;

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
    public void firstPlayerShouldBeAbleToPlanFarmHouseAfterStartGame() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

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
    public void secondPlayerShouldBeAbleToPlanCastleAfterInit() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

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
    public void twoRoundsInAGame() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

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

        sendForPlayer(gameId, P1, "pass");
        sendForPlayer(gameId, P2, "pass");
    }

    @Test
    public void villageUniversityMonasaryAndTownHallShouldWork() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P1, "plan", "action", "Village", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterCraftsman), "actionSlot", "2");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterCraftsman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        gameState = getGameState(gameId);

        // do Village with Trader : should add trader, bump track and give level 1 piece
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Traders)).isEqualTo(0);

        sendForPlayer(gameId, P1, "action", "action", "Village", "follower", "Trader", "placeTile", "Hayrick");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Traders)).isEqualTo(1);
        assertThat(gameState.getPlayer(P1).getBag()).contains(new Follower(FollowerType.Trader), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman));
        assertThat(gameState.getPlayer(P1).getPlaceTiles()).contains(Hayrick);

        // do University
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Development)).isEqualTo(0);

        sendForPlayer(gameId, P2, "action", "action", "University");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getTrackValue(Track.Development)).isEqualTo(1);
        assertThat(gameState.getPlayer(P2).getBag()).contains(new Follower(FollowerType.Scholar), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterCraftsman), new Follower(FollowerType.StarterTrader));

        sendForPlayer(gameId, P1, "pass");
        sendForPlayer(gameId, P2, "pass");

        // plan phase 2
        gameState = getGameState(gameId);

        sendForPlayer(gameId, P2, "plan", "action", "Monastery", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.Scholar), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Monastery", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterTrader), "actionSlot", "1");
        sendForPlayer(gameId, P2, "planSet");

        sendForPlayer(gameId, P1, "plan", "action", "TownHall", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.Trader), "actionSlot", "0");
        sendForPlayer(gameId, P1, "planSet");

        // do Monastery
        sendForPlayer(gameId, P2, "action", "action", "Monastery");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getBag()).contains(new Follower(FollowerType.Monk));

        // do town hall
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Development)).isEqualTo(0);

        sendForPlayer(gameId, P1, "action", "action", "TownHall", "benefit1", "Canalisation", "takeDevPoint", "true");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Development)).isEqualTo(1);
    }

    @Test
    public void scriptoriumAndShipShouldWork() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

        runBasicStart(gameId, gameState);

        // plan phase 3
        gameState = getGameState(gameId);

        sendForPlayer(gameId, P1, "plan", "action", "Scriptorium", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.Knight), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "Scriptorium", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.Scholar), "actionSlot", "1");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "plan", "action", "Ship", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Ship", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "Ship", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.Knight), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        // test the scriptorium
        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Development)).isEqualTo(1);

        sendForPlayer(gameId, P1, "action", "action", "Scriptorium");

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getTrackValue(Track.Development)).isEqualTo(2);

        // test ship
        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getMerchantLocation()).isEqualTo(TokenLocation.Orleans);

        Path path = gameState.getBoardState().getPathBetween(new PathBetween(TokenLocation.Orleans, TokenLocation.Briare), PathType.Sea);
        Collection<GoodType> goods = path.getGoods();
        GoodType goodOnPath = goods.iterator().next();
        int previousCount = gameState.getPlayer(P2).getGoodCount(goodOnPath);

        sendForPlayer(gameId, P2, "action", "action", "Ship", "from", "Orleans", "to", "Briare", "good", goodOnPath.name());

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getMerchantLocation()).isEqualTo(TokenLocation.Briare);
        assertThat(gameState.getPlayer(P2).getGoodCount(goodOnPath)).isEqualTo(previousCount + 1);
    }

    @Test
    public void wagonAndGuildhallTest() {
        String gameId = startTwoPlayerGame();
        GameState gameState = getGameState(gameId);

        runBasicStart(gameId, gameState);

        // plan phase 3
        gameState = getGameState(gameId);

        sendForPlayer(gameId, P1, "plan", "action", "Wagon", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "Wagon", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterTrader), "actionSlot", "1");
        sendForPlayer(gameId, P1, "plan", "action", "Wagon", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.Knight), "actionSlot", "2");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "plan", "action", "GuildHall", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "GuildHall", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterCraftsman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "GuildHall", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.Knight), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        // test the Wagon
        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getMerchantLocation()).isEqualTo(TokenLocation.Orleans);

        Path path = gameState.getBoardState().getPathBetween(new PathBetween(TokenLocation.Orleans, TokenLocation.Montargis), PathType.Land);
        Collection<GoodType> goods = path.getGoods();
        GoodType goodOnPath = goods.iterator().next();
        int previousCount = gameState.getPlayer(P1).getGoodCount(goodOnPath);

        sendForPlayer(gameId, P1, "action", "action", "Wagon", "from", "Orleans", "to", "Montargis", "good", goodOnPath.name());

        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P1).getMerchantLocation()).isEqualTo(TokenLocation.Montargis);
        assertThat(gameState.getPlayer(P1).getGoodCount(goodOnPath)).isEqualTo(previousCount + 1);

        // test the GuildHall
        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getTradingStationLocations()).doesNotContain(TokenLocation.Orleans);
        sendForPlayer(gameId, P2, "action", "action", "GuildHall");
        gameState = getGameState(gameId);
        assertThat(gameState.getPlayer(P2).getTradingStationLocations()).contains(TokenLocation.Orleans);
    }

    // helpers

    /**
     * will run a game where each player does Castle then University for their first 2 phases.
     *
     * @param gameId
     * @param gameState
     */
    private void runBasicStart(String gameId, GameState gameState) {
        sendForPlayer(gameId, P1, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P1, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterBoatman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "Castle", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        sendForPlayer(gameId, P1, "action", "action", "Castle");
        sendForPlayer(gameId, P2, "action", "action", "Castle");

        sendForPlayer(gameId, P1, "pass");
        sendForPlayer(gameId, P2, "pass");

        gameState = getGameState(gameId);

        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterCraftsman), "actionSlot", "1");
        sendForPlayer(gameId, P2, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P2, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P2, "planSet");

        sendForPlayer(gameId, P1, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterFarmer), "actionSlot", "0");
        sendForPlayer(gameId, P1, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterCraftsman), "actionSlot", "1");
        sendForPlayer(gameId, P1, "plan", "action", "University", "marketSlot", "" + findSlotInMarket(gameState, P1, FollowerType.StarterTrader), "actionSlot", "2");
        sendForPlayer(gameId, P1, "planSet");

        sendForPlayer(gameId, P2, "action", "action", "University");
        sendForPlayer(gameId, P1, "action", "action", "University");

        sendForPlayer(gameId, P2, "pass");
        sendForPlayer(gameId, P1, "pass");
    }

    private String startTwoPlayerGame() {
        GameState gameState = send("/game/init", "playerNames", P1 + "," + P2);
        String gameId = gameState.getGameId();
        send("/game/" + gameId + "/startGame");
        return gameId;
    }

    private int findSlotInMarket(GameState gameState, String playerId, FollowerType followerType) {
        PlayerState player = gameState.getPlayer(playerId);
        Market market = player.getMarket();
        Follower[] marketSlots = market.getMarket();
        for (int i = 0; i < marketSlots.length; i++) {
            if (followerType == marketSlots[i].getFollowerType()) return i;
        }

        String msg = String.format("player %s has no follower of type: %s in their market: %s", playerId, followerType, market);
        throw new IllegalArgumentException(msg);
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
