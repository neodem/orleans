package com.neodem.orleans.engine.core.model;

import com.neodem.orleans.collections.Grouping;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vincent Fumo (neodem@gmail.com)
 * Created on 12/28/19
 */
public class ActionGrouping {
    private final ActionType action;
    private final List<Follower> accumulator = new ArrayList<>();
    private Grouping<Follower> template;
    private Follower techOverride = null;

    public ActionGrouping(ActionType action, Grouping<Follower> template) {
        this.template = template;
        this.action = action;
    }

    public boolean addTechOverride(Follower follower) {
        List<Follower> elements = template.getTemplate();
        elements.remove(follower);
        template = new Grouping<>(elements);
        techOverride = follower;
        return isComplete();
    }

    public Follower getTechOverride() {
        return techOverride;
    }

    public Grouping<Follower> getTemplate() {
        return template;
    }

    public List<Follower> getAccumulator() {
        return accumulator;
    }

    public boolean addFollower(Follower follower) {
        accumulator.add(follower);
        return isComplete();
    }

    public boolean isComplete() {
        if (accumulator.size() != template.size()) return false;
        Grouping<Follower> test = new Grouping<>(accumulator);
        return template.equals(test);
    }

    public void reset() {
        accumulator.clear();
    }

    public ActionType getAction() {
        return action;
    }
}
