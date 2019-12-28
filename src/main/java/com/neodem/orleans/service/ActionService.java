package com.neodem.orleans.service;

import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.Follower;

import java.util.List;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public interface ActionService {
    /**
     * return true if the given action accepts all the types in the followerTypes list
     * @param actionType
     * @param followers
     * @return
     */
    boolean validAction(ActionType actionType, List<Follower> followers);

    /**
     * return true if the given action accepts all the types in the followerTypes list and
     * is 'complete'
     *
     * @param actionType
     * @param followers
     * @param techToken if there is a tech token override
     * @return
     */
    boolean fullAction(ActionType actionType, List<Follower> followers, Follower techToken);
}
