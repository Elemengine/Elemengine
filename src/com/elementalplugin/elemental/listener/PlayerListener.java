package com.elementalplugin.elemental.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.ability.AbilityUser;
import com.elementalplugin.elemental.ability.util.AbilityBoard;
import com.elementalplugin.elemental.event.user.UserBindChangeEvent;
import com.elementalplugin.elemental.event.user.UserCooldownEndEvent;
import com.elementalplugin.elemental.event.user.UserCooldownStartEvent;
import com.elementalplugin.elemental.user.PlayerUser;
import com.elementalplugin.elemental.user.Users;

/**
 * Listens for events relating to players
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerUser user = (PlayerUser) Users.manager().load(event.getPlayer());
        new BukkitRunnable() {

            @Override
            public void run() {
                AbilityBoard.from(user).ifPresent(AbilityBoard::show);
            }

        }.runTaskLater(JavaPlugin.getPlugin(Elemental.class), 2);
    }

    private void playerLeave(Player player) {
        AbilityUser user = Users.manager().get(player).get();
        if (user == null) {
            return;
        }

        user.stopInstances();
        Users.manager().save(user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLeave(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        playerLeave(event.getPlayer());
    }

    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        AbilityBoard.from(event.getPlayer()).ifPresent(b -> b.switchSlot(event.getNewSlot()));
    }

    @EventHandler
    public void onCooldownStart(UserCooldownStartEvent event) {
        if (!(event.getUser() instanceof PlayerUser)) {
            return;
        }

        AbilityBoard.from((PlayerUser) event.getUser()).ifPresent(b -> b.cooldown(event.getTag(), true));
    }

    @EventHandler
    public void onCooldownEnd(UserCooldownEndEvent event) {
        if (!(event.getUser() instanceof PlayerUser)) {
            return;
        }

        AbilityBoard.from((PlayerUser) event.getUser()).ifPresent(b -> b.cooldown(event.getCooldown().getTag(), false));
    }

    @EventHandler
    public void onBindChange(UserBindChangeEvent event) {
        if (!(event.getUser() instanceof PlayerUser)) {
            return;
        }

        AbilityBoard.from((PlayerUser) event.getUser()).ifPresent(b -> b.updateBind(event.getSlot(), event.getResult()));
    }
}
