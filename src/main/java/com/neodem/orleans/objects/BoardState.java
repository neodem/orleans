package com.neodem.orleans.objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class BoardState {
    private final Map<TokenLocation, Collection<Path>> specificPaths;
    private final Collection<Path> allPaths;

    public BoardState() {
        specificPaths = new HashMap<>();
        allPaths = new HashSet<>();
    }

    public void addPath(Path path) {
        allPaths.add(path);
        addSpecificPath(path.getFrom(), path);
        addSpecificPath(path.getTo(), path);
        allPaths.add(path);
    }

    private void addSpecificPath(TokenLocation from, Path path) {
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

    public boolean doesPathExist(TokenLocation from, TokenLocation to, PathType pathType) {
        Path testPath = new Path(from, to, pathType);
        return allPaths.contains(testPath);
    }
}
