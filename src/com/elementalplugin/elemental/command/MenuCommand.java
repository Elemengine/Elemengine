package com.elementalplugin.elemental.command;

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

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.ability.Abilities;
import com.elementalplugin.elemental.ability.AbilityInfo;
import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.storage.Configure;
import com.elementalplugin.elemental.user.PlayerUser;
import com.elementalplugin.elemental.user.Users;
import com.elementalplugin.elemental.util.Items;
import com.elementalplugin.elemental.util.gui.Button;
import com.elementalplugin.elemental.util.gui.Menu;
import com.elementalplugin.elemental.util.gui.Menu.Height;

import net.md_5.bungee.api.ChatColor;

public class MenuCommand extends SubCommand {

    @Configure String emptyBindMaterial = "BARRIER";

    private Material emptyBind;

    public MenuCommand() {
        super("menu", "Open a menu to edit or view your user info", "/elemental menu", Arrays.asList("gui", "m"));
    }

    @Override
    public void postProcessed(Config config) {
        try {
            emptyBind = Material.valueOf(emptyBindMaterial);
        } catch (Exception e) {
            Elemental.plugin().getLogger().warning("Unknown Material for emptyBind in menu command, using BARRIER");
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
                .title("&dMenu")
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
                    
                    if (user.hasPermission("elemental.command.add")) {
                        layout.insert(13, new Button(addMenu(), event -> {
                            menus.get(3).open(user);
                        }));
                    }
                    
                    boolean canChoose = (user.getSkills().isEmpty() && user.hasPermission("elemental.command.choose"))
                                     || (!user.getSkills().isEmpty() && user.hasPermission("elemental.command.rechoose"));
                    
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
                .title("&aBind Ability")
                .height(Height.SIX)
                .border(Material.GREEN_STAINED_GLASS_PANE)
                .items(
                    Abilities.manager().registered().stream()
                    .sorted((a1, a2) -> a1.getName().compareToIgnoreCase(a2.getName()))
                    .sorted((a1, a2) -> a1.getSkill().getDisplayName().compareToIgnoreCase(a2.getSkill().getDisplayName()))
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
            Menu.paged(Skill.class)
                .title("&bChoose Skill")
                .height(Height.THREE)
                .border(Material.LIGHT_BLUE_STAINED_GLASS_PANE)
                .items(
                    Skill.streamValues()
                    .sorted((s1, s2) -> s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName()))
                    .collect(Collectors.toList())
                )
                .filter((player, skill) -> skill.getParents().isEmpty() && !player.hasSkill(skill))
                .buttonizer((player, skill) -> {
                    return new Button(skillItem(skill, false, player), event -> {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "elemental choose " + skill.toString() + " " + event.getViewer().getEntity().getName());
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        //index 3 - add menu
        menus.add(
            Menu.paged(Skill.class)
                .title("&cAdd Skills")
                .height(Height.SIX)
                .border(Material.RED_STAINED_GLASS_PANE)
                .items(
                    Skill.streamValues()
                    .sorted((s1, s2) -> s1.getDisplayName().compareToIgnoreCase(s2.getDisplayName()))
                    .collect(Collectors.toList())
                )
                .filter((player, skill) -> {
                    if (player.hasSkill(skill)) {
                        return false;
                    }
                    
                    for (Skill parent : skill.getParents()) {
                        if (!player.hasSkill(parent)) {
                            return false;
                        }
                    }
                    
                    return true;
                })
                .buttonizer((player, skill) -> {
                    return new Button(skillItem(skill, false, player), event -> {
                        event.getViewer().addSkill(skill);
                        menus.get(0).open(event.getViewer());
                    });
                })
                .build()
        );
        
        //index 4 - skill menu
        menus.add(
            Menu.paged(Skill.class)
                .title("&dManage Skills")
                .height(Height.SIX)
                .items(Arrays.asList(Skill.values()))
                .filter((player, skill) -> player.hasSkill(skill))
                .buttonizer((player, skill) -> {
                    return new Button(skillItem(skill, true, player), event -> {
                        if (event.getClickType() == ClickType.LEFT) {
                            event.getViewer().toggle(skill);
                        } else if (event.getClickType() == ClickType.RIGHT && event.getViewer().hasPermission("elemental.command.remove")) {
                            event.getViewer().removeSkill(skill);
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
            meta.setDisplayName(ChatColor.GREEN + "Add Skills");
            meta.setLore(Arrays.asList(ChatColor.WHITE + "Open a menu to gain new skills"));
            meta.addItemFlags(ItemFlag.values());
        });
    }
    
    private ItemStack chooseMenu() {
        return Items.create(Material.PAPER, meta -> {
            meta.setDisplayName(ChatColor.GREEN + "Choose Skill");
            meta.setLore(Arrays.asList(ChatColor.WHITE + "Open a menu to choose a different skill"));
            meta.addItemFlags(ItemFlag.values());
        });
    }
    
    private ItemStack skillItem(Skill skill, boolean manageMenu, PlayerUser user) {
        return Items.create(skill.getMaterial(), meta -> {
            meta.setDisplayName(skill.getColoredName());
            
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.WHITE + skill.getDescription());
            
            if (manageMenu) {
                lore.add(ChatColor.LIGHT_PURPLE + "Left: " + ChatColor.YELLOW + "Toggle");
                
                String extra = user.hasPermission("elemental.command.remove") ? "" : ChatColor.STRIKETHROUGH.toString();
                lore.add(ChatColor.LIGHT_PURPLE + extra + "Right: " + ChatColor.YELLOW + extra + "Remove");
            }
            
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.values());
        });
    }

    private ItemStack abilityItem(AbilityInfo info) {
        return Items.create(info.getSkill().getMaterial(), meta -> {
            meta.setDisplayName(info.getDisplayColor() + info.getName());
            meta.setLore(Arrays.asList(info.getDescription()));
            meta.addItemFlags(ItemFlag.values());
        });
    }

    private ItemStack bindItem(int slot, AbilityInfo info) {
        Material type = emptyBind;
        String bind = ChatColor.WHITE + "NONE";

        if (info != null) {
            type = info.getSkill().getMaterial();
            bind = info.getDisplay();
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
        meta.setLore(player.getSkills().stream().map(s -> "- " + s.getColoredName()).collect(Collectors.toList()));
        meta.setOwnerProfile(player.getEntity().getPlayerProfile());
        meta.addItemFlags(ItemFlag.values());

        playerHead.setItemMeta(meta);
        return playerHead;
    }
}
