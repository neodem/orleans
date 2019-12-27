package com.neodem.orleans.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * a non unique Collection that randomizes on each add.
 * <p>
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/26/19
 */
public class Bag<T> implements Iterable<T> {

    private List<T> data = new ArrayList<>();

    public Bag() {
    }

    /**
     * add an item to the bag
     *
     * @param t
     */
    public void add(T t) {
        data.add(t);
        Collections.shuffle(data);
    }

    /**
     * remove an item from the bag
     *
     * @return the item or null if empty
     */
    public T take() {
        if (data.isEmpty()) return null;
        T item = data.get(0);
        data.remove(0);
        return item;
    }

    @Override
    public Iterator<T> iterator() {
        return data.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
        data.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
        return data.spliterator();
    }
}
