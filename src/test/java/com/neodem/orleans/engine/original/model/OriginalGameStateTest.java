package com.neodem.orleans.engine.original.model;

import com.neodem.orleans.engine.core.model.GamePhase;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.HourGlassTile;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.neodem.orleans.engine.original.model.PlaceTile.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalGameStateTest {

    @Test
    void gamesMayNotHave1Player() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OriginalGameState("gameId", 1));
    }

    @Test
    void gamesMayNotHave5Players() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new OriginalGameState("gameId", 5));
    }

    @Test
    void initShouldWork() {
        OriginalGameState gameState = new OriginalGameState("gameId", 4);

        assertThat(gameState.getGameId()).isEqualTo("gameId");
        assertThat(gameState.getRound()).isEqualTo(0);
        assertThat(gameState.getGamePhase()).isEqualTo(GamePhase.Setup);
        assertThat(gameState.getPlayers()).hasSize(0);

        Map<GoodType, Integer> goodsInventory = gameState.getGoodsInventory();
        // 5 good types
        assertThat(goodsInventory).hasSize(5);
        int totalGoods = 0;
        for (GoodType type : goodsInventory.keySet()) {
            totalGoods += goodsInventory.get(type);
        }
        // reflects 90-47 (assigned to board)
        assertThat(totalGoods).isEqualTo(43);

        assertThat(gameState.getPlaceTiles1()).hasSize(12);
        assertThat(gameState.getPlaceTiles1()).contains(Hayrick, WoolManufacturer, CheeseFactory, Winery, Brewery, Sacristy, HerbGarden, Bathhouse, Windmill, Library, Hospital, TailorShop);

        assertThat(gameState.getPlaceTiles2()).hasSize(8);
        assertThat(gameState.getPlaceTiles2()).contains(GunpowderTower, Cellar, Office, School, Pharmacy, HorseWagon, ShippingLine, Laboratory);

        assertThat(gameState.getUsedHourGlassTiles()).hasSize(0);
        assertThat(gameState.getHourGlassStack()).hasSize(18);
        assertThat(gameState.getHourGlassStack().get(0)).isEqualTo(HourGlassTile.Pilgrimage);
    }


}
