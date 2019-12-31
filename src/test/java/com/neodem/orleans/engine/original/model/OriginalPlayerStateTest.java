package com.neodem.orleans.engine.original.model;

import com.google.common.collect.Sets;
import com.neodem.orleans.collections.RandomBag;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerColor;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.core.model.Track;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalPlayerStateTest {
    private OriginalPlayerState playerState;

    @BeforeEach
    void setUp() {
        playerState = new OriginalPlayerState("someone", PlayerColor.Blue, null);
    }

    @AfterEach
    void tearDown() {
        playerState = null;
    }

    @Test
    void initShouldInitProperly() {
        assertThat(playerState.getPlayerId()).isEqualTo("someone");
        assertThat(playerState.getPlayerColor()).isEqualTo(PlayerColor.Blue);
        assertThat(playerState.getCoinCount()).isEqualTo(5);
        assertThat(playerState.getMerchantLocation()).isEqualTo(TokenLocation.Orleans);
        assertThat(playerState.tradingStationMax()).isEqualTo(10);
        assertThat(playerState.getTradingStationLocations()).isEmpty();
        RandomBag<Follower> bag = playerState.getBag();
        assertThat(bag).hasSize(4);
        Collection<Follower> test = Sets.newHashSet(new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterTrader));
        for (Follower follower : bag) {
            assertThat(test).contains(follower);
            test.remove(follower);
        }

        assertThat(playerState.getGoodCount(GoodType.Grain)).isEqualTo(0);
        assertThat(playerState.getGoodCount(GoodType.Cheese)).isEqualTo(0);
        assertThat(playerState.getGoodCount(GoodType.Wool)).isEqualTo(0);
        assertThat(playerState.getGoodCount(GoodType.Wine)).isEqualTo(0);
        assertThat(playerState.getGoodCount(GoodType.Brocade)).isEqualTo(0);

        assertThat(playerState.getTrackValue(Track.Farmers)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Craftsmen)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Traders)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Boatmen)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Knights)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Scholars)).isEqualTo(0);
        assertThat(playerState.getTrackValue(Track.Development)).isEqualTo(0);

    }
}
