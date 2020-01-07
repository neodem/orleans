package com.neodem.orleans.engine.original;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 1/6/20
 */
public class OriginalGameMasterTest {
    private OriginalGameMaster originalGameMaster;

    @BeforeEach
    void setUp() {
        originalGameMaster = new OriginalGameMaster(new OriginalActionHelper());
    }

    @AfterEach
    void tearDown() {
        originalGameMaster = null;
    }

    @Test
    void initShouldCreateValidJson() throws JsonProcessingException {
        GameState gameState = originalGameMaster.makeGame("test", Lists.newArrayList("joe", "sally"), GameVersion.Original);
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(gameState);
        assertThat(json).isNotNull();
    }
}
