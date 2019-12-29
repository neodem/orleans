package com.neodem.orleans.engine.core;

import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.original.model.BenefitName;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public interface BenefitTracker {

    /**
     *
     * @param benefitName
     * @param follower
     * @return true if there is space for and the type is allowed
     */
    boolean canAddToBenefit(BenefitName benefitName, Follower follower);

    /**
     *
     * @param benefitName
     * @param follower
     * @return true if this fills the benefit!
     */
    boolean addToBenefit(BenefitName benefitName, Follower follower);

    int getBenefitCoinReward(BenefitName benefitName);
}
