package com.elemengine.elemengine.util.math.rays;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

public interface HitBlockCallback {

    void accept(Vector position, Block block, BlockFace face);
}
