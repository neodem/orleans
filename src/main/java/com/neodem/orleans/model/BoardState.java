package com.neodem.orleans.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public abstract class BoardState {
    protected final Map<TokenLocation, Collection<Path>> pathsFromTown;
    protected final Collection<Path> allPaths;
    protected final Map<PathBetween, Collection<Path>> pathsBetween;

    public BoardState(Map<GoodType, Integer> goodsInventory, int playerCount) {
        pathsFromTown = new HashMap<>();
        pathsBetween = new HashMap<>();
        allPaths = new HashSet<>();
        init(goodsInventory, playerCount);
    }

    protected abstract void init(Map<GoodType, Integer> goodsInventory, int playerCount);

    protected void addPath(TokenLocation from, TokenLocation to, PathType pathType, Map<GoodType, Integer> goodsInventory) {
        addPath(from, to, pathType, goodsInventory, 1);
    }

    protected void addPath(TokenLocation from, TokenLocation to, PathType pathType, Map<GoodType, Integer> goodsInventory, int goodCount) {
        if (!doesPathExist(from, to, pathType)) {
            Path path = new Path(from, to, pathType);
            for (int i = 0; i < goodCount; i++)
                addGoodToPath(goodsInventory, path);

            allPaths.add(path);

            Collection<Path> paths = pathsBetween.get(path.getPathBetween());
            if (paths == null) paths = new HashSet<>();
            paths.add(path);
            pathsBetween.put(path.getPathBetween(), paths);

            addSpecificPath(from, path);
            addSpecificPath(to, path);
        }
    }

    protected void addGoodToPath(Map<GoodType, Integer> goodsInventory, Path path) {
        GoodType goodType = getRandomGoodFromInventory(goodsInventory);
        if (goodType != null) {
            path.addGood(goodType);
        } else {
            throw new RuntimeException("trying to init path with no goods available");
        }
    }

    protected void addSpecificPath(TokenLocation from, Path path) {
        Collection<Path> pathCollection = pathsFromTown.get(from);
        if (pathCollection == null) pathCollection = new HashSet<>();
        pathCollection.add(path);
        pathsFromTown.put(from, pathCollection);
    }

    public Map<TokenLocation, Collection<Path>> getPathsFromTown() {
        return pathsFromTown;
    }

    public Map<PathBetween, Collection<Path>> getPathsBetween() {
        return pathsBetween;
    }

    public Collection<Path> getAllPaths() {
        return allPaths;
    }

    protected boolean doesPathExist(TokenLocation from, TokenLocation to, PathType pathType) {
        return getPathBetween(from, to, pathType) != null;
    }

    protected GoodType getRandomGoodFromInventory(Map<GoodType, Integer> goodsInventory) {
        GoodType result = null;

        if (goodsAvailable(goodsInventory)) {
            do {
                GoodType candidate = GoodType.randomGood();
                int amountAvailable = goodsInventory.get(candidate);
                if (amountAvailable > 0) {
                    goodsInventory.put(candidate, amountAvailable - 1);
                    result = candidate;
                }
            } while (result == null);
        }

        return result;
    }

    protected boolean goodsAvailable(Map<GoodType, Integer> goodsInventory) {
        for (GoodType goodType : GoodType.values()) {
            if (goodsInventory.get(goodType) > 0) {
                return true;
            }
        }
        return false;
    }

    public Collection<Path> getPathsBetween(TokenLocation from, TokenLocation to) {
        return pathsBetween.get(new PathBetween(from, to));
    }

    public Path getPathBetween(TokenLocation from, TokenLocation to, PathType pathType) {
        Collection<Path> pathsBetween = getPathsBetween(from, to);
        if (pathsBetween != null) {
            for (Path p : pathsBetween) {
                if (p.getPathType() == pathType) return p;
            }
        }
        return null;
    }
}
