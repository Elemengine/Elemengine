package com.elemengine.elemengine.command.type;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;

import com.elemengine.elemengine.Addon;
import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.ability.Abilities;
import com.elemengine.elemengine.ability.AbilityInfo;
import com.elemengine.elemengine.ability.AbilityUser;
import com.elemengine.elemengine.command.Commands;
import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.command.TabComplete;
import com.elemengine.elemengine.command.TabCompleteList;
import com.elemengine.elemengine.element.Element;
import com.elemengine.elemengine.storage.configuration.Config;
import com.elemengine.elemengine.storage.configuration.Configurable;
import com.elemengine.elemengine.storage.configuration.Configure;
import com.elemengine.elemengine.user.Users;
import com.elemengine.elemengine.util.spigot.Chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.md_5.bungee.api.ChatColor;

public class ConfigureCommand extends SubCommand {
    
    public static final int TYPE_ARG = 0;
    public static final int OBJ_ARG = TYPE_ARG + 1;
    public static final int FIELD_ARG = OBJ_ARG + 1;
    public static final int VALUE_ARG = FIELD_ARG + 1;
    public static final int RELOAD_ARG = VALUE_ARG + 1;
    
    @Configure String usageErr = "Not enough arguments given, try: {usage}";
    @Configure String noType = "No type of configurable object found from '{input}'";
    @Configure String success = "Successfully set the value and reloaded the {type}.";
    @Configure String successNoReload = "Successfully set the value. The {type} will not update in-game until reloaded.";
    @Configure String notFound = "Could not find that {type}.";
    
    private Map<String, ConfigureOption> types = new HashMap<>();
    
    public ConfigureCommand() {
        super(
            "configure", 
            "Sets the configurable field of the given ability and reloads the ability (unless given false), which will stop all active abilities and restart any passives.", 
            "/elemengine configure <type> <object> [<path> <value> [reload]]" , 
            Arrays.asList("config", "cfg", "c")
        );
        this.register(new ConfigureAbility());
        this.register(new ConfigureAddon());
        this.register(new ConfigureCmd());
        this.register(new ConfigureElement());
    }

    @Override
    public void postProcessed(Config config) {}

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= OBJ_ARG) {
            sender.sendMessage(ChatColor.RED + usageErr.replace("{usage}", getUsage()));
            return;
        }
        
        String type = args[TYPE_ARG];
        ConfigureOption found = types.get(type);
        if (found == null) {
            sender.sendMessage(ChatColor.RED + noType.replace("{input}", type));
            return;
        }
        
        Map<String, Component> tag = Map.of("{type}", Component.text(found.name()));    
        
        found.get(args[OBJ_ARG]).ifPresentOrElse(obj -> {
            if (modifyField(sender, args, obj)) {
                found.reload(obj);
                sender.sendMessage(Chat.format(success, tag).colorIfAbsent(NamedTextColor.GREEN));
            } else {
                sender.sendMessage(Chat.format(successNoReload, tag).colorIfAbsent(NamedTextColor.GREEN));
            }
        }, () -> {
            sender.sendMessage(Chat.format(notFound, tag).colorIfAbsent(NamedTextColor.GREEN));
        });
    }

    @Override
    public TabComplete tabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return new TabComplete(new ArrayList<>(types.keySet()));
        }
        
        TabComplete options = new TabComplete();
        String type = args[TYPE_ARG];
        ConfigureOption option = types.get(type);
        if (option == null) {
            return null;
        } else if (args.length == 2) {
            return new TabComplete(option.possibilities());
        } else {
            
            option.get(args[OBJ_ARG]).ifPresentOrElse(
                a -> listConfigurableFields(args, options, a), 
                () -> {
                    options.add("not found");
                    options.shouldFilter(false);
                }
            );
        }
        
        return options;
    }
    
    public void register(ConfigureOption type) {
        if (types.containsKey(type.name().toLowerCase())) {
            return;
        }
        
        this.types.put(type.name().toLowerCase(), type);
    }
    
    public interface ConfigureOption {
        String name();
        Optional<? extends Configurable> get(String str);
        void reload(Configurable obj);
        List<String> possibilities();
    }
    
    private class ConfigureAbility implements ConfigureOption {

        @Override
        public String name() {
            return "ability";
        }

        @Override
        public Optional<AbilityInfo> get(String str) {
            return Abilities.manager().getInfo(str);
        }

        @Override
        public void reload(Configurable obj) {
            Config.process(obj);
            Users.manager().registered().forEach(AbilityUser::refresh);
        }

        @Override
        public List<String> possibilities() {
            return TabCompleteList.abilities();
        }
        
    }
    
    private class ConfigureAddon implements ConfigureOption {

        @Override
        public String name() {
            return "addon";
        }

        @Override
        public Optional<Addon> get(String str) {
            return Elemengine.getAddon(str);
        }

        @Override
        public void reload(Configurable obj) {
            if (obj instanceof Addon addon) {
                Elemengine.reload(addon);
            }
        }

        @Override
        public List<String> possibilities() {
            return Elemengine.listAddons().stream().map(a -> a.getInternalName()).collect(Collectors.toList());
        }
        
    }
    
    private class ConfigureCmd implements ConfigureOption {

        @Override
        public String name() {
            return "command";
        }

        @Override
        public Optional<? extends Configurable> get(String str) {
            return Commands.manager().get(str);
        }

        @Override
        public void reload(Configurable obj) {
            Config.process(obj);
        }

        @Override
        public List<String> possibilities() {
            return new ArrayList<>(Commands.manager().registered());
        }
        
    }
    
    private class ConfigureElement implements ConfigureOption {

        @Override
        public String name() {
            return "element";
        }

        @Override
        public Optional<? extends Configurable> get(String str) {
            return Optional.ofNullable(Element.from(str));
        }

        @Override
        public void reload(Configurable obj) {
            Config.process(obj);
        }

        @Override
        public List<String> possibilities() {
            return TabCompleteList.elements(false);
        }
        
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
    
    private void listConfigurableFields(String[] args, TabComplete list, Configurable c) {
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
                list.shouldFilter(false);
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
