package com.neodem.orleans.model;

import java.util.*;

/**
 * a set that randomizes on each add.
 * <p>
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class Bag<T> extends HashSet<T> implements Set<T> {

    public Bag() {
    }

    public Bag(Collection<? extends T> c) {
        super(c);
    }

    public Bag(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public Bag(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(T t) {
        boolean result = super.add(t);
        randomize();
        return result;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        for(T value : c) {
            super.add(value);
        }
        return true;
    }

    public void randomize() {
        List<T> list = new ArrayList<>(this);
        Collections.shuffle(list);
        this.clear();
        this.addAll(list);
    }
}
