package com.elemengine.elemengine.storage.configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.elemengine.elemengine.Elemengine;

public class Config {

    private static final Map<String, Config> CACHE = new HashMap<>();

    private File file;
    private FileConfiguration config;

    private Config(File file) {
        this.file = file;
    }

    public Config reload() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.config = YamlConfiguration.loadConfiguration(file);
        this.load();
        return this;
    }

    public boolean load() {
        try {
            config.load(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean save() {
        try {
            config.options().copyDefaults(true);
            config.save(file);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Config addDefault(String path, Object value) {
        config.addDefault(path, value);
        return this;
    }
    
    public boolean contains(String path) {
        return config.contains(path);
    }

    /**
     * Uses the given {@link BiFunction} to get a value from the FileConfiguration.
     * An example use of this is:
     * 
     * <pre>
     *  {@code 
     * double speed = config.getValue(FileConfiguration::getDouble, "Speed")
     * }
     * </pre>
     * 
     * @param <T>    Return type of the getter function
     * @param getter Function to get a value from a FileConfiguration
     * @param path   Path argument for a configuration path
     * @return a value from the config
     */
    public <T> T get(BiFunction<FileConfiguration, String, T> getter, String path) {
        return getter.apply(config, path);
    }

    public <T> T get(Function<FileConfiguration, T> getter) {
        return getter.apply(config);
    }

    public Object get(String path) {
        return config.get(path);
    }
    
    public Config set(String path, Object value) {
        config.set(path, value);
        return this;
    }

    public FileConfiguration into() {
        return config;
    }

    /**
     * Get the config from the configurable object
     * 
     * @param object the configurable object
     * @return config of the configurable object
     */
    public static Config from(Configurable object) {
        return from(object.getFileName(), object.getFolderName());
    }

    public static Config from(String fileName, String folderName) {
        return CACHE.computeIfAbsent(folderName + "/" + fileName, Config::create);
    }

    private static Config create(String path) {
        if (path.isEmpty() || path == null) {
            return null;
        } else if (!path.endsWith(".yml")) {
            path += ".yml";
        }

        return new Config(new File(Elemengine.getFolder(), "/configuration/" + path)).reload();
    }
    
    public static Optional<Field> getConfigurableField(Configurable object, String fieldArg) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Configure.class)) continue;
            
            String path = field.getAnnotation(Configure.class).value();
            
            if (path.isEmpty()) {
                path = field.getName();
            }
            
            if (path.equals(fieldArg)) {
                return Optional.of(field);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Take the given configurable object and modify any fields with the
     * {@link Configure} annotation to match the value in the object's config, or
     * making the value of the field the default if one doesn't already exist.
     * 
     * @param <T>    Object type that extends {@link Configurable}
     * @param object the object to be configured
     * @return the configured object
     */
    public static <T extends Configurable> T process(T object) {
        Config config = from(object);
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Configure.class)) {
                String path = field.getAnnotation(Configure.class).value();

                if (path.isEmpty()) {
                    path = field.getName();
                }

                try {
                    boolean access = field.canAccess(object);
                    field.setAccessible(true);

                    if (!config.into().contains(path)) {
                        config.addDefault(path, field.get(object));
                        config.into().setComments(path, parseComments(field.getAnnotation(Configure.class).comment()));
                    } else {
                        field.set(object, config.get(path));
                    }

                    field.setAccessible(access);
                } catch (Exception e) {
                    e.printStackTrace();
                    Elemengine.plugin().getLogger().warning("Unable to set config value of " + field.getName() + " for " + object.getFileName());
                }
            }
        }

        config.save();
        object.postProcessed(config);
        return object;
    }

    /**
     * Parses the given string and returns a list of comments. Comment lines are
     * split using a semicolon
     * 
     * @param comments Lines of comments, separated by semicolons in one string
     * @return list of comment lines
     */
    public static List<String> parseComments(String comments) {
        List<String> list = null;

        if (comments != null && !comments.isEmpty()) {
            String[] split = comments.split(";");
            for (int i = 0; i < split.length; ++i) {
                split[i] = split[i].trim();
            }

            list = Arrays.asList(split);
        }

        return list;
    }
}
