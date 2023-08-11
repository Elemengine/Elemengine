package com.elementalplugin.elemental.effect;

import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Location;
import org.bukkit.Particle;

import com.elementalplugin.elemental.effect.Effect.EffectBuilder;

public class ParticleData implements Spawnable {

    private Particle particle;
    private double ox = 0.0, oy = 0.0, oz = 0.0, extra = 0.0;
    private int amount = 1;
    private Object data = null;

    public ParticleData(Particle particle) {
        this.particle = particle;
    }

    private ParticleData(Particle particle, double ox, double oy, double oz, double extra, int amount, Object data) {
        this.particle = particle;
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
        this.extra = extra;
        this.amount = amount;
        this.data = data;
    }

    @Override
    public void spawn(Location location, double offsetX, double offsetY, double offsetZ) {
        double theta = ThreadLocalRandom.current().nextDouble(360);
        double yRand = ThreadLocalRandom.current().nextDouble(2) - 1;
        double scale = Math.sqrt(1 - yRand * yRand);

        double dx = offsetX * Math.cos(theta) * scale;
        double dy = yRand * offsetY;
        double dz = offsetZ * Math.sin(theta) * scale;

        location.getWorld().spawnParticle(particle, location.getX() + dx, location.getY() + dy, location.getZ() + dz, amount, ox, oy, oz, extra, data);
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public ParticleData offsets(double d) {
        return this.offsetX(d).offsetY(d).offsetZ(d);
    }

    public double offsetX() {
        return ox;
    }

    public ParticleData offsetX(double ox) {
        this.ox = ox;
        return this;
    }

    public double offsetY() {
        return oy;
    }

    public ParticleData offsetY(double oy) {
        this.oy = oy;
        return this;
    }

    public double offsetZ() {
        return oz;
    }

    public ParticleData offsetZ(double oz) {
        this.oz = oz;
        return this;
    }

    public double extra() {
        return extra;
    }

    public ParticleData extra(double extra) {
        this.extra = extra;
        return this;
    }

    public int amount() {
        return amount;
    }

    public ParticleData amount(int amount) {
        this.amount = amount;
        return this;
    }

    public Object data() {
        return data;
    }

    public ParticleData data(Object data) {
        this.data = data;
        return this;
    }

    public ParticleData clone() {
        return new ParticleData(particle, ox, oy, oz, extra, amount, data);
    }

    public EffectBuilder toEffectBuilder() {
        return Effect.builder().add(this);
    }
}
