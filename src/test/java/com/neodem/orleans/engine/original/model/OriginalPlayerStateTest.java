package com.neodem.orleans.engine.original.model;

import com.google.common.collect.Sets;
import com.neodem.orleans.collections.Bag;
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
import java.util.Map;

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
        assertThat(playerState.getTradingStationCount()).isEqualTo(10);
        assertThat(playerState.getTradingStationLocations()).isEmpty();
        assertThat(playerState.getPlaceTiles()).isEmpty();
        Bag<Follower> bag = playerState.getBag();
        assertThat(bag).hasSize(4);
        Collection<Follower> test = Sets.newHashSet(new Follower(FollowerType.StarterBoatman), new Follower(FollowerType.StarterCraftsman), new Follower(FollowerType.StarterFarmer), new Follower(FollowerType.StarterTrader));
        for (Follower follower : bag) {
            assertThat(test).contains(follower);
            test.remove(follower);
        }

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
