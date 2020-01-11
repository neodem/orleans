package com.neodem.orleans.engine.core.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Collection;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/29/19
 */
public class Follower {
    private FollowerType followerType;

    private Collection<FollowerType> dba = Sets.newHashSet(FollowerType.Any);

    public Follower(FollowerType followerType) {
        this.followerType = followerType;
    }

    protected Follower() {
    }

    public Follower(JsonNode json) {
        this.followerType = FollowerType.fromValue(json.get("followerType").textValue());
        JsonNode dba = json.get("dba");
        if (dba != null) {
            this.dba.clear();
            for (JsonNode node : dba) {
                this.dba.add(FollowerType.fromValue(node.textValue()));
            }
        }
    }

    /**
     * make a starter Follower with correct alias
     *
     * @param type
     * @return
     */
    public static Follower makeStarter(FollowerType type) {
        FollowerType alias;
        switch (type) {
            case StarterBoatman:
                alias = FollowerType.Boatman;
                break;
            case StarterCraftsman:
                alias = FollowerType.Craftsman;
                break;
            case StarterTrader:
                alias = FollowerType.Trader;
                break;
            case StarterFarmer:
                alias = FollowerType.Farmer;
                break;
            default:
                throw new IllegalArgumentException("this type " + type + " is not a starter type");

        }

        Follower f = new Follower(type);
        f.addAlias(alias);
        return f;
    }

    public static Follower makeMonk() {
        Follower f = new Follower(FollowerType.Monk);
        f.addAlias(FollowerType.Farmer);
        f.addAlias(FollowerType.Boatman);
        f.addAlias(FollowerType.Craftsman);
        f.addAlias(FollowerType.Trader);
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
