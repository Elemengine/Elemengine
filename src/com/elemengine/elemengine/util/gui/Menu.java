package com.elemengine.elemengine.util.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.metadata.LazyMetadataValue;
import org.bukkit.metadata.LazyMetadataValue.CacheStrategy;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.util.Events;
import com.elemengine.elemengine.util.Items;
import com.elemengine.elemengine.util.Maths;

import net.md_5.bungee.api.ChatColor;

public class Menu {

    private final HashMap<PlayerUser, OpenInstance> cache = new HashMap<>();

    private String title;
    private int sized;
    private BiConsumer<PlayerUser, MenuLayout> layout;

    private Menu(Builder builder) {
        this.title = builder.getTitle();
        this.sized = builder.getHeight().toSize();
        this.layout = builder.getLayout().orElseGet(() -> ((p, m) -> {}));
    }

    public void open(PlayerUser viewer) {
        OpenInstance inst;

        if (cache.containsKey(viewer)) {
            inst = cache.get(viewer);
        } else {
            inst = new OpenInstance(viewer, Bukkit.createInventory(viewer.getEntity(), sized, title));
            cache.put(viewer, inst);
        }

        inst.open(this.buildLayout(viewer));
    }

    public void close(PlayerUser viewer) {
        cache.computeIfPresent(viewer, (k, open) -> {
            open.close();
            return null;
        });
    }

    public void refresh(PlayerUser viewer) {
        Optional.ofNullable(cache.get(viewer)).ifPresent(open -> {
            open.refresh(this.buildLayout(viewer));
        });
    }

    /**
     * Saves the given data to the viewer under the menu namespace for loading
     * elsewhere.
     * 
     * @param viewer Who to save the data for
     * @param key    data name to save under
     * @param data   what to save
     */
    public static void save(PlayerUser viewer, String key, Object data) {
        viewer.getEntity().setMetadata("menu:" + key, new LazyMetadataValue(Elemengine.plugin(), CacheStrategy.NEVER_CACHE, () -> data));
    }

    /**
     * Loads data that is saved for the viewer under the menu namespace. This action
     * removes the data from storage, meaning it can only be called once after it's
     * saved.
     * 
     * @param <T>    The type of the data being retrieved
     * @param viewer Who to load the data for
     * @param key    data name to load from
     * @param type   Class representation data type
     * @return an empty optional if the viewer has no data stored under the menu
     *         namespace with the given key or if the given type does not match the
     *         data.
     */
    public static <T> Optional<T> load(PlayerUser viewer, String key, Class<T> type) {
        if (!viewer.getEntity().hasMetadata("menu:" + key)) {
            return Optional.empty();
        }

        Object data = viewer.getEntity().getMetadata("menu:" + key).get(0).value();
        viewer.getEntity().removeMetadata("menu:" + key, Elemengine.plugin());

        if (type.isInstance(data)) {
            return Optional.of(type.cast(data));
        }

        return Optional.empty();
    }

    private Button[] buildLayout(PlayerUser viewer) {
        MenuLayout ml = new MenuLayout(sized);
        layout.accept(viewer, ml);
        return ml.getLayout();
    }

    public static BasicBuilder builder() {
        return new BasicBuilder();
    }
    
    public static <T> PagedBuilder<T> paged(Class<T> itemType) {
        return new PagedBuilder<T>();
    }
    
    private static interface Builder {
        public String getTitle();
        public Height getHeight();
        public Optional<BiConsumer<PlayerUser, MenuLayout>> getLayout();
        
        public default Menu build() {
            return new Menu(this);
        }
    }

    public static class BasicBuilder implements Builder {

        private String title = "Unnamed Menu";
        private Height height = Height.ONE;
        private Optional<BiConsumer<PlayerUser, MenuLayout>> layout = Optional.empty();
        
        @Override
        public String getTitle() {
            return title;
        }
        
        @Override
        public Height getHeight() {
            return height;
        }
        
        @Override
        public Optional<BiConsumer<PlayerUser, MenuLayout>> getLayout() {
            return layout;
        }

        public BasicBuilder title(String title) {
            this.title = ChatColor.translateAlternateColorCodes('&', title);
            return this;
        }

        public BasicBuilder height(Height height) {
            this.height = height;
            return this;
        }

        public BasicBuilder layout(BiConsumer<PlayerUser, MenuLayout> layout) {
            this.layout = Optional.ofNullable(layout);
            return this;
        }
    }
    
    public static class PagedBuilder<T> implements Builder {
        
        private String title = "Unnamed Menu";
        private Height height = Height.ONE;
        private List<T> items = new ArrayList<>();
        private BiFunction<PlayerUser, T, Button> buttonMaker = (p, i) -> new Button(Items.create(Material.BARRIER, meta -> meta.setDisplayName("Unnamed button")), null);
        private Material pageMaterial = Material.SLIME_BALL, borderMaterial = Material.BLACK_STAINED_GLASS_PANE;
        private BiPredicate<PlayerUser, T> filter = (p, i) -> true;

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public Height getHeight() {
            return height;
        }

        @Override
        public Optional<BiConsumer<PlayerUser, MenuLayout>> getLayout() {
            return Optional.of((viewer, layout) -> {
                int size = layout.size();
                int rows = size / 9;
                int usable = size - rows * 2;
                int pageCount = (int) Math.ceil(items.size() / (double) usable);
                int page = Maths.clamp(Menu.load(viewer, "page", Integer.class).orElseGet(() -> 0), 0, pageCount - 1);
                
                Button border = new Button(Items.create(borderMaterial, meta -> {
                    meta.setDisplayName(ChatColor.DARK_GRAY + "Border");
                    meta.setLore(Arrays.asList());
                    meta.addItemFlags(ItemFlag.values());
                }), null);
                
                Button prevPage = new Button(Items.create(pageMaterial, meta -> {
                    meta.setDisplayName(ChatColor.GREEN + "Previous Page");
                    meta.setLore(Arrays.asList(ChatColor.WHITE + "Click for the previous page"));
                    meta.addItemFlags(ItemFlag.values());
                }), event -> {
                    Menu.save(viewer, "page", page - 1);
                    event.refreshClicked();
                });
                
                Button nextPage = new Button(Items.create(pageMaterial, meta -> {
                    meta.setDisplayName(ChatColor.GREEN + "Next Page");
                    meta.setLore(Arrays.asList(ChatColor.WHITE + "Click for the next page"));
                    meta.addItemFlags(ItemFlag.values());
                }), event -> {
                    Menu.save(viewer, "page", page + 1);
                    event.refreshClicked();
                });
                
                for (int i = 0; i < size / 9; ++i) {
                    layout.insert(i, 0, border);
                    layout.insert(i, 8, border);
                }
                
                if (page > 0) {
                    layout.insert(rows / 2, 0, prevPage);
                }
                
                if (page < pageCount - 1) {
                    layout.insert(rows / 2, 8, nextPage);
                }
                
                int i = page * usable;
                while (i < items.size()) {
                    T next = items.get(i++);
                    if (!filter.test(viewer, next)) {
                        continue;
                    }
                    
                    Button button = buttonMaker.apply(viewer, next);
                    if (button == null) {
                        continue;
                    }
                    
                    if (!layout.add(button)) {
                        break;
                    }
                }
            });
        }
        
        public PagedBuilder<T> title(String title) {
            this.title = ChatColor.translateAlternateColorCodes('&', title);
            return this;
        }

        public PagedBuilder<T> height(Height height) {
            this.height = height;
            return this;
        }
        
        public PagedBuilder<T> items(Collection<T> items) {
            this.items.clear();
            this.items.addAll(items);
            return this;
        }
        
        public PagedBuilder<T> addItems(Collection<T> items) {
            this.items.addAll(items);
            return this;
        }
        
        public PagedBuilder<T> filter(BiPredicate<PlayerUser, T> filter) {
            this.filter = filter;
            return this;
        }
        
        public PagedBuilder<T> buttonizer(BiFunction<PlayerUser, T, Button> buttonizer) {
            this.buttonMaker = buttonizer;
            return this;
        }
        
        public PagedBuilder<T> border(Material type) {
            this.borderMaterial = type;
            return this;
        }
        
        public PagedBuilder<T> pageTurner(Material type) {
            this.pageMaterial = type;
            return this;
        }
    }

    public enum Height {
        ONE, TWO, THREE, FOUR, FIVE, SIX;

        public int toSize() {
            return (this.ordinal() + 1) * 9;
        }
    }

    private class OpenInstance implements Listener {

        private PlayerUser viewer;
        private Inventory display;
        private Button[] layout;

        private OpenInstance(PlayerUser viewer, Inventory display) {
            this.viewer = viewer;
            this.display = display;
        }

        private void open(Button[] layout) {
            this.refresh(layout);
            viewer.getEntity().openInventory(display);
            Events.register(this, Elemengine.plugin());
        }

        private void close() {
            Events.unregister(this);
            viewer.getEntity().closeInventory();
        }

        private void refresh(Button[] layout) {
            this.layout = layout;

            for (int i = 0; i < layout.length; ++i) {
                display.setItem(i, layout[i] == null ? null : layout[i].getItemStack());
            }

            viewer.getEntity().updateInventory();
        }

        @EventHandler
        public void onClick(InventoryClickEvent event) {
            if (!event.getInventory().equals(display) || event.getCurrentItem() == null) {
                return;
            }

            event.setCancelled(true);
            if (layout[event.getSlot()] == null) {
                return;
            }

            layout[event.getSlot()].getAction().ifPresent((a) -> a.accept(new MenuEvent(viewer, event.getClick(), Menu.this)));
        }

        @EventHandler
        public void onClose(InventoryCloseEvent event) {
            if (!event.getInventory().equals(display)) {
                return;
            }

            Events.unregister(this);
            cache.remove(viewer);
        }
    }
}
