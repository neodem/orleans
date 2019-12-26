package com.neodem.orleans.objects;


/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class TrackInfo {
    private Track track;
    private int location;

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
}
