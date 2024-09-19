package com.elemengine.elemengine.util.spigot;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.elemengine.elemengine.Elemengine;

public final class Threads {

    private Threads() {}

    public static BukkitTask onTimer(Runnable thread, long period, long offset) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                thread.run();
            }
            
        }.runTaskTimer(Elemengine.plugin(), offset, period);
    }
    
    public static BukkitTask onDelay(Runnable thread, long delay) {
        return new BukkitRunnable() {

            @Override
            public void run() {
                thread.run();
            }
            
        }.runTaskLater(Elemengine.plugin(), delay);
    }
}
