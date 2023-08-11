package com.elementalplugin.elemental.effect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Sound;

import com.elementalplugin.elemental.effect.Effect.EffectBuilder;

public class SoundData implements Spawnable {

    private Sound sound;
    private float volume = 0.5f, pitch = 0.5f;

    public SoundData(Sound sound) {
        this.sound = sound;
    }

    @Override
    public void spawn(Location location, double offsetX, double offsetY, double offsetZ) {
        double theta = ThreadLocalRandom.current().nextDouble(360);
        double yRand = ThreadLocalRandom.current().nextDouble(2) - 1;
        double scale = Math.sqrt(1 - yRand * yRand);

        double dx = offsetX * Math.cos(theta) * scale;
        double dy = yRand * offsetY;
        double dz = offsetZ * Math.sin(theta) * scale;

        location.getWorld().playSound(location.clone().add(dx, dy, dz), sound, volume, pitch);
    }

    public Sound sound() {
        return sound;
    }

    public SoundData sound(Sound sound) {
        this.sound = sound;
        return this;
    }

    public float volume() {
        return volume;
    }

    public SoundData volume(float volume) {
        this.volume = Math.max(0, volume);
        return this;
    }

    public float pitch() {
        return pitch;
    }

    public SoundData pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public EffectBuilder toEffectBuilder() {
        return Effect.builder().add(this);
    }
}
