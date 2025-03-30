package com.elemengine.elemengine.command.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;

import com.elemengine.elemengine.Addon;
import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.type.Bindable;
import com.elemengine.elemengine.ability.type.combo.Combo;
import com.elemengine.elemengine.command.Commands;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.util.spigot.Chat;
import com.google.common.base.Preconditions;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;

public class HelpCommand extends SubCommand {
    
    private final Map<String, Topic> topics = new HashMap<>();
    
    @Configure String unknownTopic = "No topic found from '{input}'";
    @Configure String usageErr = "Use this command on a specific topic, including other command names, ability names, and element names.";
    @Configure String extraArgs = "Note: This command only takes two arguments.";
    
    public HelpCommand() {
        super("help", "Get information on various topics", "/elemengine help <topic> <specific>", Arrays.asList("h", "info"));
    }

    @Override
    public void postProcessed(Config config) {
        this.registerTopic(new CommandTopic());
        this.registerTopic(new AbilityTopic());
        this.registerTopic(new ElementTopic());
        this.registerTopic(new AddonTopic());
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.GOLD + usageErr);
            return;
        } else if (args.length > 2) {
            sender.sendMessage(ChatColor.YELLOW + extraArgs);
        }

        Topic topic = topics.get(args[0].toLowerCase());
        if (topic == null) {
            sender.sendMessage(ChatColor.RED + unknownTopic.replace("{input}", args[0]));
            return;
        }

        sender.sendMessage(topic.getSpecific(args[1], sender));
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        if (args.length > 2) {
            return TabComplete.ERROR;
        }
        
        if (args.length == 1) {
            return new TabComplete(new ArrayList<>(topics.keySet()));
        } else if (args.length == 2) {
            Topic topic = topics.get(args[0].toLowerCase());
            if (topic != null) {
                return new TabComplete(topic.tabItems());
            }
        }

        return TabComplete.ERROR;
    }
    
    public void registerTopic(Topic topic) {
        Preconditions.checkArgument(!topic.name().isBlank(), "Attempted register of topic " + topic.getClass().getName() + " with no name.");
        Preconditions.checkArgument(!topics.containsKey(topic.name().toLowerCase()), "Attempted register of topic " + topic.getClass().getName() + ", name already in use.");
        
        topics.put(topic.name().toLowerCase(), topic);
        Config.process(topic);
    }
    
    public static abstract class Topic implements Configurable {
        public abstract String name();
        public abstract ComponentLike getSpecific(String arg, CommandSender sender);
        public abstract List<String> tabItems();
        
        @Override
        public final String getFileName() {
            return name();
        }
        
        @Override
        public final String getFolderName() {
            return "commands/help_topics";
        }
        
        @Override
        public void postProcessed(Config config) {}
    }

    private static class ElementTopic extends Topic {

        @Configure String unknown = "No element found from that name!";
        @Configure String notFound = "The {element} element has no abilities associated with it!";
        @Configure String noPerm = "You don't have permission to view that element.";
        @Configure String hoverText = "Click for more information";
        
        @Override
        public String name() {
            return "element";
        }
        
        @Override
        public ComponentLike getSpecific(String arg, CommandSender sender) {
            Element element = Element.from(arg.toUpperCase());
            if (element == null) {
                return Component.text(unknown).color(NamedTextColor.RED);
            }

            if (!sender.hasPermission("elemengine." + element.toString().toLowerCase())) {
                return Component.text(noPerm).color(NamedTextColor.RED);
            }
            
            Iterator<AbilityInfo> iter = Abilities.manager().fromElement(element).iterator();
            if (!iter.hasNext()) {
                return Component.text(notFound.replace("{element}", element.getDisplayName())).color(NamedTextColor.RED);
            }

            TextComponent.Builder msgs = Component.text();
            msgs.append(Component.text(element.getDisplayName() + " Abilities\n ").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD));
            
            while (iter.hasNext()) {
                AbilityInfo ability = iter.next();
                
                if (!sender.hasPermission("elemengine.ability." + ability.getName())) {
                    continue;
                }
                
                msgs.append(
                    ability.createComponent()
                        .clickEvent(ClickEvent.runCommand("/elemengine help ability " + ability.getName()))
                        .hoverEvent(Chat.fromLegacy(ability.getDescription() + ChatColor.DARK_GRAY + "\n" + hoverText))
                );
                
                if (!iter.hasNext()) {
                    break;
                }
                
                msgs.append(Component.text(", "));
            }

            return msgs;
        }

        @Override
        public List<String> tabItems() {
            return Element.streamValues().map(Element::toString).map(String::toLowerCase).collect(Collectors.toList());
        }

    }
    
    private static class AbilityTopic extends Topic {
        
        @Configure String unknown = "No ability found from that name!";
        @Configure String noPerm = "You don't have permission to view that ability.";
        @Configure String hoverText = "Click here to bind this ability.";
        
        @Override
        public String name() {
            return "ability";
        }

        @Override
        public ComponentLike getSpecific(String arg, CommandSender sender) {
            Optional<AbilityInfo> maybe = Abilities.manager().getInfo(arg);
            if (maybe.isEmpty()) {
                return Component.text(unknown).color(NamedTextColor.RED);
            }

            AbilityInfo abil = maybe.get();
            if (!sender.hasPermission("elemengine.ability." + abil.getName())) {
                return Component.text(noPerm).color(NamedTextColor.RED);
            }
            
            TextComponent.Builder bldr = Component.text();
            bldr.append(abil.createComponent().decorate(TextDecoration.BOLD));
            
            bldr.append(Component.text(" v" + abil.getVersion() + " by " + abil.getAuthor()).color(NamedTextColor.GRAY))
                .append(Component.text("\n" + abil.getDescription()));

            if (abil instanceof Bindable bind) {
                bldr.hoverEvent(Component.text(hoverText)).clickEvent(ClickEvent.runCommand("/elemengine bind " + abil.getName()));
                bldr.append(Component.text("\nWhen Bound: " + bind.getBindUsage()).color(NamedTextColor.GRAY));
            }

            if (abil instanceof Combo combo) {
                bldr.append(Component.text("\nCombo: " + combo.getSequenceString()).color(NamedTextColor.GRAY));
            }

            return bldr;
        }

        @Override
        public List<String> tabItems() {
            return Abilities.manager().registered().stream().map(a -> a.getName().replace(" ", "_").toLowerCase()).collect(Collectors.toList());
        }
        
    }
    
    private static class CommandTopic extends Topic {

        @Configure String unknown = "No command found from that name!";
        @Configure String noPerm = "You don't have permission to view that command.";
        
        @Override
        public String name() {
            return "command";
        }

        @Override
        public ComponentLike getSpecific(String arg, CommandSender sender) {
            Optional<SubCommand> maybe = Commands.manager().get(arg);

            if (maybe.isEmpty()) {
                return Component.text(unknown).color(NamedTextColor.RED);
            }

            SubCommand cmd = maybe.get();
            if (!sender.hasPermission("elemengine.command." + cmd.getName())) {
                return Component.text(noPerm).color(NamedTextColor.RED);
            }
            
            String[] aliases = cmd.getAliases().stream().map(str -> ChatColor.WHITE + str).toArray(String[]::new);
            
            return Component.text()
                .append(Component.text("Command: " + cmd.getName()).color(NamedTextColor.DARK_AQUA).decorate(TextDecoration.BOLD))
                .append(Component.text("\n" + cmd.getDescription()).color(NamedTextColor.WHITE))
                .append(Component.text("\nHow to use: ").color(NamedTextColor.DARK_AQUA))
                .append(Component.text(cmd.getUsage()).color(NamedTextColor.WHITE))
                .append(Chat.fromLegacy(ChatColor.DARK_AQUA + "\nAliases: [" + String.join(ChatColor.DARK_AQUA + ", ", aliases) + ChatColor.DARK_AQUA + "]"));
        }

        @Override
        public List<String> tabItems() {
            return Commands.manager().registered().stream().collect(Collectors.toList());
        }
        
    }
    
    private static class AddonTopic extends Topic {

        @Configure String unknown = "No addon found from that name!";
        
        @Override
        public String name() {
            return "addon";
        }

        @Override
        public ComponentLike getSpecific(String arg, CommandSender sender) {
            Optional<Addon> maybe = Elemengine.getAddon(arg);
            if (maybe.isEmpty()) {
                return Component.text(unknown).color(NamedTextColor.RED);
            }
            
            Addon addon = maybe.get();
            
            return Component.text()
                    .append(Component.text(addon.getName()).color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD))
                    .append(Component.text(" v" + addon.getVersion()).color(NamedTextColor.DARK_GRAY))
                    .append(Component.text("\n" + addon.getDescription()).color(NamedTextColor.WHITE))
                    .append(Component.text("\nMade by: " + addon.getAuthor()).color(NamedTextColor.WHITE));
        }

        @Override
        public List<String> tabItems() {
            return Elemengine.listAddons().stream().map(Addon::getInternalName).collect(Collectors.toList());
        }
        
    }
}
