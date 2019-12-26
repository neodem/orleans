package com.neodem.orleans.objects;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class Path {
    private final TokenLocation from;
    private final TokenLocation to;
    private final PathType pathType;

    private Collection<GoodType> goods = new HashSet<>();

    public Path(TokenLocation from, TokenLocation to, PathType pathType) {
        Assert.notNull(from, "from location may not be null");
        Assert.notNull(to, "to location may not be null");
        Assert.notNull(pathType, "pathType may not be null");
        this.from = from;
        this.to = to;
        this.pathType = pathType;
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
        return from == path.from &&
                to == path.to &&
                pathType == path.pathType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(from, to, pathType);
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
