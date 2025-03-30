package com.elemengine.elemengine.command.type;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.elemengine.elemengine.Manager;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.user.PlayerUser;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.spigot.Chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

public class WhoCommand extends SubCommand {
    
    @Configure String usageErr = "Unexpected arguments, try: {usage}";
    @Configure String noPlayer = "Player not found!";
    @Configure String userErr = "Error retrieving user information. Try again later.";

    public WhoCommand() {
        super("who", "See information about users", "/elemengine who [username]", Arrays.asList("whois", "user"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + usageErr.replace("usage", getUsage()));
            return;
        }

        Player player;
        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.RED + noPlayer);
                return;
            }
        } else if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage(ChatColor.RED + "This command sender is unable to use abilities.");
            return;
        }

        PlayerUser user = Manager.of(Users.class).get(player).getAs(PlayerUser.class);
        if (user == null) {
            sender.sendMessage(ChatColor.RED + userErr);
            return;
        }

        TextComponent.Builder bldr = Component.text();

        bldr.append(Component.text(user.getEntity().getName()).color(NamedTextColor.GOLD));
        bldr.append(Component.text("\nElements: "));
        for (Element element : Element.streamValues().filter(s -> !s.getChildren().isEmpty()).sorted((a, b) -> a.getDisplayName().compareTo(b.getDisplayName())).toList()) {
            if (!user.hasElement(element)) continue;
            
            bldr.append(Component.text("\n- ").append(element.createComponent()));
            for (Element child : element.getChildren()) {
                if (!user.hasElement(child)) continue;
                
                bldr.append(Component.text("\n  - ").append(child.createComponent()));
            }
        }

        bldr.append(Component.text("\nBinds: "));
        int i = 0;
        for (AbilityInfo ability : user.getBinds()) {
            i += 1;
            int slot = i;
            bldr.append(Chat.combine(ability.createComponent(), s -> "\n" + slot + " - " + (ability == null ? "empty" : s)));
        }

        sender.sendMessage(bldr);
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return TabComplete.onlinePlayers();
        }

        return TabComplete.ERROR;
    }

    @Override
    public void postProcessed(Config config) {}

}
