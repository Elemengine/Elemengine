package com.elementalplugin.elemental.temporary;

import java.util.HashSet;

import com.elementalplugin.elemental.Manager;

public class Temps extends Manager {

    @Override
    protected int priority() {
        return 90;
    }

    @Override
    protected boolean active() {
        return true;
    }

    @Override
    protected void startup() {}

    @Override
    protected void tick() {
        TempBlock tb;
        while ((tb = TempBlock.QUEUE.peek()) != null) {
            if (!tb.progressDurations()) {
                break;
            }
        }
    }

    @Override
    protected void clean() {
        for (TempBlock tb : new HashSet<>(TempBlock.CACHE.values())) {
            tb.revert();
        }

        TempBlock.CACHE.clear();
        TempBlock.QUEUE.clear();
    }

}
