package com.elemengine.elemengine.command.type;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.spigot.Chat;

import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.ChatColor;

public class BindCommand extends SubCommand {
    
    private static final String TAG_ABILITY = "{ability}";
    private static final String TAG_SLOT = "{slot}";
    private static final String TAG_USAGE = "{usage}";
    private static final String TAG_INPUT = "{input}";
    
    @Configure String playerOnly = "Player only command!";
    @Configure String usageErr = "Wrong arg count, expected: {usage}";
    @Configure String noUser = "Something went wrong retrieving your user information.";
    @Configure String noAbility = "No ability found from '{input}'";
    @Configure String cannotBind = "You are not able to bind that ability.";
    @Configure String validSlots = "Slot must be a number from 1 to 9.";
    @Configure String success = "Successfully bound {ability} to slot {slot}!";

    public BindCommand() {
        super("bind", "Bind an ability to one of your hotbar slots.", "/elemengine bind <ability> [slot]", Arrays.asList("b"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + playerOnly);
            return;
        } else if (args.length < 1 || args.length > 2) {
            sender.sendMessage(ChatColor.RED + usageErr.replace(TAG_USAGE, ChatColor.GOLD + this.getUsage() + ChatColor.RED));
            return;
        }

        Player player = (Player) sender;
        PlayerUser user = Manager.of(Users.class).get(player).getAs(PlayerUser.class);
        if (user == null) {
            sender.sendMessage(ChatColor.RED + noUser);
            return;
        }

        Optional<AbilityInfo> ability = Manager.of(Abilities.class).getInfo(args[0]);
        if (!ability.isPresent()) {
            sender.sendMessage(ChatColor.RED + noAbility.replace(TAG_INPUT, ChatColor.GOLD + args[0] + ChatColor.RED));
            return;
        } else if (!user.canBind(ability.get())) {
            sender.sendMessage(ChatColor.RED + cannotBind);
            return;
        }

        int slot = user.getCurrentSlot();
        if (args.length == 2) {
            slot = Integer.parseInt(args[1]) - 1;
        }

        if (slot < 0 || slot > 8) {
            sender.sendMessage(ChatColor.RED + validSlots);
            return;
        }

        user.bindAbility(slot, ability.get());
        Map<String, Component> tags = Map.of(
            TAG_ABILITY, ability.get().createComponent(),
            TAG_SLOT, Component.text(slot + 1)
        );
        
        sender.sendMessage(Chat.format(success, tags));
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            if (!(sender instanceof Player)) {
                return null;
            }

            return TabComplete.bindables(Manager.of(Users.class).get((Player) sender).get());
        } else if (args.length == 2) {
            return TabComplete.slots();
        }

        return TabComplete.ERROR;
    }

    @Override
    public void postProcessed(Config config) {}

}
