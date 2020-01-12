package com.neodem.orleans.config;

import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.original.OriginalActionHelper;
import com.neodem.orleans.engine.original.OriginalGameMaster;
import com.neodem.orleans.service.GameStateService;
import com.neodem.orleans.service.OriginalGameStateInMemoryService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
@Configuration
public class ServicesConfig {

    @Bean
    public ActionHelper actionHelper() {
        return new OriginalActionHelper();
    }

    @Bean
    public GameStateService gameStateService() {
        return new OriginalGameStateInMemoryService();
    }

    @Bean
    public GameMaster gameMaster(ActionHelper actionHelper, GameStateService gameStateService) {
        return new OriginalGameMaster(actionHelper, gameStateService);
    }
}
