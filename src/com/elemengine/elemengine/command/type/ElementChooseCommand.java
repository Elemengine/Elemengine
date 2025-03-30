package com.elemengine.elemengine.command.type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.spigot.Chat;

import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

public class ElementChooseCommand extends SubCommand {
    
    @Configure String noElement = "You must specify an element to add.";
    @Configure String usageErr = "Incorrect amount of arguments given, try: {usage}";
    @Configure String noOthers = "You don't have permission to choose elements to others.";
    @Configure String noPlayer = "No player found by the name '{input}'";
    @Configure String playerOnly = "Only players can add elements to themselves.";
    @Configure String noRechoose = "You don't have permission to rechoose elements.";
    @Configure String elementNotFound = "No element found from '{input}'";
    @Configure String noSubelements = "Subelements cannot be chosen, only parent elements.";
    @Configure String noPermission = "Target does not have permission to use that element.";
    @Configure String success = "The {element} element has been chosen.";

    public ElementChooseCommand() {
        super("choose", "Choose a user's elements", "/elemengine choose [element] <user>", Arrays.asList("ch"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + noElement);
            return;
        } else if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + usageErr.replace("{usage}", getUsage()));
            return;
        }

        AbilityUser user;

        if (args.length == 2) {
            if (!hasPermission(sender, "choose.others")) {
                sender.sendMessage(ChatColor.RED + noOthers);
                return;
            }

            user = Manager.of(Users.class).get(Bukkit.getPlayer(args[1])).get();

            if (user == null) {
                sender.sendMessage(ChatColor.RED + noPlayer.replace("{input}", args[1]));
                return;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + playerOnly);
                return;
            }

            user = Manager.of(Users.class).get((Player) sender).get();
        }

        if (!user.getElements().isEmpty() && !hasPermission(sender, "rechoose")) {
            sender.sendMessage(ChatColor.RED + noRechoose);
            return;
        }

        Element element = Element.from(args[0]);

        if (element == null) {
            sender.sendMessage(ChatColor.RED + elementNotFound.replace("{input}", args[0]));
            return;
        } else if (!element.getParents().isEmpty()) {
            sender.sendMessage(ChatColor.RED + noSubelements);
            return;
        } else if (!user.hasPermission("elemengine.element." + element.toString().toLowerCase())) {
            sender.sendMessage(ChatColor.RED + noPermission);
            return;
        }

        Set<Element> toAdd = new HashSet<>();
        toAdd.add(element);
        for (Element child : element.getChildren()) {
            if (user.hasPermission("elemengine.element." + child.toString().toLowerCase())) {
                toAdd.add(child);
            }
        }

        user.setElements(toAdd);
        sender.sendMessage(Chat.format(success, Map.of("{element}", element.createComponent())).colorIfAbsent(NamedTextColor.GREEN));
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabComplete.elements(true);
        } else if (args.length == 2) {
            return TabComplete.onlinePlayers();
        }

        return TabComplete.ERROR;
    }

    @Override
    public void postProcessed(Config config) {}

}
