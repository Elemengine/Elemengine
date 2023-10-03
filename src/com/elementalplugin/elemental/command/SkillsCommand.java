package com.elementalplugin.elemental.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.elementalplugin.elemental.skill.Skill;
import com.elementalplugin.elemental.storage.Config;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class SkillsCommand extends SubCommand {

    public SkillsCommand() {
        super("skills", "Get a list of the skills on the server", "/elemental skills", Arrays.asList("sk"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Too many args given: " + getUsage());
            return;
        }

        ComponentBuilder msgs = new ComponentBuilder("Available Skills").color(ChatColor.WHITE).bold(true);

        for (Skill skill : Skill.streamValues().filter(s -> !s.getChildren().isEmpty()).sorted((a, b) -> a.getDisplayName().compareTo(b.getDisplayName())).toList()) {
            if (sender.hasPermission("elemental." + skill.toString())) {
                msgs.append("\n" + skill.getDisplayName(), FormatRetention.NONE).color(skill.getChatColor())
                    .event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to view the abilities for this skill")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemental abilities " + skill.toString()));

                for (Skill child : skill.getChildren()) {
                    msgs.append("\n - ", FormatRetention.NONE).color(ChatColor.WHITE)
                        .append(child.getDisplayName(), FormatRetention.NONE).color(child.getChatColor())
                        .event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to view the abilities for this skill")))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemental abilities " + child.toString()));
                }
            }
        }

        sender.spigot().sendMessage(msgs.create());
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public void postProcessed(Config config) {}

}
