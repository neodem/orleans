package com.neodem.orleans.engine.core.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class MarketTest {
    private Market market;

    @BeforeEach
    void setUp() {
        market = new Market();
    }

    @AfterEach
    void tearDown() {
        market = null;
    }

    /*    public void init(int marketSize) {
            this.marketSize = marketSize;
            this.availableSlots = marketSize;
            market = new Follower[marketSize];
            for (int i = 0; i < marketSize; i++) market[i] = EMPTY;
        }

     */
    @Test
    void marketInitsWithEightAvailableSlots() {
        assertThat(market.getAvailableSlots()).isEqualTo(8);
    }

    @Test
    void marketInitsCorrectly() {
        market.init(8);
        assertThat(market.getAvailableSlots()).isEqualTo(8);
        assertThat(market.getMarketSize()).isEqualTo(8);
    }

    @Test
    void maketHasSpaceShouldReturnTrueAfterInit() {
        assertThat(market.hasSpace()).isTrue();
    }

}

