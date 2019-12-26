package com.neodem.orleans.service;

import com.neodem.orleans.objects.GamePhase;
import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.GoodType;
import com.neodem.orleans.objects.HourGlassTile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.neodem.orleans.objects.PlaceTile.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class GameStateTest {
    private GameState gameState;

    @BeforeEach
    void setUp() {
        gameState = new GameState("gameId", null);
    }

    @AfterEach
    void tearDown() {
        gameState = null;
    }

    @Test
    void initShouldWork() {
        assertThat(gameState.getGameId()).isEqualTo("gameId");
        assertThat(gameState.getRound()).isEqualTo(1);
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.HourGlass);
        assertThat(gameState.getPlayers()).hasSize(0);

        Map<GoodType, Integer> goodsInventory = gameState.getGoodsInventory();
        // 5 good types
        assertThat(goodsInventory).hasSize(5);
        int totalGoods = 0;
        for (GoodType type : goodsInventory.keySet()) {
            totalGoods += goodsInventory.get(type);
        }
        assertThat(totalGoods).isEqualTo(90);

        assertThat(gameState.getPlaceTiles1()).hasSize(12);
        assertThat(gameState.getPlaceTiles1()).contains(Hayrick, WoolManufacturer, CheeseFactory, Winery, Brewery, Sacristy, HerbGarden, Bathhouse, Windmill, Library, Hospital, TailorShop);

        assertThat(gameState.getPlaceTiles2()).hasSize(8);
        assertThat(gameState.getPlaceTiles2()).contains(GunpowderTower, Cellar, Office, School, Pharmacy, HorseWagon, ShippingLine, Laboratory);

        assertThat(gameState.getUsedHourGlassTiles()).hasSize(0);
        assertThat(gameState.getHourGlassStack()).hasSize(18);
        assertThat(gameState.getHourGlassStack().get(0)).isEqualTo(HourGlassTile.Pilgrimage);
    }
}
