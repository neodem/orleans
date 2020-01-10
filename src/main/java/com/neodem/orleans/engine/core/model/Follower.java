package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class Follower {
    private FollowerType followerType;

    private Collection<FollowerType> dba = new HashSet<>();

    public Follower(FollowerType followerType) {
        this.followerType = followerType;
    }

    protected Follower() {

    }

    public Follower(JsonNode json) {
        this.followerType = FollowerType.fromValue(json.get("followerType").textValue());
        JsonNode dba = json.get("dba");
        if (dba != null) {
            for (JsonNode node : dba) {
                this.dba.add(FollowerType.fromValue(node.textValue()));
            }
        }
    }

    public static Follower makeStarter(FollowerType type, FollowerType subType) {
        Follower f = new Follower(type);
        f.addAlias(subType);
        return f;
    }

    public static Follower makeMonk() {
        Follower f = new Follower(FollowerType.Monk);
        f.addAlias(FollowerType.Farmer);
        f.addAlias(FollowerType.Boatman);
        f.addAlias(FollowerType.Craftsman);
        f.addAlias(FollowerType.Trader);
        f.addAlias(FollowerType.Scholar);
        f.addAlias(FollowerType.Scholar);
        return f;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Follower follower = (Follower) o;

        return new EqualsBuilder()
                .append(followerType, follower.followerType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(8353, 775)
                .append(followerType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return followerType.name();
    }

    public void addAlias(FollowerType followerType) {
        dba.add(followerType);
    }

    public boolean canSubFor(FollowerType followerType) {
        return dba.contains(followerType);
    }

    public void clearAliases() {
        dba.clear();
    }

    public FollowerType getFollowerType() {
        return followerType;
    }

    protected void setFollowerType(FollowerType followerType) {
        this.followerType = followerType;
    }

    public Collection<FollowerType> getDba() {
        return dba;
    }

    protected void setDba(Collection<FollowerType> dba) {
        this.dba = dba;
    }
}
