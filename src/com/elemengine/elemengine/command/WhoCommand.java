package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.storage.Config;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class WhoCommand extends SubCommand {

    public WhoCommand() {
        super("who", "See information about users", "/elemengine who [username]", Arrays.asList("whois", "user"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Too many args given: " + getUsage());
            return;
        }

        Player player;
        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + "Player not found!");
                return;
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "Console is not an ability user!");
            return;
        }

        PlayerUser user = Manager.of(Users.class).get(player).getAs(PlayerUser.class);
        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Error occured while retrieving user!");
            return;
        }

        List<String> message = new ArrayList<>();

        message.add(ChatColor.GOLD + user.getEntity().getName());
        message.add("Skills: ");
        for (Skill skill : user.getSkills()) {
            message.add("- " + skill.getColoredName());
        }

        message.add("Binds: ");
        int i = 0;
        for (AbilityInfo ability : user.getBinds()) {
            message.add((++i) + " - " + (ability == null ? "empty" : ability.getDisplay()));
        }

        sender.sendMessage(message.toArray(new String[0]));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabCompleteList.onlinePlayers();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
