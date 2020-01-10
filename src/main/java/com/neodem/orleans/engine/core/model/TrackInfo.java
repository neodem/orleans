package com.neodem.orleans.engine.core.model;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class TrackInfo {
    private Track track;
    private int location;

    protected TrackInfo() {
    }

    public TrackInfo(Track track, int location) {
        this.track = track;
        this.location = location;
    }

    public Track getTrack() {
        return track;
    }

    public int getLocation() {
        return location;
    }

    public int incLocation() {
        return ++location;
    }

    protected void setTrack(Track track) {
        this.track = track;
    }

    protected void setLocation(int location) {
        this.location = location;
    }
}
