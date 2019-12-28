package com.neodem.orleans.config;

import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.original.OriginalActionHelper;
import com.neodem.orleans.engine.original.OriginalGameMaster;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
@Configuration
public class ServicesConfig {

    @Bean
    public ActionHelper actionService() {
        return new OriginalActionHelper();
    }

    @Bean
    public GameMaster gameMaster(ActionHelper actionHelper) {
        return new OriginalGameMaster(actionHelper);
    }
}
