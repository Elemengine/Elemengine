package com.elemengine.elemengine.util.gui;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import com.elemengine.elemengine.util.Items;

import net.md_5.bungee.api.ChatColor;

public class Button {

    @FunctionalInterface
    public static interface ClickAction {
        public void accept(MenuEvent event);

        public default ClickAction andThen(ClickAction action) {
            return event -> {
                accept(event);
                action.accept(event);
            };
        }
    }

    /**
     * A {@link Button} with no item or click action
     */
    public static final Button EMPTY = new Button(null, null);

    /**
     * A {@link Button} for closing the open menu when clicked
     */
    public static final Button EXIT = new Button(Items.create(Material.BARRIER, meta -> {
        meta.setDisplayName(ChatColor.RED + "Exit Menu");
        meta.setLore(Arrays.asList(ChatColor.GRAY + "Click to exit this menu"));
        meta.addItemFlags(ItemFlag.values());
    }), MenuEvent::closeClicked);

    /**
     * A {@link Button} that fills in empty slots, uses gray stained glass
     */
    public static final Button FILLER = new Button(Items.create(Material.GRAY_STAINED_GLASS_PANE, meta -> {
        meta.setDisplayName(ChatColor.GRAY + "Filler");
        meta.setLore(Arrays.asList(ChatColor.DARK_GRAY + "Filler item, does nothing"));
        meta.addItemFlags(ItemFlag.values());
    }), null);

    private ItemStack itemStack;
    private ClickAction action;

    /**
     * Create a new {@link Button} for a GUI with the given ItemStack to display and
     * action to do when the item is clicked.
     * 
     * @param item        GUI display item
     * @param action      What to do when the item is clicked
     * @param replaceable Whether or not this item can be replaced within a gui
     */
    public Button(ItemStack item, ClickAction action) {
        this.itemStack = item;
        this.action = action;
    }

    /**
     * Get the {@link ItemStack} that is used as display. Changes to the returned
     * {@link ItemStack} are not guaranteed to happen to the one in the inventory.
     * 
     * @return display {@link ItemStack}
     */
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get the {@link ClickAction} to do when this {@link Button} is interacted with
     * 
     * @return click {@link ClickAction}
     */
    public Optional<ClickAction> getAction() {
        return Optional.ofNullable(action);
    }
}
