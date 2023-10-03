package com.elementalplugin.elemental.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.ability.AbilityUser;
import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.user.Users;

import net.md_5.bungee.api.ChatColor;

public class SkillAddCommand extends SubCommand {

    public SkillAddCommand() {
        super("add", "Add a skill to a user", "/projectkorra add <skill> [user]", Arrays.asList("a"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Specify a skill!");
            return;
        } else if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Incorrect amount of arguments given: " + getUsage());
            return;
        }

        AbilityUser user;

        if (args.length == 2) {
            if (!hasPermission(sender, "add.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to add skills to others.");
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

        Skill skill = Skill.valueOf(args[0]);

        if (skill == null) {
            sender.sendMessage(ChatColor.RED + "No skill found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        } else if (!skill.getParents().isEmpty()) {
            boolean hasParent = false;
            for (Skill parent : skill.getParents()) {
                hasParent |= user.hasSkill(parent);
            }

            if (!hasParent) {
                sender.sendMessage(ChatColor.RED + "You don't have any of the parent skills required for that subskill!");
                return;
            }
        }

        user.addSkill(skill);
        sender.sendMessage(ChatColor.GOLD + "You have added " + skill.getColoredName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.skills(false);
        } else if (args.length == 2) {
            return TabCompleteList.onlinePlayers();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
