package com.elemengine.elemengine.command;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.storage.Config;
import com.elemengine.elemengine.storage.Configurable;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class ConfigureCommand extends SubCommand {

    public ConfigureCommand() {
        super(
            "configure", 
            "Sets the configurable field of the given ability and reloads the ability (unless given false), which will stop all active abilities and restart any passives.", 
            "/elemengine configure <ability> [<path> <value> [reload]]" , 
            Arrays.asList("config", "cfg", "c")
        );
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
            Config config = Config.from(a);
            
            if (args.length == 1) {
                List<String> message = new ArrayList<>();
                
                for (String key : config.get(fc -> fc.getKeys(true))) {
                    message.add("- " + key + ": " + config.get(key));
                }
                
                sender.sendMessage(message.toArray(new String[0]));
                return;
            }
            
            if (args.length < 3 || args.length > 4) {
                sender.sendMessage(ChatColor.RED + "Configure needs two args: the path to modify and the new value");
                return;
            }
            
            if (!config.contains(args[1])) {
                sender.sendMessage(ChatColor.RED + "Could not find config path for " + args[1] + ", remember paths are case-sensitive.");
                return;
            }
            
            Object value = parse(a, args[1], args[2]);
            if (value == null) {
                sender.sendMessage(ChatColor.RED + "Unable to parse value " + args[2]);
                return;
            }
            
            config.set(args[1], value).save();
            
            boolean reload = true;
            if (args.length == 4) {
                try {
                    reload = Boolean.parseBoolean(args[3].toLowerCase());
                } catch (Exception e) {}
            }
            
            if (reload) {
                Config.process(a);
                Users.manager().registered().forEach(AbilityUser::refresh);
                sender.sendMessage(ChatColor.GREEN + "Successfully set the value and reloaded the ability.");
            } else {
                sender.sendMessage(ChatColor.GREEN + "Successfully set the value. The value will not update in-game until reloaded.");
            }
        }, () -> {
            sender.sendMessage(ChatColor.RED + "Invalid ability");
        });
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return Abilities.manager().registered().stream().map(a -> a.getName()).collect(Collectors.toList());
        }
        
        List<String> options = new ArrayList<>();
        Abilities.manager().getInfo(args[0]).ifPresentOrElse(a -> {
            FileConfiguration fc = Config.from(a).into();
            
            if (args.length == 2) {
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
            } else if (args.length == 3) {
                Object value = fc.get(args[1]);
                
                Config.getConfigurableField(a, args[1]).ifPresent(f -> {
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
                    options.add(args[2]);
                } else {
                    options.add("path not found");
                }
            } else if (args.length == 4) {
                options.add("true");
                options.add("false");
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
