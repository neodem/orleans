package com.neodem.orleans.engine.original.actions;

import com.neodem.orleans.engine.core.ActionProcessorException;
import com.neodem.orleans.engine.core.BenefitTracker;
import com.neodem.orleans.engine.core.actions.ActionProcessorBase;
import com.neodem.orleans.engine.core.model.ActionType;
import com.neodem.orleans.engine.core.model.AdditionalDataType;
import com.neodem.orleans.engine.core.model.Follower;
import com.neodem.orleans.engine.core.model.FollowerTrack;
import com.neodem.orleans.engine.core.model.GameState;
import com.neodem.orleans.engine.core.model.PlayerState;
import com.neodem.orleans.engine.core.model.Track;
import com.neodem.orleans.engine.original.DevelopmentHelper;
import com.neodem.orleans.engine.original.model.BenefitName;
import com.neodem.orleans.engine.original.model.CitizenType;

import java.util.Map;

import static com.neodem.orleans.engine.core.model.AdditionalDataType.*;
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
    public boolean doIsAllowed(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        BenefitTracker benefitTracker = gameState.getBenefitTracker();

        FollowerTrack followerTrack = player.getPlan(actionType);
        Follower plannedFollower1 = followerTrack.peekFollowerAtPosition(0);
        Follower plannedFollower2 = followerTrack.peekFollowerAtPosition(1);
        if (plannedFollower1 == null && plannedFollower2 == null) {
            throw new ActionProcessorException("There are no followers planned in this action: " + actionType);
        }
        if (plannedFollower1 != null) {
            isAllowedForFollower(benefitTracker, additionalDataMap, plannedFollower1, AdditionalDataType.benefit1);
        }
        if (plannedFollower2 != null) {
            isAllowedForFollower(benefitTracker, additionalDataMap, plannedFollower2, benefit2);
        }

        return true;
    }

    private void isAllowedForFollower(BenefitTracker benefitTracker, Map<AdditionalDataType, String> additionalDataMap, Follower f, AdditionalDataType additionalDataType) {
        BenefitName benefitName = getBenefitNameFromMap(additionalDataMap, additionalDataType);
        if (!benefitTracker.canAddToBenefit(benefitName, f)) {
            throw new ActionProcessorException("the desired follower: " + f + " can't go on " + benefitName);
        }
    }

    @Override
    public void doProcess(GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap) {
        BenefitTracker benefitTracker = gameState.getBenefitTracker();
        processBenefitPlacement(0, benefit1, gameState, player, additionalDataMap, benefitTracker);
        processBenefitPlacement(1, benefit2, gameState, player, additionalDataMap, benefitTracker);
    }

    private void processBenefitPlacement(int index, AdditionalDataType additionalDataType, GameState gameState, PlayerState player, Map<AdditionalDataType, String> additionalDataMap, BenefitTracker benefitTracker) {
        FollowerTrack followerTrack = player.getPlan(actionType);
        Follower plannedFollower = followerTrack.removeFollowerAtPosition(index);

        if (plannedFollower != null) {
            BenefitName benefitName = getBenefitNameFromMap(additionalDataMap, additionalDataType);
            boolean citizenAcquired = benefitTracker.addToBenefit(benefitName, plannedFollower);
            if (citizenAcquired) {
                player.addCitizen(CitizenType.BenefitTrack);
            }
            if (benefitName == BenefitName.Canalisation) {
                if (additionalDataMap.containsKey(takeDevPoint)) {
                    int trackIndex = player.getTrackValue(Track.Development);
                    trackIndex++;
                    if (trackIndex > MAXTRACK) trackIndex = MAXTRACK;
                    DevelopmentHelper.processReward(trackIndex - 1, trackIndex, gameState, player);
                    player.setTrackIndex(Track.Development, trackIndex);
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
