package com.elemengine.elemengine.command.type;

import java.util.Arrays;
import java.util.Map;

import org.bukkit.command.CommandSender;

import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.util.spigot.Chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class ElementsCommand extends SubCommand {
    
    @Configure String usageErr = "Too many args given, try: {usage}";
    @Configure String hoverText = "Click to view the abilities for this element";

    public ElementsCommand() {
        super("elements", "Get a list of the elements on the server", "/elemengine elements", Arrays.asList("sk"));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length > 0) {
            sender.sendMessage(Chat.format(usageErr, Map.of("{usage}", Component.text(getUsage()))).colorIfAbsent(NamedTextColor.RED));
            return;
        }

        TextComponent.Builder msgs = Component.text();
        msgs.append(Component.text("Available Elements").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));

        for (Element element : Element.streamValues().filter(s -> !s.getChildren().isEmpty()).sorted((a, b) -> a.getDisplayName().compareTo(b.getDisplayName())).toList()) {
            if (sender.hasPermission("elemental." + element.toString())) {
                msgs.appendNewline().append(
                    element.createComponent()
                        .hoverEvent(Chat.fromLegacy(hoverText))
                        .clickEvent(ClickEvent.runCommand("/elemengine help element " + element.toString()))
                );

                for (Element child : element.getChildren()) {
                    msgs.appendNewline().append(
                        Component.text("- ").append(
                            child.createComponent()
                                .hoverEvent(Chat.fromLegacy(hoverText))
                                .clickEvent(ClickEvent.runCommand("/elemengine help element " + child.toString()))
                        )
                    );
                }
            }
        }

        sender.sendMessage(msgs);
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        return TabComplete.ERROR;
    }

    @Override
    public void postProcessed(Config config) {}

}
