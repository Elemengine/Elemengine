package com.elementalplugin.elemental.util;

import org.bukkit.Location;
import org.bukkit.Sound;

public final class Sounds {

    private Sounds() {}

    public static void play(Location loc, Sound sound, float volume, float pitch) {
        loc.getWorld().playSound(loc, sound, volume, pitch);
    }

}
