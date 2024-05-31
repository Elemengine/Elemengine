package com.elemengine.elemengine.ability.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public final class Cooldown {

    private static final Map<String, Tag> CACHE = new HashMap<>();

    private final Tag tag;
    private final long start;
    private long duration = 0;

    public Cooldown(Tag tag) {
        this.tag = tag;
        this.start = System.currentTimeMillis();
    }

    public Tag getTag() {
        return tag;
    }

    public long getStartTime() {
        return start;
    }

    public long getEndTime() {
        return start + duration;
    }

    public long getDuration() {
        return duration;
    }

    public void addDuration(long duration) {
        this.duration += duration;
    }

    public long getRemaining() {
        return getEndTime() - System.currentTimeMillis();
    }

    public static Optional<Tag> tag(String text) {
        return Optional.ofNullable(CACHE.get(text));
    }

    public static Tag tag(String text, ChatColor color, boolean visible) {
        return tag(text, new ComponentBuilder(text).color(color).build(), visible);
    }

    public static Tag tag(String internal, BaseComponent component, boolean visible) {
        if (CACHE.containsKey(internal)) {
            return CACHE.get(internal);
        }

        Tag tag = new Tag(internal, component, visible);
        CACHE.put(internal, tag);
        return tag;
    }

    public static class Tag {

        private final String internal;
        private final BaseComponent component;
        private final boolean visible;

        private Tag(String internal, BaseComponent component, boolean visible) {
            this.internal = internal;
            this.component = component;
            this.visible = visible;
        }

        public String getInternal() {
            return internal;
        }

        public BaseComponent getComponent() { return component; }

        public boolean isVisible() {
            return visible;
        }

        @Override
        public String toString() {
            return internal;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Tag)) {
                return false;
            }

            return internal.equals(((Tag) other).internal);
        }

        @Override
        public int hashCode() {
            return internal.hashCode();
        }
    }
}
