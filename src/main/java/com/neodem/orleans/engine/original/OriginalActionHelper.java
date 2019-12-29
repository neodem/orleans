package com.neodem.orleans.engine.original;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionHelperBase;
import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.actions.CoinBumpProcessor;
import com.neodem.orleans.engine.core.actions.DevelopmentBumpProcessor;
import com.neodem.orleans.engine.core.actions.GoodsBumpProcessor;
import com.neodem.orleans.engine.core.actions.MovementProcessor;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PathType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.actions.*;
import com.neodem.orleans.engine.original.model.PlaceTile;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.ActionType.*;
import static com.neodem.orleans.engine.core.model.Follower.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class OriginalActionHelper extends ActionHelperBase implements ActionHelper {

    private final Map<ActionType, Grouping<Follower>> actionMappings = new HashMap<>();
    private final Map<ActionType, ActionProcessor> actionProcessors = new HashMap<>();
    private final Map<ActionType, PlaceTile> placeTileMap = new HashMap<>();

    @Override
    protected Map<ActionType, Grouping<Follower>> actionMappings() {
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
        actionMappings.put(FarmHouse, new Grouping<>(Boatman, Craftsman));
        actionMappings.put(Village, new Grouping<>(Boatman, Craftsman, Farmer));
        actionMappings.put(University, new Grouping<>(Farmer, Craftsman, Trader));
        actionMappings.put(Castle, new Grouping<>(Farmer, Boatman, Trader));
        actionMappings.put(Scriptorium, new Grouping<>(Knight, Scholar));
        actionMappings.put(TownHall, new Grouping<>(Farmer, Scholar, Knight, Trader, Craftsman, Boatman));
        actionMappings.put(Monastery, new Grouping<>(Scholar, Trader));
        actionMappings.put(Ship, new Grouping<>(Farmer, Boatman, Knight));
        actionMappings.put(Wagon, new Grouping<>(Farmer, Trader, Knight));
        actionMappings.put(GuildHall, new Grouping<>(Farmer, Craftsman, Knight));

        //places
        actionMappings.put(ShippingLine, new Grouping<>(Boatman));
        actionMappings.put(Brewery, new Grouping<>(Monk));
        actionMappings.put(Hayrick, new Grouping<>(Farmer));
        actionMappings.put(Sacristy, new Grouping<>(Monk));
        actionMappings.put(WoolManufacturer, new Grouping<>(Boatman, Trader));
        actionMappings.put(CheeseFactory, new Grouping<>(Farmer, Farmer));
        actionMappings.put(Hospital, new Grouping<>(Boatman, Scholar));
        actionMappings.put(TailorShop, new Grouping<>(Trader, Scholar));
        actionMappings.put(Windmill, new Grouping<>(Craftsman, Craftsman));
        actionMappings.put(Library, new Grouping<>(Knight, Scholar));
        actionMappings.put(Office, new Grouping<>(Scholar, Scholar));
        actionMappings.put(Cellar, new Grouping<>(Monk, Scholar));
        actionMappings.put(Laboratory, new Grouping<>(Craftsman, Scholar));
        actionMappings.put(HorseWagon, new Grouping<>(Knight, Trader));
        actionMappings.put(Winery, new Grouping<>(Farmer, Trader));

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

    @Override
    public boolean actionCanAccept(ActionType actionType, List<Follower> followers) {
        Assert.notNull(actionType, "actionType may not be null");
        Assert.notNull(followers, "followers may not be null");

        if (actionType == Pharmacy || actionType == GunpowderTower) {
            return followers != null && !followers.isEmpty();
        }

        if (actionType == TownHall || actionType == GunpowderTower) {
            for (Follower follower : followers) {
                if (follower == StarterBoatman || follower == StarterCraftsman || follower == StarterFarmer || follower == StarterTrader)
                    return false;
            }
            return true;
        }

        return super.actionCanAccept(actionType, followers);
    }

    @Override
    public boolean canPlaceIntoAction(ActionType actionType, List<Follower> followersToPlace, List<Follower> placedInActionAlready) {
        if (placedInActionAlready == null || placedInActionAlready.isEmpty() && actionCanAccept(actionType, followersToPlace))
            return true;

        if (actionType == Pharmacy) {
            return (placedInActionAlready == null || placedInActionAlready.isEmpty()) && (followersToPlace != null && !followersToPlace.isEmpty());
        }

        if (actionType == GunpowderTower) {
            return (placedInActionAlready == null || placedInActionAlready.size() < 2) && (followersToPlace != null && !followersToPlace.isEmpty());
        }

        return super.canPlaceIntoAction(actionType, followersToPlace, placedInActionAlready);
    }
}
