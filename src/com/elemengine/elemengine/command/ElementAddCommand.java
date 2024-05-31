package com.elemengine.elemengine.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class ElementAddCommand extends SubCommand {

    public ElementAddCommand() {
        super("add", "Add a element to a user", "/elemengine add <element> [user]", Arrays.asList("a"));
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
            if (!hasPermission(sender, "add.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to add elements to others.");
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

        Element element = Element.from(args[0]);

        if (element == null) {
            sender.sendMessage(ChatColor.RED + "No element found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        } else if (!element.getParents().isEmpty()) {
            boolean hasParent = false;
            for (Element parent : element.getParents()) {
                hasParent |= user.hasElement(parent);
            }

            if (!hasParent) {
                sender.sendMessage(ChatColor.RED + "You don't have any of the parent elements required for that subelement!");
                return;
            }
        }

        user.addElement(element);
        sender.sendMessage(ChatColor.GOLD + "You have added " + element.getColoredName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.elements(false);
        } else if (args.length == 2) {
            return TabCompleteList.onlinePlayers();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
