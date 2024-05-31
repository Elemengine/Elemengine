package com.elemengine.elemengine.temporary;

import com.elemengine.elemengine.Manager;

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
        
        Molecule.updateAll();
    }

    @Override
    protected void clean() {
        for (TempBlock tb : TempBlock.CACHE.values()) {
            tb.revertNoRemove();
        }

        TempBlock.CACHE.clear();
        TempBlock.QUEUE.clear();
    }

}
