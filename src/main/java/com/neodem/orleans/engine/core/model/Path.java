package com.neodem.orleans.engine.core.model;

import com.google.common.base.Objects;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class Path {
    private final PathBetween pathBetween;
    private final PathType pathType;
    private final Collection<GoodType> goods;

    public Path(PathBetween pathBetween, PathType pathType) {
        Assert.notNull(pathBetween, "pathBetween may not be null");
        Assert.notNull(pathType, "pathType may not be null");
        this.pathBetween = pathBetween;
        this.pathType = pathType;
        this.goods = new HashSet<>();
    }

    public static final Collection<Path> getPathsOfType(Collection<Path> allPaths, PathType pathType) {
        return allPaths.stream().filter(p -> p.getPathType() == pathType).collect(Collectors.toSet());
    }

    @Override
    public String toString() {
        return pathBetween +
                ", pathType=" + pathType +
                ", goods=" + goods;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path = (Path) o;
        return pathBetween.equals(path.pathBetween) &&
                pathType == path.pathType;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pathBetween, pathType);
    }

    public void addGood(GoodType goodType) {
        goods.add(goodType);
    }

    public PathBetween getPathBetween() {
        return pathBetween;
    }

    public PathType getPathType() {
        return pathType;
    }

    public Collection<GoodType> getGoods() {
        return goods;
    }

    public boolean goodAvailable(GoodType desiredGood) {
        if (goods != null) return goods.contains(desiredGood);
        return false;
    }

    public void removeGoodFromPath(GoodType desiredGood) {
        if (goodAvailable(desiredGood)) {
            goods.remove(desiredGood);
        }
    }
}
