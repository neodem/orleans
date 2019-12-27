package com.neodem.orleans.objects;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class OriginalPlayerState extends PlayerState {

    public OriginalPlayerState(String playerId) {
        super(playerId);
    }

    @Override
    protected void initState() {
        tokenLocation = TokenLocation.Orleans;
        tracks.put(Track.Farmers, 0);
        tracks.put(Track.Craftsmen, 0);
        tracks.put(Track.Traders, 0);
        tracks.put(Track.Boatmen, 0);
        tracks.put(Track.Knights, 0);
        tracks.put(Track.Scholars, 0);
        tracks.put(Track.Development, 0);

        addToBag(Token.StarterBoatmen);
        addToBag(Token.StarterCraftsman);
        addToBag(Token.StarterFarmer);
        addToBag(Token.StarterTrader);

        goodCounts.put(GoodType.Grain, 0);
        goodCounts.put(GoodType.Cheese, 0);
        goodCounts.put(GoodType.Wine, 0);
        goodCounts.put(GoodType.Wool, 0);
        goodCounts.put(GoodType.Brocade, 0);
    }
}
