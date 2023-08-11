package com.elementalplugin.elemental.ability.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import net.md_5.bungee.api.ChatColor;

public final class Cooldown {

    private static Map<String, Tag> CACHE = new HashMap<>();

    private Tag tag;
    private long start, duration = 0;

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
        return tag(text, text, color, visible);
    }

    public static Tag tag(String internal, String display, ChatColor color, boolean visible) {
        if (CACHE.containsKey(internal)) {
            return CACHE.get(internal);
        }

        if (CACHE.containsKey(display)) {
            return CACHE.get(display);
        }

        Tag tag = new Tag(internal, display, color, visible);
        CACHE.put(internal, tag);
        CACHE.put(display, tag);
        return tag;
    }

    public static class Tag {

        private String internal, display;
        private ChatColor color;
        private boolean visible;

        private Tag(String internal, String display, ChatColor color, boolean visible) {
            this.internal = internal;
            this.display = display;
            this.color = color;
            this.visible = visible;
        }

        public String getInternal() {
            return internal;
        }

        public String getDisplay() {
            return display;
        }

        public ChatColor getColor() {
            return color;
        }

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
