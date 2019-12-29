package com.neodem.orleans.engine.core.model;

import com.google.common.collect.Lists;
import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.ActionHelperBase;
import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.original.model.PlaceTile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.ActionType.FarmHouse;
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
        private Map<ActionType, Grouping<Follower>> actionMappings;

        public void setActionMappings(Map<ActionType, Grouping<Follower>> actionMappings) {
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
        actionMappings.put(FarmHouse, new Grouping<>(Boatman, Craftsman));
        actionService = new TestableActionHelperBase();
        ((TestableActionHelperBase) actionService).setActionMappings(actionMappings);
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

    @Test
    void actionIsFullShouldWorkWithNoTechOrMonks() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Craftsman), null);
        assertThat(result).isFalse();

        result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Boatman, Craftsman), null);
        assertThat(result).isTrue();

        result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Craftsman, Boatman), null);
        assertThat(result).isTrue();
    }

    @Test
    void actionIsFullShouldWorkWithMonks() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Craftsman, Monk), null);
        assertThat(result).isTrue();
    }

    @Test
    void actionIsFullShouldWorkWithStarters() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(StarterCraftsman, Boatman), null);
        assertThat(result).isTrue();
    }

    @Test
    void actionIsFullShouldWorkWithTechToken() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Boatman), Craftsman);
        assertThat(result).isTrue();
    }

    @Test
    void actionIsFullShouldWorkWithTechTokenAndMonk() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Monk), Craftsman);
        assertThat(result).isTrue();
    }

    @Test
    void actionIsFullShouldNotAllowDuplicates() {
        boolean result = actionService.actionIsFull(FarmHouse, Lists.newArrayList(Boatman), Boatman);
        assertThat(result).isFalse();
    }

    @Test
    void actionCanAcceptShouldWorkWithNoMonksOrStarters() {
        boolean result = actionService.actionCanAccept(Village, Lists.newArrayList(Farmer));
        assertThat(result).isTrue();

        result = actionService.actionCanAccept(Village, Lists.newArrayList(Boatman, Farmer));
        assertThat(result).isTrue();

        result = actionService.actionCanAccept(Village, Lists.newArrayList(Boatman, Craftsman, Farmer));
        assertThat(result).isTrue();

        result = actionService.actionCanAccept(Village, Lists.newArrayList(Boatman, Knight, Farmer));
        assertThat(result).isFalse();
    }

    @Test
    void actionCanAcceptShouldWorkWithStarters() {
        boolean result = actionService.actionCanAccept(Village, Lists.newArrayList(Boatman, StarterCraftsman, Farmer));
        assertThat(result).isTrue();
    }

    @Test
    void actionCanAcceptShouldWorkWithMonks() {
        boolean result = actionService.actionCanAccept(Village, Lists.newArrayList(Boatman, Monk, Farmer));
        assertThat(result).isTrue();
    }

    /*
        @Override
    public boolean actionCanAccept(ActionType actionType, List<Follower> followers) {
        Assert.notNull(actionType, "actionType may not be null");
        Assert.notNull(followers, "followers may not be null");

        List<Follower> sanitizedFollowers = sanitizeFollowers(followers);
        List<Follower> monksRemoved = removeMonks(sanitizedFollowers);

        Grouping<Follower> neededFollowers = actionMappings().get(actionType);
        Grouping<Follower> testFollowers = new Grouping<>(monksRemoved);
        return testFollowers.canFitInto(neededFollowers);
    }
     */
}

