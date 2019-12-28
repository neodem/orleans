package com.neodem.orleans.engine.original.model;

import com.neodem.orleans.engine.core.model.Follower;
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

        addToBag(Follower.StarterBoatman);
        addToBag(Follower.StarterCraftsman);
        addToBag(Follower.StarterFarmer);
        addToBag(Follower.StarterTrader);

        goodCounts.put(GoodType.Grain, 0);
        goodCounts.put(GoodType.Cheese, 0);
        goodCounts.put(GoodType.Wine, 0);
        goodCounts.put(GoodType.Wool, 0);
        goodCounts.put(GoodType.Brocade, 0);
    }
}
