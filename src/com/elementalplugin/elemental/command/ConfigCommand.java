package com.elementalplugin.elemental.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.elementalplugin.elemental.ability.Abilities;
import com.elementalplugin.elemental.storage.Config;
import com.elementalplugin.elemental.storage.Configurable;
import com.elementalplugin.elemental.user.Users;

import net.md_5.bungee.api.ChatColor;

public class ConfigCommand extends SubCommand {

    public ConfigCommand() {
        super("config", "Control ability configuration in-game. Use the set subcommand to change values and then the reload subcommand to have the ability reconfigured.", "/e config <ability> [set <path> <value> | reload]" , Arrays.asList("configure", "cfg", "c"));
    }

    @Override
    public void postProcessed(Config config) {}

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "Expected: " + this.getUsage());
            return;
        }
        
        Abilities.manager().getInfo(args[0]).ifPresentOrElse(a -> {
            if (args.length == 1) {
                FileConfiguration fc = Config.from(a).get();
                List<String> message = new ArrayList<>();
                
                for (String key : fc.getKeys(true)) {
                    message.add("- " + key + ": " + fc.get(key));
                }
                
                sender.sendMessage(message.toArray(new String[0]));
                return;
            }
            
            if (args[1].equalsIgnoreCase("set")) {
                if (args.length != 4) {
                    sender.sendMessage(ChatColor.RED + "Config set needs two args: the path to modify and the value to set");
                    return;
                }
                
                FileConfiguration fc = Config.from(a).get();
                if (!fc.contains(args[2])) {
                    sender.sendMessage(ChatColor.RED + "Could not find config path for " + args[2] + ", remember paths are case-sensitive.");
                    return;
                }
                
                Object value = parse(a, args[2], args[3]);
                if (value == null) {
                    sender.sendMessage(ChatColor.RED + "Unable to parse value " + args[3]);
                    return;
                }
                
                fc.set(args[2], value);
                Config.from(a).save();
                sender.sendMessage(ChatColor.GREEN + "Successfully set the value. The value will not update in-game until reloaded.");
            } else if (args[1].equalsIgnoreCase("reload")) {
                Config.process(a);
                Users.manager().registered().forEach(Abilities.manager()::refreshPassives);
                
                sender.sendMessage(ChatColor.GREEN + "Configurable values for " + a.getDisplay() + ChatColor.GREEN + " have been reloaded.");
            }
        }, () -> {
            sender.sendMessage(ChatColor.RED + "invalid ability");
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Abilities.manager().registered().stream().map(a -> a.getName()).collect(Collectors.toList());
        }
        
        List<String> options = new ArrayList<>();
        Abilities.manager().getInfo(args[0]).ifPresentOrElse(a -> {
            if (args.length == 2) {
                options.add("set");
                options.add("reload");
                return;
            }
            
            FileConfiguration fc = Config.from(a).get();
            
            if (args.length == 3) {
                for (String key : fc.getKeys(true)) {
                    if (fc.get(key) instanceof MemorySection) {
                        continue;
                    } else if (Config.getConfigurableField(a, key).isEmpty()) {
                        continue;
                    }
                    
                    options.add(key);
                }
                
                //no fields with Configure annotation found, but keys exist in config, just show them all
                if (options.isEmpty() && !fc.getKeys(true).isEmpty()) {
                    for (String key : fc.getKeys(true)) {
                        if (fc.get(key) instanceof MemorySection) {
                            continue;
                        }
                        
                        options.add(key);
                    }
                }
            } else if (args.length == 4) {
                Object value = fc.get(args[2]);
                
                Config.getConfigurableField(a, args[2]).ifPresent(f -> {
                    String type = "type: ";
                    
                    if (f.getType() == Integer.TYPE || f.getType() == Long.TYPE) {
                        type += "integer #";
                    } else if (f.getType() == Float.TYPE || f.getType() == Double.TYPE) {
                        type += "decimal #";
                    } else if (f.getType() == Boolean.TYPE) {
                        type += "true | false";
                    } else if (f.getType() == String.class) {
                        type += "text";
                    }
                    
                    options.add(type); 
                });
                
                if (value != null) {
                    options.add("current: " + value);
                    options.add(args[3]);
                } else {
                    options.add("path not found");
                }
            }
        }, () -> {
            options.add("ability not found");
        });
        
        if (options.isEmpty()) {
            return null;
        }
        
        return options;
    }

    private Object parse(Configurable obj, String path, String value) {
        Optional<Field> field = Config.getConfigurableField(obj, path);
        
        if (field.isEmpty()) {
            return null;
        }
        
        Class<?> clazz = field.get().getType();
        
        try {
            if (clazz == Integer.TYPE) {
                return Integer.parseInt(value);
            } else if (clazz == Long.TYPE) {
                return Long.parseLong(value);
            } else if (clazz == Float.TYPE) {
                return Float.parseFloat(value);
            } else if (clazz == Double.TYPE) {
                return Double.parseDouble(value);
            } else if (clazz == Boolean.TYPE) {
                return Boolean.parseBoolean(value);
            } else if (clazz == String.class) {
                return value;
            }
        } catch (Exception e) {}
        
        return null;
    }
}
