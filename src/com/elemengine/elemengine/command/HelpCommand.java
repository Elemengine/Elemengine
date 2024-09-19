package com.elemengine.elemengine.command;

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
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.hover.content.Text;

public class HelpCommand extends SubCommand {
    
    private final Map<String, Topic> topics = new HashMap<>();
    
    @Configure String unknownTopic = "No topic found from '{input}'";

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
            sender.sendMessage("Use this command on a specific topic, including other command names, ability names, and element names.");
            return;
        } else if (args.length > 2) {
            sender.sendMessage(ChatColor.YELLOW + "Note: This command only takes two arguments.");
        }

        Topic topic = topics.get(args[0].toLowerCase());
        if (topic == null) {
            sender.spigot().sendMessage(new ComponentBuilder(unknownTopic.replace("{input}", args[0])).color(ChatColor.RED).build());
            return;
        }

        sender.spigot().sendMessage(topic.getSpecific(args[1], sender));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length > 2) {
            return null;
        }
        
        if (args.length == 1) {
            return new ArrayList<>(topics.keySet());
        } else if (args.length == 2) {
            Topic topic = topics.get(args[0].toLowerCase());
            if (topic != null) {
                return topic.tabItems();
            }
        }

        return null;
    }
    
    public void registerTopic(Topic topic) {
        Preconditions.checkArgument(!topic.name().isBlank(), "Attempted register of topic " + topic.getClass().getName() + " with no name.");
        Preconditions.checkArgument(!topics.containsKey(topic.name().toLowerCase()), "Attempted register of topic " + topic.getClass().getName() + ", name already in use.");
        
        topics.put(topic.name().toLowerCase(), topic);
        Config.process(topic);
    }
    
    public static abstract class Topic implements Configurable {
        public abstract String name();
        public abstract BaseComponent getSpecific(String arg, CommandSender sender);
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
        
        @Override
        public String name() {
            return "element";
        }
        
        @Override
        public BaseComponent getSpecific(String arg, CommandSender sender) {
            Element element;
            try {
                element = Element.valueOf(arg.toUpperCase());
            } catch (Exception e) {
                return new ComponentBuilder(unknown).color(ChatColor.RED).build();
            }

            if (!sender.hasPermission("elemengine." + element.toString().toLowerCase())) {
                return new ComponentBuilder("You don't have permission to view that element.").color(ChatColor.RED).build();
            }

            ComponentBuilder msgs = new ComponentBuilder(element.getDisplayName() + " Abilities\n").color(ChatColor.WHITE).bold(true);
            
            Iterator<AbilityInfo> iter = Abilities.manager().fromElement(element).iterator();
            while (iter.hasNext()) {
                AbilityInfo ability = iter.next();
                
                if (!sender.hasPermission("elemengine.ability." + ability.getName())) {
                    continue;
                }
                
                msgs.append(ability.createComponent(), FormatRetention.NONE)
                    .event(new HoverEvent(Action.SHOW_TEXT, new Text(ability.getDescription() + ChatColor.DARK_GRAY + "\nClick for more information")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemengine bind " + ability.getName()));
                
                if (!iter.hasNext()) {
                    break;
                }
                
                msgs.append(", ", FormatRetention.NONE);
            }

            return msgs.build();
        }

        @Override
        public List<String> tabItems() {
            return Element.streamValues().map(Element::toString).map(String::toLowerCase).collect(Collectors.toList());
        }

    }
    
    private static class AbilityTopic extends Topic {
        
        @Configure String unknown = "No ability found from that name!";
        
        @Override
        public String name() {
            return "ability";
        }

        @Override
        public BaseComponent getSpecific(String arg, CommandSender sender) {
            Optional<AbilityInfo> maybe = Abilities.manager().getInfo(arg);
            if (maybe.isEmpty()) {
                return new ComponentBuilder(unknown).color(ChatColor.RED).build();
            }

            AbilityInfo abil = maybe.get();
            if (!sender.hasPermission("elemengine.ability." + abil.getName())) {
                return new ComponentBuilder("You don't have permission to view that ability.").color(ChatColor.RED).build();
            }

            ComponentBuilder bldr = new ComponentBuilder();
            bldr.append(abil.createComponent()).bold(true);
            
            if (abil instanceof Bindable) {
                bldr.event(new HoverEvent(Action.SHOW_TEXT, new Text(ChatColor.GRAY + "Click here to bind this ability.")))
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/elemengine bind " + abil.getName()));
            }
            
            bldr.append(" v" + abil.getVersion() + " by " + abil.getAuthor(), FormatRetention.NONE).color(ChatColor.DARK_GRAY)
                .append("\n" + abil.getDescription(), FormatRetention.NONE);

            if (abil instanceof Bindable bind) {
                bldr.append("\nWhen Bound: " + bind.getBindUsage(), FormatRetention.NONE).color(ChatColor.GRAY);
            }

            if (abil instanceof Combo combo) {
                bldr.append("\nCombo: " + combo.getSequenceString(), FormatRetention.NONE).color(ChatColor.GRAY);
            }

            return bldr.build();
        }

        @Override
        public List<String> tabItems() {
            return Abilities.manager().registered().stream().map(AbilityInfo::getName).collect(Collectors.toList());
        }
        
    }
    
    private static class CommandTopic extends Topic {

        @Configure String unknown = "No command found from that name!";
        
        @Override
        public String name() {
            return "command";
        }

        @Override
        public BaseComponent getSpecific(String arg, CommandSender sender) {
            Optional<SubCommand> maybe = Commands.manager().get(arg);

            if (maybe.isEmpty()) {
                return new ComponentBuilder(unknown).color(ChatColor.RED).build();
            }

            SubCommand cmd = maybe.get();
            if (!sender.hasPermission("elemengine.command." + cmd.getName())) {
                return new ComponentBuilder("You don't have permission to view that command.").color(ChatColor.RED).build();
            }

            ComponentBuilder msgs = new ComponentBuilder("Command: " + cmd.getName()).color(ChatColor.DARK_AQUA).bold(true)
                    .append("\n" + cmd.getDescription(), FormatRetention.NONE).color(ChatColor.WHITE)
                    .append("\nHow to use: ").color(ChatColor.DARK_AQUA)
                    .append(cmd.getUsage(), FormatRetention.NONE).color(ChatColor.WHITE)
                    .append("\nAliases: [").color(ChatColor.DARK_AQUA);

            boolean first = true;
            for (String alias : cmd.getAliases()) {
                if (!first) {
                    msgs.append(", ").color(ChatColor.DARK_AQUA);
                }

                first = false;

                msgs.append(alias, FormatRetention.NONE).color(ChatColor.WHITE);
            }

            return msgs.append("]").color(ChatColor.DARK_AQUA).build();
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
        public BaseComponent getSpecific(String arg, CommandSender sender) {
            Optional<Addon> maybe = Elemengine.getAddon(arg);
            if (maybe.isEmpty()) {
                return new ComponentBuilder(unknown).color(ChatColor.RED).build();
            }
            
            Addon addon = maybe.get();
            ComponentBuilder msgs = new ComponentBuilder(addon.getName()).color(ChatColor.GOLD).bold(true)
                    .append(" v" + addon.getVersion(), FormatRetention.NONE).color(ChatColor.DARK_GRAY)
                    .append("\n" + addon.getDescription(), FormatRetention.NONE)
                    .append("\nMade by: " + addon.getAuthor());
            
            return msgs.build();
        }

        @Override
        public List<String> tabItems() {
            return Elemengine.listAddons().stream().map(Addon::getInternalName).collect(Collectors.toList());
        }
        
    }
}
