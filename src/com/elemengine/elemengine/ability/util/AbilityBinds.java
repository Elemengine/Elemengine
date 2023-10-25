package com.elemengine.elemengine.ability.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.Bindable;

public class AbilityBinds implements Iterable<AbilityInfo> {

    public static enum AbilityBindResult {
        FAIL_OUT_OF_BOUNDS, FAIL_NONBINDABLE, SUCCESS;
    }

    private AbilityInfo[] binds;

    public AbilityBinds() {
        this.binds = new AbilityInfo[9];
    }

    public AbilityBinds(AbilityInfo[] binds) {
        this.binds = Arrays.copyOf(binds, 9);
    }

    public Optional<AbilityInfo> get(int index) {
        if (index < 0 || index > 8) {
            return Optional.empty();
        }

        return Optional.ofNullable(binds[index]);
    }

    public AbilityBindResult set(int index, AbilityInfo ability) {
        if (index < 0 || index > 8) {
            return AbilityBindResult.FAIL_OUT_OF_BOUNDS;
        } else if (ability != null && !(ability instanceof Bindable)) {
            return AbilityBindResult.FAIL_NONBINDABLE;
        }

        binds[index] = ability;
        return AbilityBindResult.SUCCESS;
    }

    public Set<Integer> slotsOf(AbilityInfo ability) {
        Set<Integer> slots = new HashSet<>();

        for (int i = 0; i < 9; ++i) {
            if (ability == binds[i]) {
                slots.add(i);
            }
        }

        return slots;
    }

    public void copy(AbilityBinds other) {
        binds = Arrays.copyOf(other.binds, 9);
    }

    @Override
    public AbilityBinds clone() {
        return new AbilityBinds(this.binds);
    }

    @Override
    public Iterator<AbilityInfo> iterator() {
        return new BindIterator(this);
    }

    public Iterator<AbilityBind> enumerator() {
        return new BindEnumerator(this);
    }

    private static class BindIterator implements Iterator<AbilityInfo> {

        private int cursor = -1;
        private AbilityInfo[] binds;

        private BindIterator(AbilityBinds abilityBinds) {
            this.binds = abilityBinds.binds;
        }

        @Override
        public boolean hasNext() {
            return cursor < 8;
        }

        @Override
        public AbilityInfo next() {
            return binds[++cursor];
        }

        /**
         * @deprecated Unsupported operation
         */
        @Deprecated
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static class AbilityBind {

        private int slot;
        private AbilityInfo info;

        private AbilityBind(int slot, AbilityInfo info) {
            this.slot = slot;
            this.info = info;
        }

        public int getSlot() {
            return slot;
        }

        public AbilityInfo getInfo() {
            return info;
        }
    }

    private static class BindEnumerator implements Iterator<AbilityBind> {

        private int cursor = -1;
        private AbilityInfo[] binds;

        private BindEnumerator(AbilityBinds abilityBinds) {
            this.binds = abilityBinds.binds;
        }

        @Override
        public boolean hasNext() {
            return cursor < 8;
        }

        @Override
        public AbilityBind next() {
            return new AbilityBind(++cursor, binds[cursor]);
        }

        /**
         * @deprecated Unsupported operation
         */
        @Deprecated
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
