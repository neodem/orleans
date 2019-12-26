package com.neodem.orleans.service;

import com.neodem.orleans.objects.Bag;
import com.neodem.orleans.objects.GoodType;
import com.neodem.orleans.objects.PlayerState;
import com.neodem.orleans.objects.Token;
import com.neodem.orleans.objects.TokenLocation;
import com.neodem.orleans.objects.Track;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class PlayerStateTest {
    private PlayerState playerState;

    @BeforeEach
    void setUp() {
        playerState = new PlayerState("someone");
    }

    @AfterEach
    void tearDown() {
        playerState = null;
    }

    @Test
    void initShouldInitProperly() {
        assertThat(playerState.getPlayerId()).isEqualTo("someone");
        assertThat(playerState.getCoinCount()).isEqualTo(5);
        assertThat(playerState.getTokenLocation()).isEqualTo(TokenLocation.Orleans);
        assertThat(playerState.getTradingStationCount()).isEqualTo(10);
        assertThat(playerState.getTradingStationLocations()).isEmpty();
        assertThat(playerState.getPlaceTiles()).isEmpty();
        Bag<Token> bag = playerState.getBag();
        assertThat(bag).hasSize(4);
        assertThat(bag).contains(Token.StarterBoatmen, Token.StarterCraftsman, Token.StarterFarmer, Token.StarterTrader);

        Map<GoodType, Integer> goodCounts = playerState.getGoodCounts();
        assertThat(goodCounts.get(GoodType.Grain)).isEqualTo(0);
        assertThat(goodCounts.get(GoodType.Cheese)).isEqualTo(0);
        assertThat(goodCounts.get(GoodType.Wool)).isEqualTo(0);
        assertThat(goodCounts.get(GoodType.Wine)).isEqualTo(0);
        assertThat(goodCounts.get(GoodType.Brocade)).isEqualTo(0);

        assertThat(playerState.getTrackValue(Track.Farmers)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Craftsmen)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Traders)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Boatmen)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Knights)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Scholars)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Development)).isEqualTo(0);

    }
}
