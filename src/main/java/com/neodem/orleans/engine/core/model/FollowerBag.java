package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.collections.Bag;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/31/19
 */
public class FollowerBag extends Bag<Follower> {
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
