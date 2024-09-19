package com.elemengine.elemengine.util.spigot.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class DelaySoundPlayer extends SoundPlayer {

    private final int delay;
    
    private int counter = 0;
    
    public DelaySoundPlayer(Sound sound, int delay) {
        super(sound);
        this.delay = delay;
    }
    
    @Override
    public DelaySoundPlayer category(SoundCategory category) {
        super.category(category);
        return this;
    }
    
    @Override
    public DelaySoundPlayer volume(float volume) {
        super.volume(volume);
        return this;
    }
    
    @Override
    public DelaySoundPlayer pitch(float pitch) {
        super.pitch(pitch);
        return this;
    }
    
    public void reset() {
        this.counter = 0;
    }
    
    @Override
    public void playAt(Location loc, float deltaVolume, float deltaPitch) {
        if (counter < delay) {
            counter += 1;
            return;
        }
        
        counter = 0;
        super.playAt(loc, deltaVolume, deltaPitch);
    }
}
