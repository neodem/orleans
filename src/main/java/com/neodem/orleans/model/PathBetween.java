package com.neodem.orleans.model;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.Assert;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/27/19
 */
public class PathBetween {
    private final TokenLocation location1;
    private final TokenLocation location2;

    public PathBetween(TokenLocation location1, TokenLocation location2) {
        Assert.notNull(location1, "location1 may not be null");
        Assert.notNull(location2, "location2 may not be null");
        Assert.isTrue(location1 != location2, "PathBetween must reference 2 different places");
        this.location1 = location1;
        this.location2 = location2;
    }

    @Override
    public String toString() {
        return location1 + "-" + location2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathBetween that = (PathBetween) o;
        return (location1 == that.location1 && location2 == that.location2) ||
                (location1 == that.location2 && location2 == that.location1);
    }

    @Override
    public int hashCode() {
        int h1 = new HashCodeBuilder(11, 121)
                .append(location1)
                .toHashCode();

        int h2 = new HashCodeBuilder(11, 121)
                .append(location2)
                .toHashCode();

        return h1 * h2;
    }

    public TokenLocation getLocation1() {
        return location1;
    }

    public TokenLocation getLocation2() {
        return location2;
    }

    public boolean contains(TokenLocation location) {
        return location1 == location || location2 == location;
    }
}
