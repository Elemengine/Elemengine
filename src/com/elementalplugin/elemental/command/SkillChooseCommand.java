package com.elementalplugin.elemental.command;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.ability.AbilityUser;
import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.skill.Skills;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.user.Users;

import net.md_5.bungee.api.ChatColor;

public class SkillChooseCommand extends SubCommand {

    public SkillChooseCommand() {
        super("choose", "Choose a user's skills", "/projectkorra choose [skill] <user>", Arrays.asList("ch"));
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
            if (!hasPermission(sender, "choose.others")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to choose skills for others.");
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

        if (!user.getSkills().isEmpty() && !hasPermission(sender, "rechoose")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to rechoose skills.");
            return;
        }

        Skill skill = Manager.of(Skills.class).get(args[0]);

        if (skill == null) {
            sender.sendMessage(ChatColor.RED + "No skill found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        } else if (!skill.getParents().isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Cannot choose a subskill, must choose a parent!");
            return;
        } else if (!user.hasPermission("projectkorra.skill." + skill.getName())) {
            sender.sendMessage(ChatColor.RED + "User does not have permission to use that skill!");
            return;
        }

        Set<Skill> toAdd = new HashSet<>();
        toAdd.add(skill);
        for (Skill child : skill.getChildren()) {
            if (user.hasPermission("projectkorra.skill." + child.getName())) {
                toAdd.add(child);
            }
        }

        user.setSkills(toAdd);
        sender.sendMessage(ChatColor.GOLD + "You have chosen " + skill.getColoredName());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.skills(true);
        } else if (args.length == 2) {
            return TabCompleteList.onlinePlayers();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
