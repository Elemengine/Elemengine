package com.elementalplugin.elemental.user;

import java.util.concurrent.ThreadLocalRandom;

import com.elementalplugin.elemental.util.data.Box;

@FunctionalInterface
public interface MobSlotAI {

    Box<Integer> current = Box.of(0);

    /**
     * Slot selection AI that picks a random slot
     */
    public static MobSlotAI random() {
        return () -> {
            current.set(ThreadLocalRandom.current().nextInt(9));
            return current.get();
        };
    }

    /**
     * Slot selection AI that goes through slots incrementally, 0 to 8 (left to
     * right)
     */
    public static MobSlotAI incremental() {
        return () -> {
            current.set((current.get() + 1) % 9);
            return current.get();
        };
    }

    /**
     * Slot selection AI that goes through slots decrementally, 8 to 0 (right to
     * left)
     */
    public static MobSlotAI decremental() {
        return new MobSlotAI() {

            @Override
            public int slot() {
                return 8 - current.get();
            }

            @Override
            public int next() {
                current.set((current.get() + 1) % 9);
                return slot();
            }

        };
    }

    public default int slot() {
        return current.get();
    }

    public int next();
}
