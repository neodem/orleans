package com.neodem.orleans.engine.original;

import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.engine.core.ActionHelper;
import com.neodem.orleans.engine.core.ActionProcessor;
import com.neodem.orleans.engine.core.BaseActionHelper;
import com.neodem.orleans.engine.core.actions.CoinBumpProcessor;
import com.neodem.orleans.engine.core.actions.DevelopmentBumpProcessor;
import com.neodem.orleans.engine.core.actions.GoodsBumpProcessor;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.original.actions.*;
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
public class OriginalActionHelper extends BaseActionHelper implements ActionHelper {

    private static final Map<ActionType, Grouping<Follower>> actionMappings = new HashMap<>();

    static {
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
    }

    private static final Map<ActionType, ActionProcessor> actionProcessors = new HashMap<>();

    static {
        actionProcessors.put(FarmHouse, new FarmHouseProcessor());
        actionProcessors.put(Village, new VillageProcessor());
        actionProcessors.put(University, new UniversityProcessor());
        actionProcessors.put(Castle, new CastleProcessor());
        actionProcessors.put(Scriptorium, new DevelopmentBumpProcessor(1));
        actionProcessors.put(TownHall, new TownHallProcessor());
        actionProcessors.put(Monastery, new MonasteryProcessor());
        actionProcessors.put(Ship, new ShipProcessor());
        actionProcessors.put(Wagon, new WagonProcessor());
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
        actionProcessors.put(Laboratory, new LaboratoryProcessor());
        actionProcessors.put(HorseWagon, new HorseWagonProcessor());
        actionProcessors.put(Winery, new GoodsBumpProcessor(GoodType.Wine));
        actionProcessors.put(GunpowderTower, new GunpowderTowerProcessor());
        actionProcessors.put(Pharmacy, new PharmacyProcessor());
    }

    public OriginalActionHelper() {
        super(actionMappings, actionProcessors);
    }

    @Override
    public void processAction(ActionType actionType, GameState gameState, PlayerState player) {
        super.processAction(actionType, gameState, player);
    }

    @Override
    public boolean actionCanAccept(ActionType actionType, List<Follower> followers) {
        Assert.notNull(actionType, "actionType may not be null");
        Assert.notNull(followers, "followers may not be null");

        if (actionType == Pharmacy || actionType == GunpowderTower) {
            return followers != null && !followers.isEmpty();
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
