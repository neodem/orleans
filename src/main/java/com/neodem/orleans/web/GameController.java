package com.neodem.orleans.web;

import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.GameMaster;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
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
    public GameState gameInit(@RequestParam(value = "playerNames") List<String> names) {
        UUID uuid = UUID.randomUUID();
//        String gameId = uuid.toString();

        String gameId = "test";

        GameState gameState = gameMaster.makeGame(gameId, names, GameVersion.Original);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/nextPhase")
    public GameState gameStart(@PathVariable(value = "gameId") String gameId) {
        GameState gameState = gameMaster.nextPhase(gameId);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/plan")
    public GameState submitPlan(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId, @RequestParam(value = "action") String action, @RequestParam(value = "followers") List<String> followers) {

        ActionType actionType = null;
        try {
            actionType = ActionType.valueOf(action);
        } catch (IllegalArgumentException e) {
            // TODO
        }

        List<Follower> followerTypes = new ArrayList<>(followers.size());
        for (String follower : followers) {
            Follower followerType;
            try {
                followerType = Follower.valueOf(follower);
                followerTypes.add(followerType);
            } catch (IllegalArgumentException e) {
                // TODO
            }
        }

        GameState gameState = gameMaster.addToPlan(gameId, playerId, actionType, followerTypes);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/planSet")
    public GameState submitPlan(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId) {
        GameState gameState = gameMaster.planSet(gameId, playerId);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/action")
    public GameState doAction(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId, @RequestParam(value = "action") String action) {
        ActionType actionType = null;
        try {
            actionType = ActionType.valueOf(action);
        } catch (IllegalArgumentException e) {
            // TODO
        }

        GameState gameState = gameMaster.doAction(gameId, playerId, actionType);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/pass")
    public GameState passActionPhase(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId) {
        GameState gameState = gameMaster.pass(gameId, playerId);
        return gameState;
    }

}
