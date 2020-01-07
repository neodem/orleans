package com.neodem.orleans.engine.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class FollowerTrack {

    protected static final class Slot {
        FollowerType ft;
        Follower f;

        public Slot(FollowerType ft) {
            this.ft = ft;
        }

        public Slot(Slot slot) {
            this.ft = slot.ft;
            this.f = slot.f;
        }

        public FollowerType getFt() {
            return ft;
        }

        public Follower getF() {
            return f;
        }
    }

    private final List<Slot> track = new ArrayList<>();
    private final int maxSize;

    private int filledSpotsCount;
    private boolean full;

    public int size() {
        return filledSpotsCount;
    }

    public FollowerTrack(FollowerType... followerTypes) {
        maxSize = followerTypes.length;
        for (FollowerType ft : followerTypes) {
            track.add(new Slot(ft));
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
        full = false;
        filledSpotsCount = template.filledSpotsCount;
        for (Slot s : template.getTrack()) {
            track.add(new Slot(s));
        }
    }

    /**
     * will return a reference to the follower in the given position
     *
     * @param position
     * @return null if there are no Followers in that position
     */
    public Follower getFollowerAtPosition(int position) {
        Slot slot = track.get(position);
        return slot.f;
    }

    /**
     * will return a reference to the follower in the given position AND remove it from the track
     *
     * @param position
     * @return
     */
    public Follower removeFollowerAtPosition(int position) {
        Slot slot = track.get(position);
        Follower follower = slot.f;
        if (follower != null) {
            filledSpotsCount--;
            slot.f = null;
        }
        return follower;
    }

    public List<Slot> getTrack() {
        return track;
    }

    /**
     * return true if all of the needed types are filled with Followers that can satify them
     * optionally add one techSlot to override
     *
     * @param techSlot may be null
     * @return
     */
    public boolean isReady(Integer techSlot) {
        boolean ready = true;
        for (int i = 0; i < track.size(); i++) {
            if (techSlot != null && techSlot == i) continue;
            Slot s = track.get(i);
            if (s.f == null) return false;
            if (!(s.f.getType() == s.ft || s.f.canSubFor(s.ft))) {
                ready = false;
                break;
            }
        }
        return ready;
    }

    public Collection<Follower> removeAllFollowers() {
        Collection<Follower> followers = new HashSet<>();

        for (Slot s : track) {
            if (s.f != null) {
                followers.add(s.f);
                s.f = null;
                filledSpotsCount--;
            }
        }

        return followers;
    }

    public boolean canAdd(Follower follower, int position) {
        if (full) return false;

        Slot slot = track.get(position);
        if (slot.f == null) {
            return follower.getType() == slot.ft || follower.canSubFor(slot.ft);
        }

        return false;
    }

    public boolean add(Follower follower, int position) {
        if (canAdd(follower, position)) {

            Slot slot = track.get(position);
            slot.f = follower;
            track.add(position, slot);

            filledSpotsCount++;

            if (filledSpotsCount == maxSize) {
                full = true;
            }
        } else {
            throw new IllegalArgumentException("slot at " + position + " is filled already!");
        }

        return full;
    }
}
