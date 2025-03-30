package com.elemengine.elemengine.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.event.user.UserCreationEvent;
import com.elemengine.elemengine.storage.database.DBConnection;
import com.elemengine.elemengine.util.spigot.Events;

public class Users extends Manager implements Listener {

    private static final String 
        T_PLAYER_READ                   = "SELECT * FROM t_player WHERE uuid = '&1'",
        T_PLAYER_BINDS_READ             = "SELECT * FROM t_player_binds WHERE uuid = '&1'",
        T_PLAYER_BINDS_READ_SPECIFIC    = "SELECT * FROM t_player_binds WHERE uuid = '&1' AND bound_slot = &2",
        T_PLAYER_ELEMENTS_READ          = "SELECT * FROM t_player_elements WHERE uuid = '&1'",
        T_PLAYER_ELEMENTS_READ_SPECIFIC = "SELECT * FROM t_player_elements WHERE uuid = '&1' AND element_name = '&2'";
    
    private Map<UUID, AbilityUser> cache = new HashMap<>();
    private long prevTime = System.currentTimeMillis(), autoSave = 1000 * 60 * 60, nextSave = System.currentTimeMillis() + autoSave;

    @Override
    protected int priority() {
        return 70;
    }

    @Override
    protected boolean active() {
        return true;
    }

    @Override
    protected void startup() {
        prevTime = System.currentTimeMillis();
    }

    @Override
    protected void tick() {
        double deltaTime = (System.currentTimeMillis() - prevTime) / 1000D;
        Iterator<AbilityUser> iter = cache.values().iterator();

        boolean save = false;
        if (System.currentTimeMillis() >= nextSave) {
            save = true;
            nextSave = System.currentTimeMillis() + autoSave;
            Elemengine.plugin().getLogger().info("Autosaving...");
        }

        while (iter.hasNext()) {
            AbilityUser user = iter.next();

            if (user.shouldRemove()) {
                iter.remove();
                continue;
            }

            if (save) {
                this.save(user);
            }

            user.getStamina().regen(deltaTime);
            user.updateCooldowns();
        }

        prevTime = System.currentTimeMillis();
    }

    @Override
    protected void clean() {
        cache.values().forEach(this::save);
        cache.clear();
    }

    public boolean register(AbilityUser user) {
        if (user.getUniqueID() == null || !cache.containsKey(user.getUniqueID())) {
            return false;
        }

        cache.put(user.getUniqueID(), user);
        return true;
    }

    public UserCast get(LivingEntity lent) {
        if (lent == null) {
            return new UserCast();
        }

        return new UserCast(cache.get(lent.getUniqueId()));
    }

    public UserCast get(UUID uuid) {
        if (uuid == null) {
            return new UserCast();
        }

        return new UserCast(cache.get(uuid));
    }

    public AbilityUser load(Player player) {
        AbilityUser user = cache.getOrDefault(player.getUniqueId(), new PlayerUser(player));
        DBConnection db = Elemengine.database();

        try {
            db.read(T_PLAYER_READ.replace("&1", player.getUniqueId().toString()), rs -> {
                if (!rs.next()) {
                    return;
                }
                
                db.read(T_PLAYER_ELEMENTS_READ.replace("&1", player.getUniqueId().toString()), elementRs -> {
                    while (elementRs.next()) {
                        Element element = Element.from(elementRs.getString("element_name").toUpperCase());
                        if (element == null) {
                            continue;
                        }
    
                        user.addElement(element);
                        if (elementRs.getInt("toggled") != 0) {
                            user.toggle(element);
                        }
                    }
                });

                db.read(T_PLAYER_BINDS_READ.replace("&1", player.getUniqueId().toString()), abilityRs -> {
                    while (abilityRs.next()) {
                        int slot = abilityRs.getInt("bound_slot");
    
                        Manager.of(Abilities.class).getInfo(abilityRs.getString("ability_name")).ifPresent((ability) -> {
                            user.bindAbility(slot, ability);
                        });
                    }
                });
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        cache.put(player.getUniqueId(), user);
        Events.call(new UserCreationEvent(user));
        return user;
    }

    public void save(AbilityUser user) {
        if (user == null) {
            return;
        }

        DBConnection db = Elemengine.database();

        try {
            if (db.exists(T_PLAYER_READ.replace("&1", user.getUniqueID().toString()))) {
                // update eventually when t_pk_player has more columns?
            } else {
                db.send("INSERT INTO t_player VALUES ('" + user.getUniqueID() + "')");
            }

            for (Element element : Element.values()) {
                if (!user.hasElement(element)) {
                    db.send("DELETE FROM t_player_elements WHERE uuid = '" + user.getUniqueID() + "' AND element_name = '" + element.toString() + "'");
                } else if (db.exists(T_PLAYER_ELEMENTS_READ_SPECIFIC.replace("&1", user.getUniqueID().toString()).replace("&2", element.toString()))) {
                    db.send("UPDATE t_player_elements SET toggled = " + (user.isToggled(element) ? 1 : 0) + " WHERE uuid = '" + user.getUniqueID() + "' AND element_name = '" + element.toString() + "'");
                } else {
                    db.send("INSERT INTO t_player_elements VALUES ('" + user.getUniqueID() + "', '" + element.toString() + "', " + (user.isToggled(element) ? 1 : 0) + ")");
                }
            }

            int slot = 0;
            for (AbilityInfo ability : user.getBinds()) {
                if (ability == null) {
                    db.send("DELETE FROM t_player_binds WHERE uuid = '" + user.getUniqueID() + "' AND bound_slot = " + slot);
                } else if (db.exists(T_PLAYER_BINDS_READ_SPECIFIC.replace("&1", user.getUniqueID().toString()).replace("&2", "" + slot))) {
                    db.send("UPDATE t_player_binds SET ability_name = '" + ability.getName() + "' WHERE uuid = '" + user.getUniqueID() + "' AND bound_slot = " + slot);
                } else {
                    db.send("INSERT INTO t_player_binds VALUES ('" + user.getUniqueID() + "', " + slot + ", '" + ability.getName() + "')");
                }

                ++slot;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public Set<AbilityUser> registered() {
        return new HashSet<>(cache.values());
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.load(event.getPlayer());
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
        this.save(user);
    }

    public static Users manager() {
        return Manager.of(Users.class);
    }

    public static class UserCast {
        private AbilityUser user;

        private UserCast() {
            this.user = null;
        }

        private UserCast(AbilityUser user) {
            this.user = user;
        }

        /**
         * Return the AbilityUser
         * 
         * @return an AbilityUser from the given id
         */
        public AbilityUser get() {
            return user;
        }

        /**
         * Return the AbilityUser, but only if the given class can be cast upon it, null
         * otherwise.
         * 
         * @param <T>   type of the class to cast
         * @param clazz what to return the AbilityUser as
         * @return AbilityUser of the given type
         */
        public <T extends AbilityUser> T getAs(Class<T> clazz) {
            if (clazz == null) {
                return null;
            }

            if (clazz.isInstance(user)) {
                return clazz.cast(user);
            }

            return null;
        }
    }
}
