package com.neodem.orleans.service;

import com.google.common.collect.Lists;
import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.model.ActionType.Village;
import static com.neodem.orleans.model.Follower.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class BaseActionServiceTest {
    private BaseActionService actionService;

    private class TestableBaseActionService extends BaseActionService {
        public TestableBaseActionService(Map<ActionType, Grouping<Follower>> actionMappings) {
            super(actionMappings);
        }
    }


    @BeforeEach
    void setUp() {
        Map<ActionType, Grouping<Follower>> actionMappings = new HashMap<>();
        actionMappings.put(Village, new Grouping<>(Boatman, Craftsman, Farmer));
        actionService = new TestableBaseActionService(actionMappings);
    }

    @AfterEach
    void tearDown() {
        actionService = null;
    }

    @Test
    void canPlaceShouldFailIfFilledSpot() {
        List<Follower> followersToPlace = Lists.newArrayList(Boatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Boatman);
        assertThat(actionService.canPlace(Village, followersToPlace, placedAlready)).isFalse();
    }

    @Test
    void canPlaceShouldPassIfAllCorrect() {
        List<Follower> followersToPlace = Lists.newArrayList(Boatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Craftsman);
        assertThat(actionService.canPlace(Village, followersToPlace, placedAlready)).isTrue();
    }

    @Test
    void canPlaceShouldNotMindStarters() {
        List<Follower> followersToPlace = Lists.newArrayList(StarterBoatman);
        List<Follower> placedAlready = Lists.newArrayList(Farmer, Craftsman);
        assertThat(actionService.canPlace(Village, followersToPlace, placedAlready)).isTrue();
    }
}

