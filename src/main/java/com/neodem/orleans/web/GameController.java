package com.neodem.orleans.web;

import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        String gameId = uuid.toString();

        GameState gameState = gameMaster.makeGame(gameId, names, GameVersion.Original);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/nextPhase")
    public GameState nextPhase(@PathVariable(value = "gameId") String gameId) {
        GameState gameState = gameMaster.nextPhase(gameId);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/plan")
    public GameState submitPlan(
            @PathVariable(value = "gameId") String gameId,
            @PathVariable(value = "playerId") String playerId,
            @RequestParam(value = "action") String action,
            @RequestParam(value = "marketSlot") int marketSlot,
            @RequestParam(value = "actionSlot") int actionSlot
    ) {

        ActionType actionType;
        try {
            actionType = ActionType.valueOf(action);
        } catch (IllegalArgumentException e) {
            // TODO
            throw e;
        }

        GameState gameState = gameMaster.addToPlan(gameId, playerId, actionType, marketSlot, actionSlot);
        return gameState;
    }

    private FollowerType getFollowerType(String valueString) {
        FollowerType followerType;
        try {
            followerType = FollowerType.valueOf(valueString);
        } catch (IllegalArgumentException e) {
            // TODO
            throw e;
        }
        return followerType;
    }

    @RequestMapping("/game/{gameId}/{playerId}/planSet")
    public GameState planSet(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId) {
        GameState gameState = gameMaster.planSet(gameId, playerId);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/gameState")
    public GameState gameState(@PathVariable(value = "gameId") String gameId) {
        GameState gameState = gameMaster.getGameState(gameId);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/action")
    public GameState doAction(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId, @RequestParam(value = "action") String action, @RequestParam Map<String, String> allParams) {
        ActionType actionType = null;
        try {
            actionType = ActionType.valueOf(action);
        } catch (IllegalArgumentException e) {
            // TODO
        }

        Map<AdditionalDataType, String> additionalDataMap = new HashMap<>();
        if (allParams != null) {
            for (String key : allParams.keySet()) {
                AdditionalDataType type = null;
                try {
                    type = AdditionalDataType.valueOf(key);
                } catch (IllegalArgumentException e) {
                }
                if (type != null) {
                    additionalDataMap.put(type, allParams.get(key));
                }
            }
        }

        GameState gameState = gameMaster.doAction(gameId, playerId, actionType, additionalDataMap);
        return gameState;
    }

    @RequestMapping("/game/{gameId}/{playerId}/pass")
    public GameState passActionPhase(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId) {
        GameState gameState = gameMaster.pass(gameId, playerId);
        return gameState;
    }

}
