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
import com.elemengine.elemengine.addon.Addons;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.user.Users;

import net.md_5.bungee.api.ChatColor;

public class ConfigureCommand extends SubCommand {
    
    public static final int TYPE_ARG = 0;
    public static final int OBJ_ARG = TYPE_ARG + 1;
    public static final int FIELD_ARG = OBJ_ARG + 1;
    public static final int VALUE_ARG = FIELD_ARG + 1;
    public static final int RELOAD_ARG = VALUE_ARG + 1;

    public ConfigureCommand() {
        super(
            "configure", 
            "Sets the configurable field of the given ability and reloads the ability (unless given false), which will stop all active abilities and restart any passives.", 
            "/elemengine configure <type> <object> [<path> <value> [reload]]" , 
            Arrays.asList("config", "cfg", "c")
        );
    }

    @Override
    public void postProcessed(Config config) {}

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= OBJ_ARG) {
            sender.sendMessage(ChatColor.RED + "Expected: " + this.getUsage());
            return;
        }
        
        String type = args[TYPE_ARG];
        
        if (type.equalsIgnoreCase("ability")) {
            Abilities.manager().getInfo(args[OBJ_ARG]).ifPresentOrElse(a -> {
                if (modifyField(sender, args, a)) {
                    Config.process(a);
                    Users.manager().registered().forEach(AbilityUser::refresh);
                    sender.sendMessage(ChatColor.GREEN + "Successfully set the value and reloaded the ability.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Successfully set the value. The value will not update in-game until reloaded.");
                }
            }, () -> {
                sender.sendMessage(ChatColor.RED + "Unknown ability");
            });
        } else if (type.equalsIgnoreCase("addon")) {
            Addons.manager().tryFrom(args[OBJ_ARG]).ifPresentOrElse(a -> {
                if (modifyField(sender, args, a)) {
                    Addons.manager().reload(a);
                    sender.sendMessage(ChatColor.GREEN + "Successfully set the value and reloaded the addon.");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Successfully set the value. The value will not update in-game until reloaded.");
                }
            }, () -> {
                sender.sendMessage(ChatColor.RED + "Unknown addon");
            });
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            List<String> types = new ArrayList<>();
            types.add("ability");
            types.add("addon");
            return types;
        }
        
        List<String> options = new ArrayList<>();
        String type = args[TYPE_ARG];
        
        if (args.length == 2) {
            if (type.equalsIgnoreCase("ability")) {
                return Abilities.manager().registered().stream().map(a -> a.getName()).collect(Collectors.toList());
            } else if (type.equalsIgnoreCase(type)) {
                return Addons.manager().list().stream().map(a -> a.getInternalName()).collect(Collectors.toList());
            }
        }
        
        if (type.equalsIgnoreCase("ability")) {
            Abilities.manager().getInfo(args[OBJ_ARG]).ifPresentOrElse(
                a -> listConfigurableFields(args, options, a), 
                () -> options.add("ability not found")
            );
        } else if (type.equalsIgnoreCase("addon")) {
            Addons.manager().tryFrom(args[OBJ_ARG]).ifPresentOrElse(
                a -> listConfigurableFields(args, options, a),
                () -> options.add("addon not found")
            );
        }
        
        if (options.isEmpty()) {
            return null;
        }
        
        return options;
    }
    
    private boolean modifyField(CommandSender sender, String[] args, Configurable c) {
        Config config = Config.from(c);
        
        if (args.length == 2) {
            List<String> message = new ArrayList<>();
            
            for (String key : config.get(fc -> fc.getKeys(true))) {
                message.add("- " + key + ": " + config.get(key));
            }
            
            sender.sendMessage(message.toArray(new String[0]));
            return false;
        }
        
        if (args.length < 4 || args.length > 5) {
            sender.sendMessage(ChatColor.RED + "Configure needs two args: the path to modify and the new value");
            return false;
        }
        
        if (!config.contains(args[FIELD_ARG])) {
            sender.sendMessage(ChatColor.RED + "Could not find config path for " + args[FIELD_ARG] + ", remember paths are case-sensitive.");
            return false;
        }
        
        Object value = parse(c, args[FIELD_ARG], args[VALUE_ARG]);
        if (value == null) {
            sender.sendMessage(ChatColor.RED + "Unable to parse value " + args[VALUE_ARG]);
            return false;
        }
        
        config.set(args[FIELD_ARG], value).save();
        
        if (args.length == RELOAD_ARG + 1) {
            try {
                return Boolean.parseBoolean(args[RELOAD_ARG].toLowerCase());
            } catch (Exception e) {}
        }
        
        return true;
    }
    
    private void listConfigurableFields(String[] args, List<String> list, Configurable c) {
        FileConfiguration fc = Config.from(c).into();
        
        if (args.length == FIELD_ARG + 1) {
            for (String key : fc.getKeys(true)) {
                if (fc.get(key) instanceof MemorySection) {
                    continue;
                } else if (Config.getConfigurableField(c, key).isEmpty()) {
                    continue;
                }
                
                list.add(key);
            }
            
            //no fields with Configure annotation found, but keys exist in config, just show them all
            if (list.isEmpty() && !fc.getKeys(true).isEmpty()) {
                for (String key : fc.getKeys(true)) {
                    if (fc.get(key) instanceof MemorySection) {
                        continue;
                    }
                    
                    list.add(key);
                }
            }
        } else if (args.length == VALUE_ARG + 1) {
            Object value = fc.get(args[FIELD_ARG]);
            
            Config.getConfigurableField(c, args[FIELD_ARG]).ifPresent(f -> {
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
                
                list.add(type); 
            });
            
            if (value != null) {
                list.add("current: " + value);
                list.add(args[VALUE_ARG]);
            } else {
                list.add("path not found");
            }
        } else if (args.length == RELOAD_ARG + 1) {
            list.add("true");
            list.add("false");
        }
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
