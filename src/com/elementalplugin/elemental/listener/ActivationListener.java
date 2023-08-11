package com.elementalplugin.elemental.listener;

import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.ability.Abilities;
import com.elementalplugin.elemental.ability.AbilityUser;
import com.elementalplugin.elemental.ability.activation.Trigger;
import com.elementalplugin.elemental.event.skill.SkillChangeEvent;
import com.elementalplugin.elemental.user.Users;
import com.elementalplugin.elemental.util.Threads;
import com.elementalplugin.elemental.util.Threads.ScheduleDelay;

/**
 * Listens for events relating to ability activations
 */
public class ActivationListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        AbilityUser user = Users.manager().get(event.getPlayer()).get();

        if (user == null || event.getHand() != EquipmentSlot.HAND) {
            return;
            /*
             * } else if (event.getAction() == Action.LEFT_CLICK_AIR || (event.getAction()
             * == Action.LEFT_CLICK_BLOCK && event.useInteractedBlock() !=
             * Event.Result.DENY)) { Abilities.manager().activate(user, Trigger.LEFT_CLICK,
             * event);
             */
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK && (event.useInteractedBlock() != Event.Result.DENY)) {
            Abilities.manager().activate(user, Trigger.RIGHT_CLICK_BLOCK, event);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerSwing(PlayerAnimationEvent event) {
        AbilityUser user = Users.manager().get(event.getPlayer()).get();

        if (user == null || event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
            return;
        }

        Abilities.manager().activate(user, Trigger.LEFT_CLICK, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteractEntity(PlayerInteractAtEntityEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer()).get();

        if (user == null || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Abilities.manager().activate(user, Trigger.RIGHT_CLICK_ENTITY, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSneak(PlayerToggleSneakEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer()).get();

        if (user == null) {
            return;
        }

        Abilities.manager().activate(user, event.isSneaking() ? Trigger.SNEAK_DOWN : Trigger.SNEAK_UP, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getEntity().getUniqueId()).get();

        if (user == null) {
            return;
        }

        Abilities.manager().activate(user, Trigger.DAMAGED, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSprint(PlayerToggleSprintEvent event) {
        AbilityUser user = Manager.of(Users.class).get(event.getPlayer().getUniqueId()).get();

        if (user == null) {
            return;
        }

        Abilities.manager().activate(user, event.isSprinting() ? Trigger.SPRINT_ON : Trigger.SPRINT_OFF, event);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onUserSkillsChange(SkillChangeEvent event) {
        if (event.getHolder() instanceof AbilityUser) {
            Threads.schedule(() -> ((AbilityUser) event.getHolder()).refreshPassives(), new ScheduleDelay(1));
        }
    }
}
