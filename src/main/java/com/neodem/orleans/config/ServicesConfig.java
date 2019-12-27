package com.neodem.orleans.config;

import com.neodem.orleans.service.DefaultGameMaster;
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
    public GameMaster gameMaster() {
        return new DefaultGameMaster();
    }
}
