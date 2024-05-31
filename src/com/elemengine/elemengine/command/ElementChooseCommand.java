package com.elemengine.elemengine.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class ElementChooseCommand extends SubCommand {

    public ElementChooseCommand() {
        super("choose", "Choose a user's elements", "/elemengine choose [element] <user>", Arrays.asList("ch"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Specify a element!");
            return;
        } else if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments given: " + getUsage());
            return;
        }

        AbilityUser user;

        if (args.length == 2) {
            if (!hasPermission(sender, "choose.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to choose elements for others.");
                return;
            }

            user = Manager.of(Users.class).get(Bukkit.getPlayer(args[1])).get();

            if (user == null) {
                sender.sendMessage(ChatColor.RED + "No player found by name '" + ChatColor.GOLD + args[1] + ChatColor.RED + "'");
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This is a player only command argument.");
                return;
            }

            user = Manager.of(Users.class).get((Player) sender).get();
        }

        if (!user.getElements().isEmpty() && !hasPermission(sender, "rechoose")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to rechoose elements.");
            return;
        }

        Element element = Element.from(args[0]);

        if (element == null) {
            sender.sendMessage(ChatColor.RED + "No element found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        } else if (!element.getParents().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Cannot choose a subelement, must choose a parent!");
            return;
        } else if (!user.hasPermission("elemental.element." + element.toString().toLowerCase())) {
            sender.sendMessage(ChatColor.RED + "User does not have permission to use that element!");
            return;
        }

        Set<Element> toAdd = new HashSet<>();
        toAdd.add(element);
        for (Element child : element.getChildren()) {
            if (user.hasPermission("elemental.element." + child.toString().toLowerCase())) {
                toAdd.add(child);
            }
        }

        user.setElements(toAdd);
        sender.sendMessage(ChatColor.GOLD + "You have chosen " + element.getColoredName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.elements(true);
        } else if (args.length == 2) {
            return TabCompleteList.onlinePlayers();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
