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

    private BoardState() {
        pathsFromTown = new HashMap<>();
        pathsBetween = new HashMap<>();
        allPaths = new HashSet<>();
    }

    public BoardState(Map<GoodType, Integer> goodsInventory, int playerCount) {
        this();
        init(goodsInventory, playerCount);
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

    public Path getPathBetween(PathBetween pathBetween, PathType pathType) {
        Collection<Path> allPathsBetween = pathsBetween.get(pathBetween);
        if (allPathsBetween != null) {
            for (Path p : allPathsBetween) {
                if (p.getPathType() == pathType) return p;
            }
        }
        return null;
    }

    //////////////////////////

    protected abstract void init(Map<GoodType, Integer> goodsInventory, int playerCount);

    protected void addPath(TokenLocation location1, TokenLocation location2, PathType pathType, Map<GoodType, Integer> goodsInventory) {
        addPath(new PathBetween(location1, location2), pathType, goodsInventory, 1);
    }


    protected void addPath(TokenLocation location1, TokenLocation location2, PathType pathType, Map<GoodType, Integer> goodsInventory, int goodCount) {
        addPath(new PathBetween(location1, location2), pathType, goodsInventory, goodCount);
    }

    protected void addPath(PathBetween pathBetween, PathType pathType, Map<GoodType, Integer> goodsInventory, int goodCount) {

        if (!doesPathExist(pathBetween, pathType)) {
            Path path = new Path(pathBetween, pathType);
            for (int i = 0; i < goodCount; i++)
                addDifferentRandomGoodToPath(goodsInventory, path);

            allPaths.add(path);

            Collection<Path> paths = pathsBetween.get(path.getPathBetween());
            if (paths == null) paths = new HashSet<>();
            paths.add(path);
            pathsBetween.put(path.getPathBetween(), paths);

            addSpecificPath(pathBetween.getLocation1(), path);
            addSpecificPath(pathBetween.getLocation2(), path);
        }
    }

    protected void addDifferentRandomGoodToPath(Map<GoodType, Integer> goodsInventory, Path path) {
        GoodType goodType;
        do {
            goodType = getRandomGoodFromInventory(goodsInventory);
            if (goodType == null) throw new RuntimeException("trying to init path with no goods available");
        } while (path.getGoods().contains(goodType));

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

    protected boolean doesPathExist(PathBetween pathBetween, PathType pathType) {
        return getPathBetween(pathBetween, pathType) != null;
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
}
