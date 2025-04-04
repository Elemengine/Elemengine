package com.elemengine.elemengine.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.command.CommandSender;

import com.elemengine.elemengine.storage.configuration.Configurable;

public abstract class SubCommand implements Configurable {

    private String name, description, usage;
    private List<String> aliases;
    
    UUID uuid;

    public SubCommand(String name, String description, String usage, List<String> aliases) {
        this.name = name.toLowerCase();
        this.description = description;
        this.usage = usage;
        this.aliases = new ArrayList<>(aliases);
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getUsage() {
        return usage;
    }

    public final List<String> getAliases() {
        return new ArrayList<>(aliases);
    }

    @Override
    public String getFolderName() {
        return "commands";
    }

    @Override
    public String getFileName() {
        return name;
    }
    
    /**
     * Check if the sender has permission for this command. Permissions
     * are structured like <code>elemengine.command.[command name]</code>
     * @param sender Who to check for permission
     * @return true if the sender has permission
     */
    public final boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("elemengine.command." + name);
    }

    /**
     * Check if the sender has permission for this command with an extra
     * attachment. Permissions are structured like <code>elemengine.command.[command name].[extra]</code>
     * 
     * @param sender Who to check for permission
     * @param extra The extra attachment for the permission
     * @return true if the sender has permission
     */
    public final boolean hasPermission(CommandSender sender, String extra) {
        return sender.hasPermission("elemengine.command." + name + "." + extra);
    }

    /**
     * Execute this command for the given command sender and arguments.
     * 
     * @param sender Who is using the command
     * @param args Command arguments after this command name
     */
    public abstract void execute(CommandSender sender, String[] args);

    public abstract TabComplete tabComplete(CommandSender sender, String[] args);

}
