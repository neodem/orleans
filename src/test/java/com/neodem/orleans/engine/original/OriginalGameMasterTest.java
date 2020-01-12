package com.neodem.orleans.engine.original;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.HourGlassTile;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.model.OriginalGameState;
import com.neodem.orleans.service.GameStateService;
import com.neodem.orleans.service.OriginalGameStateInMemoryService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/6/20
 */
public class OriginalGameMasterTest {
    private OriginalGameMaster originalGameMaster;
    private GameStateService<OriginalGameState> gameStateService;

    @BeforeEach
    void setUp() {
        gameStateService = mock(OriginalGameStateInMemoryService.class);
        originalGameMaster = new OriginalGameMaster(new OriginalActionHelper(), gameStateService);
    }

    @AfterEach
    void tearDown() {
        originalGameMaster = null;
        gameStateService = null;
    }

    @Test
    void initShouldCreateValidJson() throws JsonProcessingException {
        GameState gameState = originalGameMaster.makeGame("test", Lists.newArrayList("joe", "sally"), GameVersion.Original);
        when(gameStateService.gameStateExists("test")).thenReturn(false);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(gameState);
        assertThat(json).isNotNull();
    }

    @Test
    void doCensusPhaseShouldTriggerTorture() {
        OriginalGameState gameState = originalGameMaster.makeGame("test", Lists.newArrayList("joe", "sally", "mike"), GameVersion.Original);

        PlayerState joe = gameState.getPlayer("joe");
        joe.setTrackIndex(Track.Farmers, 0);
        int joeCoinCount = joe.getCoinCount();
        assertThat(joeCoinCount).isGreaterThan(0);
        joe.removeCoin(joeCoinCount);

        PlayerState mike = gameState.getPlayer("mike");
        mike.setTrackIndex(Track.Farmers, 2);

        PlayerState sally = gameState.getPlayer("sally");
        sally.setTrackIndex(Track.Farmers, 4);
        int sallyCoinCount = sally.getCoinCount();
        assertThat(sallyCoinCount).isGreaterThan(0);

        boolean phaseComplete = originalGameMaster.doCensusPhase(gameState);

        assertThat(phaseComplete).isFalse();
        assertThat(joe.isBeingTortured()).isTrue();
        assertThat(sally.getCoinCount()).isEqualTo(sallyCoinCount + 1);
        assertThat(joe.getCoinCount()).isEqualTo(-1);
    }

    @Test
    void doEventPhaseShouldTriggerTortureForTaxes() {
        OriginalGameState gameState = originalGameMaster.makeGame("test", Lists.newArrayList("joe", "sally"), GameVersion.Original);

        // joe should be on the hook for 2 coins of taxes, he has 0
        PlayerState joe = gameState.getPlayer("joe");
        int joeCoinCount = joe.getCoinCount();
        assertThat(joeCoinCount).isGreaterThan(0);
        joe.removeCoin(joeCoinCount);
        joe.addGood(GoodType.Grain);
        joe.addGood(GoodType.Grain);
        joe.addGood(GoodType.Grain);
        joe.addGood(GoodType.Grain);
        joe.addGood(GoodType.Grain);
        joe.addGood(GoodType.Grain);

        PlayerState sally = gameState.getPlayer("sally");
        sally.addCoin(100);

        gameState.setCurrentHourGlass(HourGlassTile.Taxes);

        boolean phaseComplete = originalGameMaster.doEventPhase(gameState);

        assertThat(phaseComplete).isFalse();
        assertThat(joe.isBeingTortured()).isTrue();
        assertThat(joe.getCoinCount()).isEqualTo(-2);
    }

    @Test
    void doEventPhaseShouldTriggerTortureForHarvest() {
        OriginalGameState gameState = originalGameMaster.makeGame("test", Lists.newArrayList("joe", "sally"), GameVersion.Original);

        // joe should be on the hook for 5 coins for harvest, he has 0
        PlayerState joe = gameState.getPlayer("joe");
        int joeCoinCount = joe.getCoinCount();
        assertThat(joeCoinCount).isGreaterThan(0);
        joe.removeCoin(joeCoinCount);
        assertThat(joe.getFullGoodCount()).isEqualTo(0);

        PlayerState sally = gameState.getPlayer("sally");
        sally.addGood(GoodType.Wine);
        sally.addGood(GoodType.Wine);

        gameState.setCurrentHourGlass(HourGlassTile.Harvest);

        boolean phaseComplete = originalGameMaster.doEventPhase(gameState);

        assertThat(phaseComplete).isFalse();
        assertThat(joe.isBeingTortured()).isTrue();
        assertThat(joe.getCoinCount()).isEqualTo(-5);
    }

}
