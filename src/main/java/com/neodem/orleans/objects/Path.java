package com.neodem.orleans.objects;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class Path {
    private final TokenLocation from;
    private final TokenLocation to;

    private final Set<TokenLocation> locationKey = new HashSet<>();
    private final PathType pathType;

    private Collection<GoodType> goods = new HashSet<>();

    public Path(TokenLocation from, TokenLocation to, PathType pathType) {
        Assert.notNull(from, "from location may not be null");
        Assert.notNull(to, "to location may not be null");
        Assert.notNull(pathType, "pathType may not be null");
        this.from = from;
        this.to = to;
        this.pathType = pathType;
        this.locationKey.add(from);
        this.locationKey.add(to);
    }

    @Override
    public String toString() {
        return from +
                "->" + to +
                ", pathType=" + pathType +
                ", goods=" + goods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return locationKey.equals(path.locationKey) &&
                pathType == path.pathType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(locationKey, pathType);
    }

    public void addGood(GoodType goodType) {
        goods.add(goodType);
    }

    public TokenLocation getFrom() {
        return from;
    }

    public TokenLocation getTo() {
        return to;
    }

    public PathType getPathType() {
        return pathType;
    }

    public Collection<GoodType> getGoods() {
        return goods;
    }
}
