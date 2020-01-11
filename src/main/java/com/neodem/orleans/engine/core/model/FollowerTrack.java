package com.neodem.orleans.engine.core.model;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class FollowerTrack {

    protected static final class Slot {
        FollowerType expectedType;
        Follower followerInSlot;

        public Slot() {
        }

        public Slot(FollowerType expectedType) {
            this.expectedType = expectedType;
        }

        public Slot(Slot slot) {
            this.expectedType = slot.expectedType;
            this.followerInSlot = slot.followerInSlot;
        }

        public FollowerType getExpectedType() {
            return expectedType;
        }

        public Follower getFollowerInSlot() {
            return followerInSlot;
        }

        protected void setExpectedType(FollowerType expectedType) {
            this.expectedType = expectedType;
        }

        protected void setFollowerInSlot(Follower followerInSlot) {
            this.followerInSlot = followerInSlot;
        }
    }

    private Slot[] track;
    private int maxSize;
    private int filledSpotsCount;
    private boolean full;

    // set to true if the action is ready with at least one follower
    private boolean readyWhenNotFull = false;

    protected FollowerTrack() {
    }

    public int size() {
        return filledSpotsCount;
    }

    public FollowerTrack(FollowerType... followerTypes) {
        maxSize = followerTypes.length;
        track = new Slot[maxSize];
        for (int i = 0; i < maxSize; i++) {
            track[i] = new Slot(followerTypes[i]);
        }
        full = false;
        filledSpotsCount = 0;
    }

    /**
     * copy constructor
     *
     * @param template
     */
    public FollowerTrack(FollowerTrack template) {
        maxSize = template.maxSize;
        track = new Slot[maxSize];
        full = false;
        filledSpotsCount = template.filledSpotsCount;
        Slot[] templateTrack = template.getTrack();
        for (int i = 0; i < maxSize; i++) {
            track[i] = new Slot(templateTrack[i]);
        }
        readyWhenNotFull = template.readyWhenNotFull;
    }

    /**
     * will return a reference to the follower in the given position
     *
     * @param position
     * @return null if there are no Followers in that position
     */
    public Follower peekFollowerAtPosition(int position) {
        Slot slot = track[position];
        return slot.followerInSlot;
    }

    public FollowerType getTypeForSlot(int position) {
        Slot slot = track[position];
        return slot.expectedType;
    }

    /**
     * will return a reference to the follower in the given position AND remove it from the track
     *
     * @param position
     * @return
     */
    public Follower removeFollowerAtPosition(int position) {
        Slot slot = track[position];
        Follower follower = slot.followerInSlot;
        if (follower != null) {
            filledSpotsCount--;
            slot.followerInSlot = null;
        }
        return follower;
    }

    public Slot[] getTrack() {
        return track;
    }

    /**
     * return true if all of the needed types are filled with Followers that can satisfy them
     * optionally add one techSlot to override
     *
     * @param techSlot may be null
     * @return
     */
    public boolean isReady(Integer techSlot) {
        boolean ready = true;
        boolean atLeastOneFilled = false;
        for (int i = 0; i < track.length; i++) {
            if (techSlot != null && techSlot == i) {
                atLeastOneFilled = true;
                continue;
            }
            // get the slot
            Slot s = track[i];

            // if it's empty we are not ready
            if (s.followerInSlot == null) {
                ready = false;
                continue;
            } else {
                atLeastOneFilled = true;
            }

            // if we have something in the slot but it's not the right type we are not ready
            if (!(s.followerInSlot.getFollowerType() == s.expectedType || s.followerInSlot.canSubFor(s.expectedType))) {
                ready = false;
                break;
            }
        }

        if (!ready && readyWhenNotFull && atLeastOneFilled) return true;

        return ready;
    }

    public Collection<Follower> removeAllFollowers() {
        Collection<Follower> followers = new HashSet<>();

        for (Slot s : track) {
            if (s.followerInSlot != null) {
                followers.add(s.followerInSlot);
                s.followerInSlot = null;
                filledSpotsCount--;
            }
        }

        return followers;
    }

    public boolean canAdd(Follower follower, int position) {
        if (full) return false;

        Slot slot = track[position];
        if (slot.followerInSlot == null) {
            return follower.getFollowerType() == slot.expectedType || follower.canSubFor(slot.expectedType);
        }

        return false;
    }

    public boolean add(Follower follower, int position) {
        if (canAdd(follower, position)) {

            Slot slot = track[position];
            slot.followerInSlot = follower;
            track[position] = slot;

            filledSpotsCount++;

            if (filledSpotsCount == maxSize) {
                full = true;
            }
        } else {
            throw new IllegalArgumentException("slot at " + position + " is filled already!");
        }

        return full;
    }

    protected void setTrack(Slot[] track) {
        this.track = track;
    }

    public int getMaxSize() {
        return maxSize;
    }

    protected void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public int getFilledSpotsCount() {
        return filledSpotsCount;
    }

    protected void setFilledSpotsCount(int filledSpotsCount) {
        this.filledSpotsCount = filledSpotsCount;
    }

    public boolean isFull() {
        return full;
    }

    protected void setFull(boolean full) {
        this.full = full;
    }

    public boolean isReadyWhenNotFull() {
        return readyWhenNotFull;
    }

    public void setReadyWhenNotFull(boolean readyWhenNotFull) {
        this.readyWhenNotFull = readyWhenNotFull;
    }
}
