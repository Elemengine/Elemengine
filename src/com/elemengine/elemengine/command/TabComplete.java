package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.List;

import com.elemengine.elemengine.ability.AbilityUser;

public class TabComplete {
    
    public static final TabComplete ERROR = new TabComplete(null);

    private final List<String> list;
    private boolean emptyError = true, shouldFilter = true;
    
    public TabComplete() {
        this.list = new ArrayList<>();
    }
    
    public TabComplete(boolean errorWhenEmpty) {
        this.list = new ArrayList<>();
        this.emptyError = errorWhenEmpty;
    }
    
    public TabComplete(List<String> list) {
        this.list = list;
    }
    
    public TabComplete(List<String> list, boolean errorWhenEmpty) {
        this.list = list;
        this.emptyError = errorWhenEmpty;
    }
    
    List<String> inner() {
        return list;
    }
    
    public TabComplete add(String option) {
        this.list.add(option);
        return this;
    }
    
    public TabComplete errorWhenEmpty(boolean error) {
        this.emptyError = error;
        return this;
    }
    
    public boolean errorWhenEmpty() {
        return emptyError;
    }
    
    public TabComplete shouldFilter(boolean filter) {
        this.shouldFilter = filter;
        return this;
    }
    
    public boolean shouldFilter() {
        return shouldFilter;
    }
    
    public boolean isEmpty() {
        return list.size() == 0;
    }
    
    public static TabComplete onlinePlayers() {
        return new TabComplete(TabCompleteList.onlinePlayers());
    }
    
    public static TabComplete abilities() {
        return new TabComplete(TabCompleteList.abilities());
    }

    public static TabComplete bindables(AbilityUser user) {
        return new TabComplete(TabCompleteList.bindables(user));
    }

    public static TabComplete slots() {
        return new TabComplete(TabCompleteList.slots());
    }

    public static TabComplete elements(boolean parentOnly) {
        return new TabComplete(TabCompleteList.elements(parentOnly));
    }
}
