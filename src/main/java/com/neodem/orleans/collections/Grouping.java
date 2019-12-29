package com.neodem.orleans.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * a grouping of elements.  This impl. does not care about ordering!
 * <p>
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class Grouping<T> {

    private final List<T> template;

    public Grouping(T... elements) {
        template = new ArrayList<>(elements.length);
        Collections.addAll(template, elements);
    }


    public Grouping(Collection<T> elements) {
        template = new ArrayList<>(elements.size());
        template.addAll(elements);
    }

    public int size() {
        return template.size();
    }

    public List<T> getTemplate() {
        if (template.isEmpty()) return new ArrayList<>();

        List<T> copy = new ArrayList<>(template.size());
        copy.addAll(template);
        return copy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Grouping<?> grouping = (Grouping<?>) o;

        List<?> other = grouping.template;

        if (template.size() != other.size()) return false;

        List<T> templateCopy = new ArrayList<>(template);
        for (Object element : other) {
            if (templateCopy.isEmpty()) return false;
            if (templateCopy.contains(element)) {
                templateCopy.remove(element);
            } else {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (template.isEmpty()) return template.hashCode();

        int hashCode = 0;
        for (T element : template) {
            hashCode += element.hashCode();
        }
        return hashCode;
    }

    /**
     * test to see if this grouping 'can fit' into a given target grouping
     *
     * @param target
     * @return true if all elements in this grouping can fit into the given grouping
     */
    public boolean canFitInto(Grouping<T> target) {
        if (target.template.isEmpty()) return false;
        if (template.size() > target.template.size()) return false;

        List<T> templateCopy = new ArrayList<>(target.template);
        boolean result = true;

        for (T element : template) {
            if (templateCopy.isEmpty()) return true;
            if (templateCopy.contains(element)) {
                templateCopy.remove(element);
            } else {
                result = false;
            }
        }

        return result;
    }
}
