package com.elementalplugin.elemental.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;

import com.elementalplugin.elemental.Elemental;
import com.elementalplugin.elemental.Manager;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.util.reflect.DynamicLoader;
import com.google.common.base.Preconditions;

import net.md_5.bungee.api.ChatColor;

public class Commands extends Manager implements CommandExecutor, TabCompleter {

    private Map<String, SubCommand> cache = new HashMap<>();
    private Set<String> main = new HashSet<>();

    @Override
    protected int priority() {
        return 50;
    }

    @Override
    protected boolean active() {
        return false;
    }

    @Override
    protected void startup() {
        PluginCommand spigot = Elemental.plugin().getCommand("elemental");
        spigot.setExecutor(this);
        spigot.setTabCompleter(this);

        DynamicLoader.load(Elemental.plugin(), "com.elementalplugin.elemental.command", SubCommand.class, this::register);
    }

    @Override
    protected void tick() {}

    @Override
    protected void clean() {
        cache.clear();
        main.clear();
    }

    public void register(SubCommand cmd) {
        Preconditions.checkArgument(cmd != null, "Cannot register null command!");
        Preconditions.checkArgument(!cmd.getName().isBlank(), "Cannot register command with empty name!");
        Preconditions.checkArgument(!cache.containsKey(cmd.getName().toLowerCase()), "Cannot register command with existing name!");

        Config.process(cmd);

        cache.put(cmd.getName().toLowerCase(), cmd);
        main.add(cmd.getName().toLowerCase());

        List<String> aliases = cmd.getAliases();
        if (aliases == null) {
            aliases = Collections.emptyList();
        }

        for (String alias : aliases) {
            if (cache.containsKey(alias.toLowerCase())) {
                Elemental.plugin().getLogger().warning("Command '" + cmd.getName() + "' attempted to register under existing alias '" + alias + "'!");
                continue;
            }

            cache.put(alias.toLowerCase(), cmd);
        }
    }

    public Optional<SubCommand> get(String label) {
        return Optional.ofNullable(cache.get(label.toLowerCase()));
    }

    public Set<String> registered() {
        return new HashSet<>(main);
    }
    
    public List<String> filter(List<String> options, String input) {
        if (options == null) {
            return null;
        }

        options.removeIf((s) -> !s.toLowerCase().contains(input.toLowerCase()));
        return options;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("elemental")) {
            return false;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "Use [/elemental help] to see a list of commands");
            return true;
        }

        SubCommand cmd = this.get(args[0]).orElse(null);

        if (cmd == null) {
            sender.sendMessage(ChatColor.RED + "No subcommand found from '" + ChatColor.GOLD + args[0] + ChatColor.RED + "'");
            return true;
        }

        if (!sender.hasPermission("elemental.command." + cmd.getName())) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use that command!");
            return true;
        }

        cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!command.getName().equalsIgnoreCase("elemental")) {
            return null;
        }

        if (args.length == 1) {
            return main.stream().filter((s) -> s.toLowerCase().contains(args[0].toLowerCase())).collect(Collectors.toList());
        }

        SubCommand cmd = this.get(args[0]).orElse(null);

        if (cmd == null) {
            return null;
        }

        List<String> list = cmd.tabComplete(sender, Arrays.copyOfRange(args, 1, args.length));
        filter(list, args[args.length - 1]);
        
        if (list == null || list.isEmpty()) {
            list = Arrays.asList("Unexpected argument at index " + (args.length - 1) + ": '" + args[args.length - 1] + "'");
        }

        return list;
    }

    public static Commands manager() {
        return Manager.of(Commands.class);
    }
}
