package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.Items;
import com.elemengine.elemengine.util.gui.Button;
import com.elemengine.elemengine.util.gui.Menu;
import com.elemengine.elemengine.util.gui.Menu.Height;

import net.md_5.bungee.api.ChatColor;

public class MenuCommand extends SubCommand {

    @Configure String emptyBindMaterial = "BARRIER";

    private Material emptyBind;

    public MenuCommand() {
        super("menu", "Open a menu to edit or view your user info", "/elemengine menu", Arrays.asList("gui", "m"));
    }

    @Override
    public void postProcessed(Config config) {
        try {
            emptyBind = Material.valueOf(emptyBindMaterial);
        } catch (Exception e) {
            Elemengine.plugin().getLogger().warning("Unknown Material for emptyBind in menu command, using BARRIER");
            emptyBind = Material.BARRIER;
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Command takes no args!");
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Command is player-only!");
            return;
        }
        
        if (!hasPermission(sender)) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to perform that command.");
            return;
        }

        PlayerUser player = Users.manager().get((Player) sender).getAs(PlayerUser.class);
        
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find user info.");
            return;
        }
        
        menus().get(0).open(player);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }
    
    private List<Menu> menus() {
        List<Menu> menus = new ArrayList<>();

        //index 0 - main menu
        menus.add(
            Menu.builder()
                .title("Bending Menu")
                .height(Height.FIVE)
                .layout((user, layout) -> {
                    user.getBinds().enumerator().forEachRemaining(bind -> {
                        layout.insert(bind.getSlot(), new Button(bindItem(bind.getSlot(), bind.getInfo()), event -> {
                            if (event.getClickType() == ClickType.LEFT) {
                                Menu.save(user, "slot", bind.getSlot());
                                menus.get(1).open(event.getViewer());
                            } else if (event.getClickType() == ClickType.RIGHT) {
                                user.clearBind(bind.getSlot());
                                event.refreshClicked();
                            }
                        }));
                    });
                    
                    layout.insert(12, new Button(playerHead(user), event -> {
                        menus.get(4).open(user);
                    }));
                    
                    if (user.hasPermission("elemengine.command.add")) {
                        layout.insert(13, new Button(addMenu(), event -> {
                            menus.get(3).open(user);
                        }));
                    }
                    
                    boolean canChoose = (user.getElements().isEmpty() && user.hasPermission("elemengine.command.choose"))
                                     || (!user.getElements().isEmpty() && user.hasPermission("elemengine.command.rechoose"));
                    
                    if (canChoose) {
                        layout.insert(14, new Button(chooseMenu(), event -> {
                            menus.get(2).open(user);
                        }));
                    }
        
                    while (layout.add(Button.FILLER));
                })
                .build()
        );

        //index 1 - bind menu
        menus.add(
            Menu.paged(AbilityInfo.class)
                .title("Bind Ability")
                .height(Height.SIX)
                .items(
                    Abilities.manager().registered().stream()
                    .sorted((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()))
                    .collect(Collectors.toList())
                )
                .filter((player, ability) -> player.canBind(ability))
                .buttonizer((player, ability) -> {
                    return new Button(abilityItem(ability), event -> {
                        Menu.load(event.getViewer(), "slot", Integer.class).ifPresent(slot -> {
                            event.getViewer().bindAbility(slot, ability);
                        });
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        //index 2 - choose menu
        menus.add(
            Menu.paged(Element.class)
                .title("Choose Element")
                .height(Height.THREE)
                .items(
                    Element.streamValues()
                    .sorted((s1, s2) -> s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName()))
                    .collect(Collectors.toList())
                )
                .filter((player, element) -> element.getParents().isEmpty() && !player.hasElement(element))
                .buttonizer((player, element) -> {
                    return new Button(elementItem(element, false, player), event -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "elemengine choose " + element.toString() + " " + event.getViewer().getEntity().getName());
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        //index 3 - add menu
        menus.add(
            Menu.paged(Element.class)
                .title("Add Elements")
                .height(Height.SIX)
                .items(
                    Element.streamValues()
                    .sorted((s1, s2) -> s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName()))
                    .collect(Collectors.toList())
                )
                .filter((player, element) -> {
                    if (player.hasElement(element)) {
                        return false;
                    }
                    
                    for (Element parent : element.getParents()) {
                        if (!player.hasElement(parent)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .buttonizer((player, element) -> {
                    return new Button(elementItem(element, false, player), event -> {
                        event.getViewer().addElement(element);
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        //index 4 - element menu
        menus.add(
            Menu.paged(Element.class)
                .title("Manage Elements")
                .height(Height.SIX)
                .items(Arrays.asList(Element.values()))
                .filter((player, element) -> player.hasElement(element))
                .buttonizer((player, element) -> {
                    return new Button(elementItem(element, true, player), event -> {
                        if (event.getClickType() == ClickType.LEFT) {
                            event.getViewer().toggle(element);
                        } else if (event.getClickType() == ClickType.RIGHT && event.getViewer().hasPermission("elemengine.command.remove")) {
                            event.getViewer().removeElement(element);
                        }
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        return menus;
    }
    
    private ItemStack addMenu() {
        return Items.create(Material.FEATHER, meta -> {
            meta.setDisplayName(ChatColor.GREEN + "Add Elements");
            meta.setLore(Arrays.asList(ChatColor.WHITE + "Open a menu to gain new elements"));
            meta.addItemFlags(ItemFlag.values());
        });
    }
    
    private ItemStack chooseMenu() {
        return Items.create(Material.PAPER, meta -> {
            meta.setDisplayName(ChatColor.GREEN + "Choose Element");
            meta.setLore(Arrays.asList(ChatColor.WHITE + "Open a menu to choose a different element"));
            meta.addItemFlags(ItemFlag.values());
        });
    }
    
    private ItemStack elementItem(Element element, boolean manageMenu, PlayerUser user) {
        return Items.create(element.getMaterial(), meta -> {
            meta.setDisplayName(element.getColoredName());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + element.getDescription());
            
            if (manageMenu) {
                lore.add(ChatColor.LIGHT_PURPLE + "Left: " + ChatColor.YELLOW + "Toggle");
                
                String extra = user.hasPermission("elemengine.command.remove") ? "" : ChatColor.STRIKETHROUGH.toString();
                lore.add(ChatColor.LIGHT_PURPLE + extra + "Right: " + ChatColor.YELLOW + extra + "Remove");
            }
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.values());
        });
    }

    private ItemStack abilityItem(AbilityInfo info) {
        return Items.create(Material.PAPER, meta -> {
            meta.setDisplayName(info.createComponent().toLegacyText());
            meta.setLore(List.of(info.getDescription()));
            meta.addItemFlags(ItemFlag.values());
        });
    }

    private ItemStack bindItem(int slot, AbilityInfo info) {
        Material type = emptyBind;
        String bind = ChatColor.WHITE + "NONE";

        if (info != null) {
            type = Material.PAPER;
            bind = info.createComponent().toLegacyText();
        }

        String bound = bind;

        return Items.create(type, meta -> {
            meta.setDisplayName(ChatColor.WHITE + "Slot " + (slot + 1) + ": " + bound);
            meta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Left: " + ChatColor.YELLOW + "Rebind slot", ChatColor.LIGHT_PURPLE + "Right: " + ChatColor.YELLOW + "Clear slot"));
            meta.addItemFlags(ItemFlag.values());
        });
    }

    private ItemStack playerHead(PlayerUser player) {
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) playerHead.getItemMeta();

        meta.setDisplayName(ChatColor.WHITE + player.getEntity().getName());
        meta.setLore(player.getElements().stream().map(s -> "- " + s.getColoredName()).collect(Collectors.toList()));
        meta.setOwnerProfile(player.getEntity().getPlayerProfile());
        meta.addItemFlags(ItemFlag.values());

        playerHead.setItemMeta(meta);
        return playerHead;
    }
}
