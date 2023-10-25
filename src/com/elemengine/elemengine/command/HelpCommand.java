package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.bukkit.command.CommandSender;

import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.Bindable;
import com.elemengine.elemengine.ability.combo.Combo;
import com.elemengine.elemengine.skill.Skill;
import com.elemengine.elemengine.storage.Config;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class HelpCommand extends SubCommand {

    public HelpCommand() {
        super("help", "Get information on various topics", "/elemengine help <topic>", Arrays.asList("h", "info"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage("Use this command on a specific topic, including other command names, ability names, and skill names. Prefix the name with what it is, like 'command:help'.");
            return;
        } else if (args.length > 1) {
            sender.sendMessage(ChatColor.RED + "Too many args, only give one topic.");
            return;
        }

        String[] split = args[0].toLowerCase().split(":");
        BaseComponent[] message;

        switch (split[0]) {
        case "command":
            message = command(split[1], sender);
            break;
        case "ability":
            message = ability(split[1], sender);
            break;
        case "skill":
            message = skill(split[1], sender);
            break;
        default:
            message = new ComponentBuilder("Unknown topic type").color(ChatColor.RED).create();
            break;
        }

        sender.spigot().sendMessage(message);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 1) {
            return null;
        }

        List<String> tabs = new ArrayList<>();
        Commands.manager().registered().stream().map(s -> "command:" + s).forEach(tabs::add);
        Abilities.manager().registered().stream().map(a -> "ability:" + a.getName()).forEach(tabs::add);
        Skill.streamValues().map(s -> "skill:" + s.toString()).forEach(tabs::add);

        return tabs;
    }

    private BaseComponent[] skill(String arg, CommandSender sender) {
        Skill skill = Skill.valueOf(arg);
        if (skill == null) {
            return new ComponentBuilder("No skill from that name!").color(ChatColor.RED).create();
        }

        if (!sender.hasPermission("elemental." + skill.toString().toLowerCase())) {
            return new ComponentBuilder("You don't have permission to view info on that skill.").color(ChatColor.RED).create();
        }

        ComponentBuilder msgs = new ComponentBuilder(skill.getDisplayName()).color(ChatColor.WHITE).bold(true);

        for (AbilityInfo ability : Abilities.manager().fromSkill(skill)) {
            if (sender.hasPermission("elemengine.ability." + ability.getName())) {
                msgs.append("\n" + ability.getName(), FormatRetention.NONE).color(ability.getDisplayColor()).event(new HoverEvent(Action.SHOW_TEXT, new Text(ability.getDescription())));

                if (ability instanceof Bindable) {
                    msgs.append(" ", FormatRetention.NONE)
                        .append("[bind]", FormatRetention.NONE)
                        .event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to bind this ability to your current slot")))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemengine bind " + ability.getName()));
                }
            }
        }

        return msgs.create();
    }

    private BaseComponent[] ability(String arg, CommandSender sender) {
        Optional<AbilityInfo> maybe = Abilities.manager().getInfo(arg);
        if (!maybe.isPresent()) {
            return new ComponentBuilder("No ability from that name!").color(ChatColor.RED).create();
        }

        AbilityInfo abil = maybe.get();
        if (!sender.hasPermission("elemengine.ability." + abil.getName())) {
            return new ComponentBuilder("You don't have permission to view that ability.").color(ChatColor.RED).create();
        }

        ComponentBuilder bldr = new ComponentBuilder();
        bldr.append(abil.getName() + " ").color(abil.getDisplayColor()).bold(true);

        if (abil instanceof Bindable) {
            bldr.append("[bind]", FormatRetention.NONE).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(abil.getDisplayColor() + "Click to bind this ability"))).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemental bind " + abil.getName()));
        }

        bldr.append("\n" + abil.getDescription(), FormatRetention.NONE).color(abil.getDisplayColor())
            .append("\nMade by: " + abil.getAuthor() + ", version: " + abil.getVersion(), FormatRetention.NONE).color(ChatColor.WHITE);

        if (abil instanceof Bindable bind) {
            bldr.append("\nBind: " + bind.getBindUsage(), FormatRetention.NONE);
        }

        if (abil instanceof Combo combo) {
            bldr.append("\nCombo: " + combo.getSequenceString(), FormatRetention.NONE);
        }

        return bldr.create();
    }

    private BaseComponent[] command(String arg, CommandSender sender) {
        Optional<SubCommand> maybe = Commands.manager().get(arg);

        if (!maybe.isPresent()) {
            return new ComponentBuilder("No command from that name!").color(ChatColor.RED).create();
        }

        SubCommand cmd = maybe.get();
        if (!sender.hasPermission("elemengine.command." + cmd.getName())) {
            return new ComponentBuilder("You don't have permission to view that command.").color(ChatColor.RED).create();
        }

        ComponentBuilder msgs = new ComponentBuilder(cmd.getName()).color(ChatColor.DARK_AQUA).bold(true)
                .append("\n" + cmd.getDescription(), FormatRetention.NONE).color(ChatColor.WHITE)
                .append("\nHow to use: ").color(ChatColor.DARK_AQUA)
                .append(cmd.getUsage(), FormatRetention.NONE).color(ChatColor.WHITE)
                .append("\nAliases: [").color(ChatColor.DARK_AQUA);

        boolean first = true;
        for (String alias : cmd.getAliases()) {
            first &= false;

            if (!first) {
                msgs.append(", ").color(ChatColor.DARK_AQUA);
            }

            msgs.append(alias, FormatRetention.NONE).color(ChatColor.WHITE);
        }

        return msgs.append("]").color(ChatColor.DARK_AQUA).create();
    }

    @Override
    public void postProcessed(Config config) {}
}
