package com.neodem.orleans.engine.original;

import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionHelperBase;
import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.actions.CoinBumpProcessor;
import com.neodem.orleans.engine.core.actions.DevelopmentBumpProcessor;
import com.neodem.orleans.engine.core.actions.GoodsBumpProcessor;
import com.neodem.orleans.engine.core.actions.MovementProcessor;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerTrack;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PathType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.actions.*;
import com.neodem.orleans.engine.original.model.PlaceTile;

import java.util.HashMap;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.ActionType.*;
import static com.neodem.orleans.engine.core.model.FollowerType.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class OriginalActionHelper extends ActionHelperBase implements ActionHelper {

    private final Map<ActionType, FollowerTrack> actionMappings = new HashMap<>();
    private final Map<ActionType, ActionProcessor> actionProcessors = new HashMap<>();
    private final Map<ActionType, PlaceTile> placeTileMap = new HashMap<>();

    @Override
    protected Map<ActionType, FollowerTrack> actionMappings() {
        return actionMappings;
    }

    @Override
    protected Map<ActionType, ActionProcessor> actionProcessors() {
        return actionProcessors;
    }

    @Override
    protected Map<ActionType, PlaceTile> placeTileMap() {
        return placeTileMap;
    }

    public OriginalActionHelper() {
        actionMappings.put(FarmHouse, new FollowerTrack(Boatman, Craftsman));
        actionMappings.put(Village, new FollowerTrack(Farmer, Boatman, Craftsman));
        actionMappings.put(University, new FollowerTrack(Farmer, Craftsman, Trader));
        actionMappings.put(Castle, new FollowerTrack(Farmer, Boatman, Trader));
        actionMappings.put(Scriptorium, new FollowerTrack(Knight, Scholar));

        FollowerTrack townHallTrack = new FollowerTrack(Any, Any);
        townHallTrack.setReadyWhenNotFull(true);
        actionMappings.put(TownHall, townHallTrack);

        actionMappings.put(Monastery, new FollowerTrack(Scholar, Trader));
        actionMappings.put(Ship, new FollowerTrack(Farmer, Boatman, Knight));
        actionMappings.put(Wagon, new FollowerTrack(Farmer, Trader, Knight));
        actionMappings.put(GuildHall, new FollowerTrack(Farmer, Craftsman, Knight));

        //places
        actionMappings.put(ShippingLine, new FollowerTrack(Boatman));
        actionMappings.put(Brewery, new FollowerTrack(Monk));
        actionMappings.put(Hayrick, new FollowerTrack(Farmer));
        actionMappings.put(Sacristy, new FollowerTrack(Monk));
        actionMappings.put(WoolManufacturer, new FollowerTrack(Boatman, Trader));
        actionMappings.put(CheeseFactory, new FollowerTrack(Farmer, Farmer));
        actionMappings.put(Hospital, new FollowerTrack(Boatman, Scholar));
        actionMappings.put(TailorShop, new FollowerTrack(Trader, Scholar));
        actionMappings.put(Windmill, new FollowerTrack(Craftsman, Craftsman));
        actionMappings.put(Library, new FollowerTrack(Knight, Scholar));
        actionMappings.put(Office, new FollowerTrack(Scholar, Scholar));
        actionMappings.put(Cellar, new FollowerTrack(Monk, Scholar));
        actionMappings.put(Laboratory, new FollowerTrack(Craftsman, Scholar));
        actionMappings.put(HorseWagon, new FollowerTrack(Knight, Trader));
        actionMappings.put(Winery, new FollowerTrack(Farmer, Trader));

        placeTileMap.put(ShippingLine, PlaceTile.ShippingLine);
        placeTileMap.put(Brewery, PlaceTile.Brewery);
        placeTileMap.put(Hayrick, PlaceTile.Hayrick);
        placeTileMap.put(Sacristy, PlaceTile.Sacristy);
        placeTileMap.put(WoolManufacturer, PlaceTile.WoolManufacturer);
        placeTileMap.put(CheeseFactory, PlaceTile.CheeseFactory);
        placeTileMap.put(Hospital, PlaceTile.Hospital);
        placeTileMap.put(TailorShop, PlaceTile.TailorShop);
        placeTileMap.put(Windmill, PlaceTile.Windmill);
        placeTileMap.put(Library, PlaceTile.Library);
        placeTileMap.put(Office, PlaceTile.Office);
        placeTileMap.put(Cellar, PlaceTile.Cellar);
        placeTileMap.put(Laboratory, PlaceTile.Laboratory);
        placeTileMap.put(HorseWagon, PlaceTile.HorseWagon);
        placeTileMap.put(Winery, PlaceTile.Winery);

        actionProcessors.put(FarmHouse, new FarmHouseProcessor());
        actionProcessors.put(Village, new VillageProcessor(this));
        actionProcessors.put(University, new UniversityProcessor());
        actionProcessors.put(Castle, new CastleProcessor());
        actionProcessors.put(Scriptorium, new DevelopmentBumpProcessor(1));
        actionProcessors.put(TownHall, new TownHallProcessor(TownHall));
        actionProcessors.put(Monastery, new MonasteryProcessor());
        actionProcessors.put(Ship, new MovementProcessor(PathType.Sea));
        actionProcessors.put(Wagon, new MovementProcessor(PathType.Land));
        actionProcessors.put(GuildHall, new GuildHallProcessor());

        //places
        actionProcessors.put(ShippingLine, new DevelopmentBumpProcessor(1));
        actionProcessors.put(Brewery, new CoinBumpProcessor(2));
        actionProcessors.put(Hayrick, new GoodsBumpProcessor(GoodType.Grain));
        actionProcessors.put(WoolManufacturer, new GoodsBumpProcessor(GoodType.Wool));
        actionProcessors.put(CheeseFactory, new GoodsBumpProcessor(GoodType.Cheese));
        actionProcessors.put(Hospital, new HospitalProcessor());
        actionProcessors.put(TailorShop, new GoodsBumpProcessor(GoodType.Brocade));
        actionProcessors.put(Windmill, new WindmillProcessor());
        actionProcessors.put(Library, new DevelopmentBumpProcessor(2));
        actionProcessors.put(Office, new OfficeProcessor());
        actionProcessors.put(Cellar, new CoinBumpProcessor(4));
        actionProcessors.put(Laboratory, new LaboratoryProcessor(this));
        actionProcessors.put(HorseWagon, new MovementProcessor(PathType.Land));
        actionProcessors.put(Winery, new GoodsBumpProcessor(GoodType.Wine));
        actionProcessors.put(GunpowderTower, new TownHallProcessor(GunpowderTower));
        actionProcessors.put(Pharmacy, new PharmacyProcessor());
    }

    @Override
    public boolean isActionValid(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        return super.isActionValid(actionType, gameState, player, additionalDataMap);
    }

    @Override
    public void processAction(ActionType actionType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        super.processAction(actionType, gameState, player, additionalDataMap);
    }
}
