package com.elemengine.elemengine;

import java.util.function.Predicate;
import java.util.function.Supplier;

import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.element.util.Earthbending;
import com.elemengine.elemengine.element.util.Waterbending;
import com.elemengine.elemengine.event.user.UserRequestSourceEvent;
import com.elemengine.elemengine.util.math.Vectors;
import com.elemengine.elemengine.util.spigot.Events;

public class Sourcing implements Listener {
    
    Sourcing() {}
    
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onRequest(UserRequestSourceEvent event) {
        if (event.hasSourceSupplier()) return;
        
        Predicate<Block> v = b -> false;
        boolean action = false;
        
        if (event.includesSourceType(Element.WATER)) {
            v = v.or(Waterbending::isWaterbendable);
            action = true;
        }
        
        if (event.includesSourceType(Element.PLANT)) {
            v = v.or(Waterbending::isPlantbendable);
            action = true;
        }
        
        if (event.includesSourceType(Element.EARTH)) {
            v = v.or(Earthbending::isEarthbendable);
            action = true;
        }
        
        if (event.includesSourceType(Element.METAL)) {
            v = v.or(Earthbending::isMetalbendable);
            action = true;
        }
        
        if (event.includesSourceType(Element.LAVA)) {
            v = v.or(Earthbending::isLavabendable);
            action = true;
        }
        
        if (!action) return;
        
        Location loc = event.getUser().getEyeLocation();
        Vector dir = loc.getDirection().multiply(0.5);
        Predicate<Block> valid = v;
        
        for (double d = 0; d < event.getRange(); d += 0.5) {
            RayTraceResult result = Vectors.rayTraceBlocks(loc, d, FluidCollisionMode.ALWAYS, false);
            
            if (result != null && result.getHitBlock() != null && valid.test(result.getHitBlock())) {
                Block block = result.getHitBlock();
                event.setSourceSupplier(() -> {
                    if (valid.test(block)) {
                        return block.getLocation();
                    }
                    
                    return null;
                });
                break;
            }
            
            loc.add(dir);
        }
    }
    
    /**
     * Request a source for the given element(s) within the specified range. This calls the
     * {@link UserRequestSourceEvent} and returns {@link UserRequestSourceEvent#getSourceSupplier()}.
     * This can be picked up by any addons to modify how sourcing works, while Elemengine has
     * a built in backup for any sourcing that doesn't already have a source supplier. The default
     * sourcing only checks the player's look direction with the range for a targeted source block,
     * doing nothing else with the supplied source.
     * @param user The {@link AbilityUser} to request a source for
     * @param range How far to allow the sourcing
     * @param first The main element to source for
     * @param others Any additional elements to search for
     * @return A {@link Supplier} of {@link Location} for the found source, or null if one wasn't found.
     */
    public static Supplier<Location> request(AbilityUser user, double range, Element first, Element...others) {
        return Events.call(new UserRequestSourceEvent(user, range, first, others)).getSourceSupplier();
    }
    
    /**
     * Request a source for the given element(s) within the specified range. This is a wrapper call
     * for {@link Sourcing#request(AbilityUser, double, Element, Element...)} that returns the supplied
     * Location, or null if the source supplier was null.
     * This can be picked up by any addons to modify how sourcing works, while Elemengine has
     * a built in backup for any sourcing that doesn't already have a source supplier. The default
     * sourcing only checks the player's look direction with the range for a targeted source block,
     * doing nothing else with the supplied source.
     * @param user The {@link AbilityUser} to request a source for
     * @param range How far to allow the sourcing
     * @param first The main element to source for
     * @param others Any additional elements to search for
     * @return A {@link Location} for the found source, or null if one wasn't found.
     */
    public static Location requestLocation(AbilityUser user, double range, Element first, Element...others) {
        Supplier<Location> supp = request(user, range, first, others);
        if (supp == null) return null;
        return supp.get();
    }
}
