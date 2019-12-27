package com.neodem.orleans.service;

import com.neodem.orleans.model.ActionType;
import com.neodem.orleans.model.FollowerType;

import java.util.List;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public interface ActionService {
    /**
     * return true if the given action accepts all the types in the followerTypes list
     * @param actionType
     * @param followerTypes
     * @return
     */
    boolean validAction(ActionType actionType, List<FollowerType> followerTypes);
}
