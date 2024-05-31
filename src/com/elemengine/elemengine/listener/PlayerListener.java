package com.elemengine.elemengine.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.user.Users;

/**
 * Listens for events relating to players
 */
public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Users.manager().load(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerLeave(event.getPlayer());
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        playerLeave(event.getPlayer());
    }

    private void playerLeave(Player player) {
        AbilityUser user = Users.manager().get(player).get();
        if (user == null) {
            return;
        }

        user.stopInstances();
        Users.manager().save(user);
    }
}
