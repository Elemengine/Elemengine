package com.elemengine.elemengine.util.spigot;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class Items {

    private Items() {}

    /**
     * Create an item and modify it's metadata.
     * 
     * @param type     Material of the item
     * @param metadata Metadata modifier
     * @return the created ItemStack
     */
    public static ItemStack create(Material type, Consumer<ItemMeta> metadata) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        metadata.accept(meta);

        item.setItemMeta(meta);
        return item;
    }

    /**
     * Create an item and modify it's specific metadata. If the given type does not
     * use the given metadata type, the item will be returned with no metadata
     * modified.
     * 
     * @param <T>      Type of the metadata (e.g. SkullData)
     * @param type     Material of the item
     * @param spec     Class of the metadata type
     * @param metadata Metadata modifier
     * @return the created ItemStack
     */
    public static <T extends ItemMeta> ItemStack create(Material type, Class<T> spec, Consumer<T> metadata) {
        ItemStack item = new ItemStack(type);
        ItemMeta meta = item.getItemMeta();

        if (spec.isAssignableFrom(meta.getClass())) {
            metadata.accept(spec.cast(meta));
            item.setItemMeta(meta);
        }

        return item;
    }
}
