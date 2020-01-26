package com.neodem.orleans.web;

import com.neodem.orleans.engine.core.GameMaster;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GameVersion;
import com.neodem.orleans.engine.core.model.TortureType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "gameController", description = "the Game Engine API")
@RestController
@RequestMapping("/game")
public class GameController {

    @Resource
    private GameMaster gameMaster;

    @Operation(summary = "Initialize Game", description = "Init a new game", tags = { "gameController" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GameState.class))))
    })
    @RequestMapping("/init")
    public GameState gameInit(@Parameter(description="Comma Seperated list of player names for the game (2-4) allowed")  @RequestParam(value = "playerNames") List<String> names) {
        UUID uuid = UUID.randomUUID();
        String gameId = uuid.toString();

        GameState gameState = gameMaster.makeGame(gameId, names, GameVersion.Original);
        return gameState;
    }

    @RequestMapping("/{gameId}/startGame")
    public GameState startGame(@PathVariable(value = "gameId") String gameId) {
        GameState gameState = gameMaster.startGame(gameId);
        return gameState;
    }

    @RequestMapping("/{gameId}/{playerId}/plan")
    public GameState submitPlan(
            @PathVariable(value = "gameId") String gameId,
            @PathVariable(value = "playerId") String playerId,
            @RequestParam(value = "action") String action,
            @RequestParam(value = "marketSlot") int marketSlot,
            @RequestParam(value = "actionSlot") int actionSlot
    ) {
        GameState gameState = gameMaster.addToPlan(gameId, playerId, ActionType.valueOf(action), marketSlot, actionSlot);
        return gameState;
    }

    @RequestMapping("/{gameId}/{playerId}/action")
    public GameState doAction(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId, @RequestParam(value = "action") String action, @RequestParam Map<String, String> allParams) {
        Map<AdditionalDataType, String> additionalDataMap = convertAdditionalDataMap(allParams, "action");
        GameState gameState = gameMaster.doAction(gameId, playerId, ActionType.valueOf(action), additionalDataMap);
        return gameState;
    }

    @RequestMapping("/{gameId}/{playerId}/pass")
    public GameState passActionPhase(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId) {
        GameState gameState = gameMaster.pass(gameId, playerId);
        return gameState;
    }

    // player needs to submit one per coin owed.. game will not proceed until all coins are accounted for
    @RequestMapping("/{gameId}/{playerId}/torture")
    public GameState torturePlan(@PathVariable(value = "gameId") String gameId, @PathVariable(value = "playerId") String playerId, @RequestParam(value = "tortureType") String tortureTypeString, @RequestParam Map<String, String> allParams) {
        TortureType tortureType = TortureType.valueOf(tortureTypeString);
        Map<AdditionalDataType, String> additionalDataMap = convertAdditionalDataMap(allParams, "tortureType");
        GameState gameState = gameMaster.torturePlan(gameId, playerId, tortureType, additionalDataMap);
        return gameState;
    }

    @RequestMapping("/{gameId}/gameState")
    public GameState gameState(@PathVariable(value = "gameId") String gameId) {
        GameState gameState = gameMaster.getGameState(gameId);
        return gameState;
    }

    private Map<AdditionalDataType, String> convertAdditionalDataMap(@RequestParam Map<String, String> allParams, String skipKey) {
        Map<AdditionalDataType, String> additionalDataMap = new HashMap<>();
        if (allParams != null) {
            for (String key : allParams.keySet()) {
                if (key.equals(skipKey)) continue;
                AdditionalDataType type = AdditionalDataType.valueOf(key);
                additionalDataMap.put(type, allParams.get(key));
            }
        }
        return additionalDataMap;
    }
}
