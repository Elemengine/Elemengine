package com.elemengine.elemengine.ability.util;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.ability.AbilityInstance;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.event.stamina.StaminaChangeEvent;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.util.Events;

public final class Stamina {
    
    private static Config config = Config.from("stamina", "properties");
    
    static {
        config.addDefault("DefaultRegen", 0.15);
        config.save();
    }

    private AbilityUser user;
    private double current = 1.0, regen;
    private BossBar bar;
    private Set<AbilityInstance<?>> paused = new HashSet<>();
    private Set<AbilityInstance<?>> onRemove = new HashSet<>();

    public Stamina(AbilityUser user) {
        this.user = user;
        this.regen = config.get(FileConfiguration::getDouble, "DefaultRegen");
        this.bar = Bukkit.createBossBar("Bending Stamina", BarColor.GREEN, BarStyle.SOLID);
        this.updateBar();
    }

    public AbilityUser user() {
        return user;
    }

    public double getRegen() {
        return regen;
    }

    public double getAmount() {
        return current;
    }

    /**
     * Attempts to consume the given percent of stamina
     * 
     * @param percent how much stamina to consume
     * @return true if the entire percent can be used
     */
    public boolean consume(double percent) {
        StaminaChangeEvent event = Events.call(new StaminaChangeEvent(this, Math.abs(percent), true));
        if (event.isCancelled()) {
            return false;
        }

        double diff = this.current - Math.abs(event.getAmount());
        if (diff < 0) {
            return false;
        }

        this.current = diff;
        return true;
    }

    /**
     * Depletes up to the given amount of stamina
     * 
     * @param percent how much stamina can be used
     * @return percent of stamina actually used
     */
    public double deplete(double percent) {
        StaminaChangeEvent event = Events.call(new StaminaChangeEvent(this, Math.abs(percent), true));
        if (event.isCancelled()) {
            return 0;
        }

        double temp = this.current;
        this.current = Math.max(0, this.current - event.getAmount());
        return temp - this.current;
    }
    
    /**
     * Depletes the given percent of stamina and calculates the strength
     * based on the given modifier. This strength value can be multiplied
     * by ability values to weaken them if less stamina than given can be
     * depleted from this stamina.
     * @param percent how much stamina to deplete
     * @param modifier percent to remove from the strength
     * @return strength value to be multipled by ability values
     */
    public double depleteStrength(double percent, double modifier) {
        StaminaChangeEvent event = Events.call(new StaminaChangeEvent(this, Math.abs(percent), true));
        double amount = event.getAmount();
        
        if (amount == 0 || event.isCancelled()) {
            return 1.0 - modifier;
        }
        
        double temp = this.current;
        this.current = Math.max(0, this.current - amount);
        
        return 1.0 - modifier + modifier * (temp - current) / amount;
    }

    public void restore(double percent) {
        StaminaChangeEvent event = Events.call(new StaminaChangeEvent(this, Math.abs(percent), false));
        if (event.isCancelled()) {
            return;
        }

        this.current = Math.min(1.0, this.current + Math.abs(event.getAmount()));
    }

    public void regen(double deltaTime) {
        Iterator<AbilityInstance<?>> iter = onRemove.iterator();
        while (iter.hasNext()) {
            AbilityInstance<?> next = iter.next();
            if (next.ticksLived() < 0) {
                iter.remove();
                paused.remove(next);
            }
        }

        if (paused.isEmpty() && user.getEntity().getRemainingAir() >= user.getEntity().getMaximumAir()) {
            this.current = Math.min(1.0, this.current + deltaTime * regen);
        }

        this.updateBar();
    }

    public void pauseRegen(AbilityInstance<?> instance) {
        this.pauseRegen(instance, true);
    }

    public void pauseRegen(AbilityInstance<?> instance, boolean removeUnpauses) {
        this.paused.add(instance);
        if (removeUnpauses) {
            this.onRemove.add(instance);
        }
    }

    public void unpauseRegen(AbilityInstance<?> instance) {
        this.paused.remove(instance);
        this.onRemove.remove(instance);
    }

    public void modifyRegen(double change) {
        this.regen = Math.max(this.regen + change, 0);
    }

    private void updateBar() {
        bar.setProgress(current);
        if (current >= 0.5) {
            bar.setColor(BarColor.GREEN);
        } else if (current >= 0.1) {
            bar.setColor(BarColor.YELLOW);
        } else {
            bar.setColor(BarColor.RED);
        }

        if (user.getEntity() instanceof Player) {
            bar.addPlayer((Player) user.getEntity());
        }
    }
}
