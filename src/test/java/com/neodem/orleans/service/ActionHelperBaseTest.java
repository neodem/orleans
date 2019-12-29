package com.neodem.orleans.service;

import com.google.common.collect.Lists;
import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.ActionHelperBase;
import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.original.model.PlaceTile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.ActionType.Village;
import static com.neodem.orleans.engine.core.model.Follower.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class ActionHelperBaseTest {
    private ActionHelperBase actionService;

    private class TestableActionHelperBase extends ActionHelperBase {
        private final Map<ActionType, Grouping<Follower>> actionMappings;

        public TestableActionHelperBase(Map<ActionType, Grouping<Follower>> actionMappings) {
            this.actionMappings = actionMappings;
        }

        @Override
        protected Map<ActionType, Grouping<Follower>> actionMappings() {
            return actionMappings;
        }

        @Override
        protected Map<ActionType, ActionProcessor> actionProcessors() {
            return null;
        }

        @Override
        protected Map<ActionType, PlaceTile> placeTileMap() {
            return null;
        }
    }


    @BeforeEach
    void setUp() {
        Map<ActionType, Grouping<Follower>> actionMappings = new HashMap<>();
        actionMappings.put(Village, new Grouping<>(Boatman, Craftsman, Farmer));
        actionService = new TestableActionHelperBase(actionMappings);
    }

    @AfterEach
    void tearDown() {
        actionService = null;
    }

    @Test
    void canPlaceShouldFailIfFilledSpot() {
        List<Follower> followersToPlace = Lists.newArrayList(Boatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Boatman);
        assertThat(actionService.canPlaceIntoAction(Village, followersToPlace, placedAlready)).isFalse();
    }

    @Test
    void canPlaceShouldPassIfAllCorrect() {
        List<Follower> followersToPlace = Lists.newArrayList(Boatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Craftsman);
        assertThat(actionService.canPlaceIntoAction(Village, followersToPlace, placedAlready)).isTrue();
    }

    @Test
    void canPlaceShouldNotMindStarters() {
        List<Follower> followersToPlace = Lists.newArrayList(StarterBoatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Craftsman);
        assertThat(actionService.canPlaceIntoAction(Village, followersToPlace, placedAlready)).isTrue();
    }
}

