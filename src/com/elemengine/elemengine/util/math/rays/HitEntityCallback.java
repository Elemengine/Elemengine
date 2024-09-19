package com.elemengine.elemengine.util.math.rays;

import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public interface HitEntityCallback {

    void accept(Vector position, Entity entity);
}
