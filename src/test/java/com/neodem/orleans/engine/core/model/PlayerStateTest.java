package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.neodem.orleans.engine.original.OriginalActionHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/10/20
 */
public class PlayerStateTest {
    private PlayerState playerState;

    private class TestablePlayerState extends PlayerState {

        public TestablePlayerState() {
            super("Joe", PlayerColor.Blue, new OriginalActionHelper());
        }

        @Override
        protected void initState() {
        }
    }

    @BeforeEach
    void setUp() {
        playerState = new TestablePlayerState();
    }

    @Test
    void jsonUnmarshalPlansShouldUnmarshalPlans() throws JsonProcessingException {
        String plansJson = "{\"FarmHouse\":{\"track\":[{\"expectedType\":\"Boatman\",\"followerInSlot\":{\"followerType\":\"StarterBoatman\",\"dba\":[\"Boatman\"],\"followerType\":\"StarterBoatman\"}},{\"expectedType\":\"Craftsman\",\"followerInSlot\":null}],\"maxSize\":2,\"filledSpotsCount\":1,\"full\":false}}";
        Map<ActionType, FollowerTrack> plans = playerState.jsonUnmarshalPlans(plansJson, new ObjectMapper());
        assertThat(plans).isNotNull();
        assertThat(plans.get(ActionType.FarmHouse)).isNotNull();
    }
}
