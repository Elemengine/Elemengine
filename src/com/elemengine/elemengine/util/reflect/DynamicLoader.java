package com.elemengine.elemengine.util.reflect;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.base.Preconditions;

public class DynamicLoader {
    
    public static <T> void load(JavaPlugin plugin, String path, Class<T> parentClass, Consumer<T> consumer) {
        Preconditions.checkArgument(plugin != null, "Cannot load dynamically from null plugin");
        Preconditions.checkArgument(path != null, plugin.getName() + " attempted to dynamically load null path");

        ClassLoader loader = plugin.getClass().getClassLoader();

        try {
            Enumeration<URL> resources = loader.getResources(path.replace('.', '/'));
            if (!resources.hasMoreElements()) {
                plugin.getLogger().info("Could not load resources from path '" + path + "' for plugin '" + plugin.getName() + "'");
                return;
            }
            
            String jarLoc = resources.nextElement().getPath();
            JarFile jar = new JarFile(new File(URLDecoder.decode(jarLoc.substring(5, jarLoc.length() - path.length() - 2), "UTF-8")));
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class") || entry.getName().contains("$")) {
                    continue;
                }

                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                if (!className.startsWith(path)) {
                    continue;
                }

                Class<?> clazz = Class.forName(className, true, loader);
                if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                if (parentClass.isAssignableFrom(clazz)) {
                    consumer.accept(parentClass.cast(clazz.getDeclaredConstructor().newInstance()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(JavaPlugin plugin, String path, Predicate<Class<?>> filter, Consumer<Class<?>> consumer) {
        Preconditions.checkArgument(plugin != null, "Cannot load dynamically from null plugin");
        Preconditions.checkArgument(path != null, plugin.getName() + " attempted to dynamically load null path");

        ClassLoader loader = plugin.getClass().getClassLoader();

        try {
            Enumeration<URL> resources = loader.getResources(path.replace('.', '/'));
            String jarLoc = resources.nextElement().getPath();
            JarFile jar = new JarFile(new File(URLDecoder.decode(jarLoc.substring(5, jarLoc.length() - path.length() - 2), "UTF-8")));
            Enumeration<JarEntry> entries = jar.entries();

            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                if (!entry.getName().endsWith(".class") || entry.getName().contains("$")) {
                    continue;
                }

                String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                if (!className.startsWith(path)) {
                    continue;
                }

                Class<?> clazz = Class.forName(className, true, loader);
                if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                    continue;
                }

                if (filter.test(clazz)) {
                    consumer.accept(clazz);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void loadDir(JavaPlugin plugin, File dir, boolean deep, Predicate<Class<?>> filter, Consumer<Class<?>> consumer) {
        Preconditions.checkArgument(plugin != null, "Cannot load dynamically from null plugin");
        Preconditions.checkArgument(dir != null, plugin.getName() + " attempted to dynamically load null directory");
        
        Set<File> files = new HashSet<>();
        List<URL> urls = new ArrayList<>();
        
        DynamicLoader.searchDir(dir, deep, found -> {
            if (files.add(found)) {
                try {
                    urls.add(found.toURI().toURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
            
        try (URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[0]), plugin.getClass().getClassLoader())) {    
            for (File file : files) {
                try (JarFile jar = new JarFile(file)) {
                    if (jar.getEntry("plugin.yml") != null) {
                        plugin.getLogger().warning("Jar file '" + file.getName() + "' loaded by '" + plugin.getName() + "' is a plugin, not an addon");
                        continue;
                    }
                    
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        if (!entry.getName().endsWith(".class") || entry.getName().contains("$")) {
                            continue;
                        }
                        
                        String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                        Class<?> clazz = Class.forName(className, true, loader);
                        if (clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                            continue;
                        }
                        
                        if (filter.test(clazz)) {
                            consumer.accept(clazz);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void searchDir(File dir, boolean deep, Consumer<File> found) {
        for (File file : dir.listFiles(file -> (deep && file.isDirectory() && !file.equals(dir)) || file.getName().endsWith(".jar"))) {
            if (file.isDirectory()) {
                searchDir(file, deep, found);
            } else {
                found.accept(file);
            }
        }
    }

    public static <T> Set<T> loadAndCollect(JavaPlugin plugin, String path, Class<T> parentClass) {
        Set<T> found = new HashSet<>();
        DynamicLoader.load(plugin, path, parentClass, found::add);
        return found;
    }
    
    public static Set<Class<?>> loadAndCollect(JavaPlugin plugin, String path, Predicate<Class<?>> filter) {
        Set<Class<?>> found = new HashSet<>();
        DynamicLoader.load(plugin, path, filter, found::add);
        return found;
    }
    
    public static Set<Class<?>> loadDirAndCollect(JavaPlugin plugin, File dir, boolean deep, Predicate<Class<?>> filter) {
        Set<Class<?>> found = new HashSet<>();
        DynamicLoader.loadDir(plugin, dir, deep, filter, found::add);
        return found;
    }
}
