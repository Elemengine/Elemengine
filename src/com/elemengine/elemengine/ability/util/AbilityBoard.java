package com.elemengine.elemengine.ability.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.type.Bindable;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.user.Users.UserCast;

import net.md_5.bungee.api.ChatColor;

public class AbilityBoard {

    private static Map<PlayerUser, AbilityBoard> CACHE = new HashMap<>();
    private static String[] base = { "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "empty", "    -- Extras --", "empty", "empty", "empty", "empty", "empty"
    };

    private BoardSlot[] slots = new BoardSlot[9];
    private LinkedList<Cooldown.Tag> misc = new LinkedList<>();
    private PlayerUser user;
    private int oldSlot;
    private Scoreboard board;
    private Objective obj;

    private AbilityBoard(PlayerUser user) {
        this.user = user;
        this.oldSlot = user.getCurrentSlot();
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.obj = board.registerNewObjective("abilityboard", Criteria.DUMMY, "Abilities");
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (int i = 0; i < slots.length; ++i) {
            slots[i] = new BoardSlot(board, obj, i).update("" + (oldSlot == i || i == 10 ? ChatColor.WHITE : ChatColor.DARK_GRAY), base[i]);
        }
    }

    public void hide() {
        user.getEntity().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    public void show() {
        user.getEntity().setScoreboard(board);
        this.update();
    }

    /**
     * Toggles visibility of the scoreboard
     * 
     * @return true if visible after toggle
     */
    public boolean toggle() {
        if (user.getEntity().getScoreboard().equals(this.board)) {
            this.hide();
            return false;
        }

        this.show();
        return true;
    }

    public AbilityBoard update() {
        int i = 0;
        for (AbilityInfo bind : user.getBinds()) {
            this.updateBind(i, bind);
            ++i;
        }

        /*
         * ++i; //skip over slot 10, never needs update
         * 
         * Iterator<Cooldown.Tag> iter = misc.iterator(); while (i < 15) { if
         * (iter.hasNext()) { Cooldown.Tag tag = iter.next();
         * slots[i].update(ChatColor.DARK_GRAY + "- ", tag.getColor() +
         * (ChatColor.STRIKETHROUGH + tag.getDisplay())); } else {
         * slots[i].update(ChatColor.DARK_GRAY + "- ", ChatColor.DARK_GRAY + "empty"); }
         * ++i; }
         */

        return this;
    }

    public void updateBind(int slot, AbilityInfo ability) {
        ChatColor color = slot == oldSlot ? ChatColor.WHITE : ChatColor.DARK_GRAY;
        slots[slot].update(color + "> ", ability == null ? color + "empty" : ability.getDisplay());
        if (ability != null && user.hasCooldown(ability)) {
            bindCooldown(slot, true);
        }
    }

    public void switchSlot(int newSlot) {
        int slot = oldSlot;
        oldSlot = newSlot;
        this.updateBind(slot, user.getBoundAbility(slot).orElse(null));
        this.updateBind(newSlot, user.getBoundAbility(newSlot).orElse(null));
    }

    public void cooldown(Cooldown.Tag tag, boolean added) {
        Optional<AbilityInfo> ability = Abilities.manager().getInfo(tag.getInternal());

        if (ability.isPresent()) {
            if (ability.get() instanceof Bindable) {
                for (int slot : user.getBinds().slotsOf(ability.get())) {
                    bindCooldown(slot, added);
                }
                return;
            }
        }

        miscCooldown(tag, added);
    }

    public void bindCooldown(int slot, boolean added) {
        user.getBinds().get(slot).ifPresent((ability) -> {
            slots[slot].team.setSuffix(added ? ability.getDisplayColor() + (ChatColor.STRIKETHROUGH + ability.getName()) : ability.getDisplay());
        });
    }

    public void miscCooldown(Cooldown.Tag tag, boolean added) {
        if (added) {
            misc.push(tag);
        } else {
            misc.remove(tag);
        }
        this.update();
    }

    public static Optional<AbilityBoard> from(PlayerUser player) {
        // return empty if disabled or world is disabled
        return Optional.of(CACHE.computeIfAbsent(player, AbilityBoard::new).update());
    }

    public static Optional<AbilityBoard> from(Player player) {
        UserCast user = Manager.of(Users.class).get(player);

        if (user.get() == null || !(user.get() instanceof PlayerUser)) {
            return Optional.empty();
        }

        return from(user.getAs(PlayerUser.class));
    }

    private static class BoardSlot {

        private Objective obj;
        private int slot;
        private Team team;
        private String entry;

        @SuppressWarnings("deprecation")
        public BoardSlot(Scoreboard board, Objective obj, int slot) {
            this.obj = obj;
            this.slot = slot + 1;
            this.team = board.registerNewTeam("slot" + slot);
            this.entry = ChatColor.values()[slot % 10] + "" + ChatColor.values()[slot % 16];

            team.addEntry(entry);
        }

        public BoardSlot update(String prefix, String name) {
            team.setPrefix(prefix);
            team.setSuffix(name);
            obj.getScore(entry).setScore(-slot);
            return this;
        }
    }
}
