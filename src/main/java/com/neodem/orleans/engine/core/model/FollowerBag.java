package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.neodem.orleans.collections.RandomBag;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/31/19
 */
public class FollowerBag extends RandomBag<Follower> {
    public FollowerBag(JsonNode json) {
        for (JsonNode node : json) {
            Follower f = new Follower(node);
            super.add(f);
        }
    }

    public FollowerBag() {
    }

    /**
     * take a specific folllower of the given type from the bag
     *
     * @param followerType
     * @return
     */
    public Follower takeOfType(FollowerType followerType) {

        int found = -1;
        for (int i = 0; i < data.size(); i++) {
            if (data.get(i).getType() == followerType) {
                found = i;
                break;
            }
        }

        if (found != -1) return data.remove(found);
        return null;
    }
}
