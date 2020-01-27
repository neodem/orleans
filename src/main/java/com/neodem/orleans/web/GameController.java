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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
//@Tag(name = "gameController", description = "the Game Engine API")
@RestController
@RequestMapping("/game")
public class GameController {

    @Resource
    private GameMaster gameMaster;

    @Operation(summary = "Initialize Game", description = "Init a new game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = GameState.class))))
    })
    @RequestMapping(value = "/init", method = RequestMethod.GET)
    public GameState gameInit(
            @Parameter(description = "Comma Seperated list of player names for the game (2-4) allowed") @RequestParam(value = "playerNames") List<String> names
    ) {
        UUID uuid = UUID.randomUUID();
        String gameId = uuid.toString();

        GameState gameState = gameMaster.makeGame(gameId, names, GameVersion.Original);
        return gameState;
    }

    @Operation(summary = "Start a Game", description = "Start a game that has been previously initialized")
    @RequestMapping(value = "/{gameId}/startGame", method = RequestMethod.GET)
    public GameState startGame(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId
    ) {
        GameState gameState = gameMaster.startGame(gameId);
        return gameState;
    }

    @Operation(summary = "Submit a plan for a player", description = "On their turn a player may submit as many plans as they can 'afford' each call moves one follower from their market to the desired action spot")
    @RequestMapping(value = "/{gameId}/{playerId}/plan", method = RequestMethod.GET)
    public GameState submitPlan(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId,
            @Parameter(description = "the playerId") @PathVariable(value = "playerId") String playerId,
            @Parameter(description = "the action to move the follower to") @RequestParam(value = "action") String action,
            @Parameter(description = "the market slot to move the follower from (0 based)") @RequestParam(value = "marketSlot") int marketSlot,
            @Parameter(description = "the action slot to move the follower to (0 based)") @RequestParam(value = "actionSlot") int actionSlot
    ) {
        GameState gameState = gameMaster.addToPlan(gameId, playerId, ActionType.valueOf(action), marketSlot, actionSlot);
        return gameState;
    }

    @Operation(summary = "execute an action", description = "When allowed, a player may execte an action")
    @RequestMapping(value = "/{gameId}/{playerId}/action", method = RequestMethod.GET)
    public GameState doAction(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId,
            @Parameter(description = "the playerId") @PathVariable(value = "playerId") String playerId,
            @Parameter(description = "the action to execute") @RequestParam(value = "action") String action,
            @Parameter(description = "some actions require extra params to be added for context") @RequestParam Map<String, String> allParams
    ) {
        Map<AdditionalDataType, String> additionalDataMap = convertAdditionalDataMap(allParams, "action");
        GameState gameState = gameMaster.doAction(gameId, playerId, ActionType.valueOf(action), additionalDataMap);
        return gameState;
    }

    @Operation(summary = "player pass", description = "When done submitting plans/torture, the player can and should pass")
    @RequestMapping(value = "/{gameId}/{playerId}/pass", method = RequestMethod.GET)
    public GameState passActionPhase(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId,
            @Parameter(description = "the playerId") @PathVariable(value = "playerId") String playerId
    ) {
        GameState gameState = gameMaster.pass(gameId, playerId);
        return gameState;
    }

    @Operation(summary = "Submit a torture for a player", description = "When a player owes coins, they must submit one torture plan per coin owed")
    @RequestMapping(value = "/{gameId}/{playerId}/torture", method = RequestMethod.GET)
    public GameState torturePlan(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId,
            @Parameter(description = "the playerId") @PathVariable(value = "playerId") String playerId,
            @Parameter(description = "the type of torture to endure") @RequestParam(value = "tortureType") String tortureTypeString,
            @Parameter(description = "some tortures require extra params to be added for context") @RequestParam Map<String, String> allParams
    ) {
        TortureType tortureType = TortureType.valueOf(tortureTypeString);
        Map<AdditionalDataType, String> additionalDataMap = convertAdditionalDataMap(allParams, "tortureType");
        GameState gameState = gameMaster.torturePlan(gameId, playerId, tortureType, additionalDataMap);
        return gameState;
    }

    @Operation(summary = "get current game state", description = "This will return the game state at this moment in time")
    @RequestMapping(value = "/{gameId}/gameState", method = RequestMethod.GET)
    public GameState gameState(
            @Parameter(description = "the gameId") @PathVariable(value = "gameId") String gameId
    ) {
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
