package com.neodem.orleans.config;

import com.neodem.orleans.service.ActionService;
import com.neodem.orleans.service.OriginalActionService;
import com.neodem.orleans.service.OriginalGameMaster;
import com.neodem.orleans.service.GameMaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
@Configuration
public class ServicesConfig {

    @Bean
    public ActionService actionService() {
        return new OriginalActionService();
    }

    @Bean
    public GameMaster gameMaster(ActionService actionService) {
        return new OriginalGameMaster(actionService);
    }
}
