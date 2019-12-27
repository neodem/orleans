package com.neodem.orleans.web;

import com.neodem.orleans.objects.GameState;
import com.neodem.orleans.objects.GameVersion;
import com.neodem.orleans.service.GameMaster;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
@RestController
public class GameController {

    @Resource
    private GameMaster gameMaster;

    @RequestMapping("/game/init")
    public GameState greeting(@RequestParam(value = "playerNames") List<String> names) {
        UUID uuid = UUID.randomUUID();
        String gameId = uuid.toString();

        GameState gameState = gameMaster.makeGame(gameId, names, GameVersion.Original);
        return gameState;
    }

}
