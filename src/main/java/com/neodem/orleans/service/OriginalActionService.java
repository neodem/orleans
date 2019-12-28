package com.neodem.orleans.service;

import com.google.common.collect.Lists;
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

    private static final Map<ActionType, List<Follower>> actionMappings = new HashMap<>();
    static {
        actionMappings.put(FarmHouse, Lists.newArrayList(Boatman, Craftsman));
        actionMappings.put(Village, Lists.newArrayList(Boatman, Craftsman, Farmer));
        actionMappings.put(University, Lists.newArrayList(Farmer, Craftsman, Trader));
        actionMappings.put(Castle, Lists.newArrayList(Farmer, Boatman, Trader));
        actionMappings.put(Scriptorium, Lists.newArrayList(Knight, Scholar));
        actionMappings.put(TownHall, Lists.newArrayList(Farmer, Scholar, Knight, Trader, Craftsman, Boatman));
        actionMappings.put(Monastery, Lists.newArrayList(Scholar, Trader));
        actionMappings.put(Ship, Lists.newArrayList(Farmer, Boatman, Knight));
        actionMappings.put(Wagon, Lists.newArrayList(Farmer, Trader, Knight));
        actionMappings.put(GuildHall, Lists.newArrayList(Farmer, Craftsman, Knight));
    }

    public OriginalActionService() {
        super(actionMappings);
    }
}
