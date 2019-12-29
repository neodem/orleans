package com.neodem.orleans.engine.original.model;

import com.neodem.orleans.engine.core.model.EmptyFollowerSlot;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GoodType;
import com.neodem.orleans.engine.core.model.PlayerColor;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.TokenLocation;
import com.neodem.orleans.engine.core.model.Track;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalPlayerState extends PlayerState {

    private final static int MARKET_SIZE = 8;

    public OriginalPlayerState(String playerId, PlayerColor playerColor) {
        super(playerId, playerColor);
    }

    @Override
    protected void initState() {
        merchantLocation = TokenLocation.Orleans;
        tracks.put(Track.Farmers, 0);
        tracks.put(Track.Craftsmen, 0);
        tracks.put(Track.Traders, 0);
        tracks.put(Track.Boatmen, 0);
        tracks.put(Track.Knights, 0);
        tracks.put(Track.Scholars, 0);
        tracks.put(Track.Development, 0);

        for (int i = 0; i < MARKET_SIZE; i++) market.add(i, new EmptyFollowerSlot());

        addToBag(Follower.makeStarter(FollowerType.StarterBoatman, FollowerType.Boatman));
        addToBag(Follower.makeStarter(FollowerType.StarterCraftsman, FollowerType.Craftsman));
        addToBag(Follower.makeStarter(FollowerType.StarterFarmer, FollowerType.Farmer));
        addToBag(Follower.makeStarter(FollowerType.StarterTrader, FollowerType.Trader));

        goodCounts.put(GoodType.Grain, 0);
        goodCounts.put(GoodType.Cheese, 0);
        goodCounts.put(GoodType.Wine, 0);
        goodCounts.put(GoodType.Wool, 0);
        goodCounts.put(GoodType.Brocade, 0);
    }


}
