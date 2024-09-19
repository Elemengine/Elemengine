package com.elemengine.elemengine.temporary;

import java.net.http.WebSocket.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockFromToEvent;

import com.elemengine.elemengine.Manager;

public class Temps extends Manager implements Listener {

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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onFlow(BlockFromToEvent event) {
        if (!TempBlock.exists(event.getBlock())) {
            return;
        }

        event.setCancelled(!TempBlock.of(event.getBlock()).currentlyHasPhysics());
    }
}
