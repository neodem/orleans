package com.neodem.orleans.service;

import com.google.common.collect.Lists;
import com.neodem.orleans.collections.Grouping;
import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.neodem.orleans.model.ActionType.*;
import static com.neodem.orleans.model.Follower.*;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class OriginalActionService extends BaseActionService implements ActionService {

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
    }

    public OriginalActionService() {
        super(actionMappings);
    }
}
