package com.neodem.orleans.engine.original.actions;

import com.google.common.collect.Sets;
import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.BenefitTracker;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.FollowerTrack;
import com.neodem.orleans.engine.core.model.FollowerType;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;
import com.neodem.orleans.engine.original.model.BenefitName;
import com.neodem.orleans.engine.original.model.CitizenType;

import java.util.Collection;
import java.util.Map;

import static com.neodem.orleans.engine.core.model.AdditionalDataType.takeDevPoint;
import static com.neodem.orleans.engine.original.DevelopmentHelper.MAXTRACK;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class TownHallProcessor extends ActionProcessorBase {

    private final ActionType actionType;

    public TownHallProcessor(ActionType actionType) {
        this.actionType = actionType;
    }

    @Override
    protected Collection<AdditionalDataType> requiredTypes() {
        return Sets.newHashSet(AdditionalDataType.follower1, AdditionalDataType.benefit1);
    }

    @Override
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        BenefitTracker benefitTracker = gameState.getBenefitTracker();

        Map<ActionType, FollowerTrack> plans = player.getPlans();
        FollowerTrack followerTypes = plans.get(actionType); // followers planned

        FollowerType followerType1 = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower1);
        if (!followerTypes.contains(followerType1)) {
            throw new ActionProcessorException("the desired follower type: " + followerType1 + " is not part of your plan for this action");
        }

        BenefitName benefitName1 = getBenefitNameFromMap(additionalDataMap, AdditionalDataType.benefit1);
        if (!benefitTracker.canAddToBenefit(benefitName1, followerType1)) {
            throw new ActionProcessorException("the desired follower type: " + followerType1 + " can't go on " + benefitName1);
        }

        if (followerTypes.size() > 1) {
            FollowerType followerType2 = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower2);
            BenefitName benefitName2 = getBenefitNameFromMap(additionalDataMap, AdditionalDataType.benefit2);
            if (followerType2 != null && benefitName2 != null) {
                if (!followerTypes.contains(followerType2)) {
                    throw new ActionProcessorException("the desired follower type: " + followerType2 + " is not part of your plan for this action");
                }

                if (!benefitTracker.canAddToBenefit(benefitName2, followerType2)) {
                    throw new ActionProcessorException("the desired follower type: " + followerType2 + " can't go on " + benefitName2);
                }
            }
        }

        return true;
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        BenefitTracker benefitTracker = gameState.getBenefitTracker();

        FollowerType followerType1 = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower1);
        BenefitName benefitName1 = getBenefitNameFromMap(additionalDataMap, AdditionalDataType.benefit1);
        processBenefitPlacement(gameState, player, additionalDataMap, benefitTracker, followerType1, benefitName1);

        FollowerType followerType2 = getFollowerFromMap(additionalDataMap, AdditionalDataType.follower2);
        BenefitName benefitName2 = getBenefitNameFromMap(additionalDataMap, AdditionalDataType.benefit2);
        processBenefitPlacement(gameState, player, additionalDataMap, benefitTracker, followerType2, benefitName2);
    }

    private void processBenefitPlacement(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap, BenefitTracker benefitTracker, FollowerType followerType, BenefitName benefitName) {
        if (followerType != null && benefitName != null) {
            boolean citizenAcquired = benefitTracker.addToBenefit(benefitName, followerType);
            if (citizenAcquired) {
                player.addCitizen(CitizenType.BenefitTrack);
            }

            if (benefitName == BenefitName.Canalisation) {
                if (additionalDataMap.containsKey(takeDevPoint)) {
                    int trackIndex = player.getTrackValue(Track.Development);
                    trackIndex++;
                    if (trackIndex > MAXTRACK) trackIndex = MAXTRACK;
                    DevelopmentHelper.processReward(trackIndex - 1, trackIndex, gameState, player);
                    player.getTracks().put(Track.Development, trackIndex);
                } else {
                    player.addCoin();
                }
            } else {
                int coinReward = benefitTracker.getBenefitCoinReward(benefitName);
                player.addCoin(coinReward);
            }
        }
    }
}
