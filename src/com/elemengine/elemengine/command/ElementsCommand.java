package com.elemengine.elemengine.command;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class ElementsCommand extends SubCommand {

    public ElementsCommand() {
        super("elements", "Get a list of the elements on the server", "/elemengine elements", Arrays.asList("sk"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(ChatColor.RED + "Too many args given: " + getUsage());
            return;
        }

        ComponentBuilder msgs = new ComponentBuilder("Available Elements").color(ChatColor.WHITE).bold(true);

        for (Element element : Element.streamValues().filter(s -> !s.getChildren().isEmpty()).sorted((a, b) -> a.getDisplayName().compareTo(b.getDisplayName())).toList()) {
            if (sender.hasPermission("elemental." + element.toString())) {
                msgs.append("\n" + element.getDisplayName(), FormatRetention.NONE).color(element.getChatColor())
                    .event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to view the abilities for this element")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemengine abilities " + element.toString()));

                for (Element child : element.getChildren()) {
                    msgs.append("\n - ", FormatRetention.NONE).color(ChatColor.WHITE)
                        .append(child.getDisplayName(), FormatRetention.NONE).color(child.getChatColor())
                        .event(new HoverEvent(Action.SHOW_TEXT, new Text("Click to view the abilities for this element")))
                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemengine abilities " + child.toString()));
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
