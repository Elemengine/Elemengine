package com.elemengine.elemengine.temporary;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Consumer;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;

import com.google.common.base.Preconditions;

public class TempBlock {
    
    static final Map<Block, TempBlock> CACHE = new HashMap<>();
    static final PriorityQueue<TempBlock> QUEUE = new PriorityQueue<>((a, b) -> (int) (a.getCurrentEndtime() - b.getCurrentEndtime()));

    private boolean inQueue = false;
    private Block block;
    private BlockState original;
    private final LinkedList<TempData> stack = new LinkedList<>();

    private TempBlock(Block block) {
        this.block = block;
        this.original = block.getState();
    }

    public Block getBlock() {
        return block;
    }

    public BlockData getCurrentData() {
        return stack.peek().data;
    }

    public long getCurrentEndtime() {
        return stack.peek().endTime();
    }

    public boolean currentlyHasPhysics() {
        return stack.peek().physics;
    }

    public TempData setData(BlockData data) {
        return this.setData(data, -1, false, true);
    }

    public TempData setData(BlockData data, long duration) {
        return this.setData(data, duration, false, true);
    }

    public TempData setData(BlockData data, boolean physics, boolean bendable) {
        return this.setData(data, -1, physics, bendable);
    }

    public TempData setData(BlockData data, long duration, boolean physics, boolean bendable) {
        this.block.setBlockData(data, false);

        TempData td = new TempData(this, data, duration, physics, bendable);
        this.stack.addFirst(td);

        if (this.inQueue && duration <= 0) {
            QUEUE.remove(this);
            this.inQueue = false;
        } else if (!this.inQueue && duration > 0) {
            QUEUE.add(this);
            this.inQueue = true;
        }
        
        return td;
    }

    public boolean isBendable() {
        if (stack.isEmpty()) {
            return true;
        }

        return stack.peek().bendable;
    }

    public void revertData(TempData data) {
        if (data == null || !stack.contains(data)) {
            return;
        }

        if (stack.peek() == data) {
            stack.poll();
            
            TempData td;
            while ((td = stack.peek()) != null) {
                if (!td.isDone()) {
                    this.block.setBlockData(td.data, false);
                    if (!this.inQueue && td.duration > 0) {
                        QUEUE.add(this);
                    }
                    break;
                }

                stack.poll();
            }
        } else {
            stack.remove(data);
        }

        data.onRevert.accept(this);
        if (stack.isEmpty()) {
            this.revert();
        }
    }

    public void revert() {
        CACHE.remove(this.block);
        QUEUE.remove(this);
        this.revertNoRemove();
    }
    
    void revertNoRemove() {
        for (TempData td : this.stack) {
            td.onRevert.accept(this);
        }
        
        TempData peek = this.stack.peek();
        this.stack.clear();
        
        if (peek != null && !peek.data.equals(this.block.getBlockData())) {
            return;
        }
        
        original.update();
    }

    public void destroy() {
        CACHE.remove(this.block);
        for (TempData td : this.stack) {
            td.onDestroy.accept(this);
        }
        this.stack.clear();
    }

    boolean progressDurations() {
        if (stack.isEmpty()) {
            return true;
        }

        TempData td = stack.peek();

        if (td.isDone()) {
            this.revertData(td);
            return true;
        }

        return false;
    }

    public static boolean exists(Block block) {
        return CACHE.containsKey(block);
    }

    public static TempBlock of(Block block) {
        Preconditions.checkArgument(block != null);
        return CACHE.computeIfAbsent(block, TempBlock::new);
    }

    public static class TempData {
        private TempBlock owner;
        private BlockData data;
        private long created = System.currentTimeMillis(), duration = -1;
        private boolean physics, bendable;
        private Consumer<TempBlock> onDestroy = (b) -> {}, onRevert = (b) -> {};

        private TempData(TempBlock owner, BlockData data, long duration, boolean physics, boolean bendable) {
            this.owner = owner;
            this.data = data;
            this.duration = duration;
            this.physics = physics;
            this.bendable = bendable;
        }

        public long lifetime() {
            return System.currentTimeMillis() - created;
        }

        public long endTime() {
            return created + duration;
        }

        public boolean isBendable() {
            return bendable;
        }

        public boolean isDone() {
            return duration > 0 && lifetime() >= duration;
        }

        public boolean hasPhysics() {
            return physics;
        }

        public TempData setDestroyEffect(Consumer<TempBlock> onDestroy) {
            if (onDestroy == null) {
                this.onDestroy = (b) -> {};
            } else {
                this.onDestroy = onDestroy;
            }
            return this;
        }

        public TempData setRevertEffect(Consumer<TempBlock> onRevert) {
            if (onRevert == null) {
                this.onRevert = (b) -> {};
            } else {
                this.onRevert = onRevert;
            }
            return this;
        }
        
        public void revert() {
            this.owner.revertData(this);
        }
    }
}
