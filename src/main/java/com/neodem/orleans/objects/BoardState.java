package com.neodem.orleans.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public abstract class BoardState {
    protected final Map<TokenLocation, Collection<Path>> specificPaths;
    protected final Collection<Path> allPaths;

    public BoardState() {
        specificPaths = new HashMap<>();
        allPaths = new HashSet<>();
    }

    protected void addPath(Path path) {
        allPaths.add(path);
        addSpecificPath(path.getFrom(), path);
        addSpecificPath(path.getTo(), path);
        allPaths.add(path);
    }

    protected void addSpecificPath(TokenLocation from, Path path) {
        Collection<Path> pathCollection = specificPaths.get(from);
        if(pathCollection == null) pathCollection = new HashSet<>();
        pathCollection.add(path);
        specificPaths.put(from, pathCollection);
    }

    public Collection<Path> getSpecificPaths(TokenLocation from) {
        return specificPaths.get(from);
    }

    public Collection<Path> getAllPaths() {
        return allPaths;
    }

    protected boolean doesPathExist(TokenLocation from, TokenLocation to, PathType pathType) {
        Path testPath = new Path(from, to, pathType);
        return allPaths.contains(testPath);
    }

    protected void addPath(TokenLocation from, TokenLocation to, PathType pathType, Map<GoodType, Integer> goodsInventory) {
        if (!doesPathExist(from, to, pathType)) {
            GoodType goodType = getRandomGoodFromInventory(goodsInventory);
            if (goodType != null) {
                Path path = new Path(from, to, pathType);
                path.addGood(goodType);
                addPath(path);
            } else {
                throw new RuntimeException("trying to init path with no goods available");
            }
        }
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
