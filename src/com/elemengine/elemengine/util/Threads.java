package com.elemengine.elemengine.util;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.elemengine.elemengine.Elemengine;

public final class Threads {

    private Threads() {}

    public static BukkitTask schedule(Runnable thread, ScheduleType type) {
        return type.schedule(thread);
    }

    public interface ScheduleType {
        public BukkitTask schedule(Runnable thread);
    }

    public static class ScheduleDelay implements ScheduleType {

        private long delay;

        public ScheduleDelay(long delay) {
            this.delay = delay;
        }

        @Override
        public BukkitTask schedule(Runnable thread) {
            return new BukkitRunnable() {

                @Override
                public void run() {
                    thread.run();
                }

            }.runTaskLater(Elemengine.plugin(), delay);
        }

    }

    public static class ScheduleTimer implements ScheduleType {

        private long period, offset;

        public ScheduleTimer(long period, long offset) {
            this.period = period;
            this.offset = offset;
        }

        @Override
        public BukkitTask schedule(Runnable thread) {
            return new BukkitRunnable() {

                @Override
                public void run() {
                    thread.run();
                }

            }.runTaskTimer(Elemengine.plugin(), offset, period);
        }

    }
}
