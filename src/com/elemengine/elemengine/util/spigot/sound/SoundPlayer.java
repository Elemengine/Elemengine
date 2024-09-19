package com.elemengine.elemengine.util.spigot.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class SoundPlayer {

    private final Sound sound;
    private SoundCategory category = SoundCategory.PLAYERS;
    private float volume = 1.0f, pitch = 1.0f;
    
    public SoundPlayer(Sound sound) {
        this.sound = sound;
    }
    
    public SoundPlayer category(SoundCategory category) {
        this.category = category;
        return this;
    }
    
    public SoundPlayer volume(float volume) {
        this.volume = volume;
        return this;
    }
    
    public SoundPlayer pitch(float pitch) {
        this.pitch = pitch;
        return this;
    }
    
    public void playAt(Location loc, float volumeDelta, float pitchDelta) {
        loc.getWorld().playSound(loc, sound, category, volume + volumeDelta, pitch + pitchDelta);
    }
    
    public final void playAt(Location loc) {
        this.playAt(loc, 0, 0);
    }
}
