package com.neodem.orleans.collections;

import org.springframework.util.Assert;

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
        Assert.notEmpty(elements, "Grouping needs at least one element");
        template = new ArrayList<>(elements.length);
        Collections.addAll(template, elements);
    }


    public Grouping(Collection<T> elements) {
        Assert.notEmpty(elements, "Grouping needs at least one element");
        template = new ArrayList<>(elements.size());
        template.addAll(elements);
    }

    public int size() {
        return template.size();
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
        int hashCode = 0;
        for(T element : template) {
            hashCode += element.hashCode();
        }
        return hashCode;
    }

    /**
     * test to see if this grouping 'can fit' into a given grouping
     *
     * @param testGrouping
     * @return true if all elements in this grouping can fit into the given grouping
     */
    public boolean canFitInto(Grouping<T> testGrouping) {
        if (template.size() > testGrouping.template.size()) return false;

        List<T> templateCopy = new ArrayList<>(template);
        boolean result = true;

        for (T element : testGrouping.template) {
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
