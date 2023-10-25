package com.elemengine.elemengine.command;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.storage.Config;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class BindCommand extends SubCommand {

    public BindCommand() {
        super("bind", "Bind an ability to your hotbar", "/elemengine bind <ability> [slot]", Arrays.asList("b"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Player only command!");
            return;
        } else if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + "Wrong arg count, expected: " + ChatColor.GOLD + this.getUsage());
            return;
        }

        Player player = (Player) sender;
        PlayerUser user = Manager.of(Users.class).get(player).getAs(PlayerUser.class);
        if (user == null) {
            sender.sendMessage(ChatColor.RED + "Something went wrong retrieving your user.");
            return;
        }

        Optional<AbilityInfo> ability = Manager.of(Abilities.class).getInfo(args[0]);
        if (!ability.isPresent()) {
            sender.sendMessage(ChatColor.RED + "No ability found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return;
        } else if (!user.canBind(ability.get())) {
            sender.sendMessage(ChatColor.RED + "Cannot bind the ability. Check if you have the ability's skill and permission node.");
            return;
        }

        int slot = user.getCurrentSlot();
        if (args.length == 2) {
            slot = Integer.parseInt(args[1]) - 1;
        }

        if (slot < 0 || slot > 8) {
            sender.sendMessage(ChatColor.RED + "Ability slots are only 1-9 on the hotbar.");
            return;
        }

        user.bindAbility(slot, ability.get());
        sender.sendMessage(ChatColor.GOLD + "Successfully bound " + ability.get().getDisplay() + ChatColor.GOLD + " to slot " + (slot + 1));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return null;
            }

            return TabCompleteList.bindables(Manager.of(Users.class).get((Player) sender).get());
        } else if (args.length == 2) {
            return TabCompleteList.slots();
        }

        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
