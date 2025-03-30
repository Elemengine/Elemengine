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

import com.elemengine.elemengine.Elemengine;
import com.google.common.base.Preconditions;

public class Dynamics {
    
    private Dynamics() {}

    public static <T> T construct(Class<T> type) {
        T inst = null;
        try {
            inst = type.getDeclaredConstructor().newInstance();
        } catch (Exception e) {}

        return inst;
    }

    public static <T extends Enum<T>> T enumValue(Class<T> type, String value) {
        T inst = null;
        try {
            inst = Enum.valueOf(type, value.toUpperCase());
        } catch (Exception e) {}

        return inst;
    }

    public static <T> void load(String path, Class<T> parentClass, Consumer<T> consumer) {
        Preconditions.checkArgument(path != null, "Attempted to dynamically load null path.");
        ClassLoader loader = parentClass.getClassLoader();

        try {
            Enumeration<URL> resources = loader.getResources(path.replace('.', '/'));
            if (!resources.hasMoreElements()) {
                Elemengine.logger().info("Could not load resources from path '" + path + "'");
                return;
            }

            String jarLoc = resources.nextElement().getPath();
            try (JarFile jar = new JarFile(new File(URLDecoder.decode(jarLoc.substring(5, jarLoc.length() - path.length() - 2), "UTF-8")))) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);

                    if (!className.startsWith(path)) {
                        continue;
                    }

                    Class<?> clazz = Class.forName(className, true, loader);
                    if (entry.getName().contains("$") || clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                        continue;
                    }

                    if (parentClass.isAssignableFrom(clazz)) {
                        consumer.accept(parentClass.cast(clazz.getDeclaredConstructor().newInstance()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static <T> void load(String path, ClassLoader loader, Class<T> parentClass, Consumer<T> consumer) {
        Preconditions.checkArgument(path != null, "Attempted to dynamically load null path.");

        try {
            Enumeration<URL> resources = loader.getResources(path.replace('.', '/'));
            if (!resources.hasMoreElements()) {
                Elemengine.logger().info("Could not load resources from path '" + path + "'");
                return;
            }

            String jarLoc = resources.nextElement().getPath();
            try (JarFile jar = new JarFile(new File(URLDecoder.decode(jarLoc.substring(5, jarLoc.length() - path.length() - 2), "UTF-8")))) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);

                    if (!className.startsWith(path)) {
                        continue;
                    }

                    Class<?> clazz = Class.forName(className, true, loader);
                    if (entry.getName().contains("$") || clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                        continue;
                    }

                    if (parentClass.isAssignableFrom(clazz)) {
                        consumer.accept(parentClass.cast(clazz.getDeclaredConstructor().newInstance()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void load(String path, ClassLoader loader, Predicate<Class<?>> filter, Consumer<Class<?>> consumer) {
        Preconditions.checkArgument(path != null, "Attempted to dynamically load null path");

        try {
            Enumeration<URL> resources = loader.getResources(path.replace('.', '/'));
            if (!resources.hasMoreElements()) {
                Elemengine.logger().info("Could not load resources from path '" + path + "'");
                return;
            }

            String jarLoc = resources.nextElement().getPath();
            try (JarFile jar = new JarFile(new File(URLDecoder.decode(jarLoc.substring(5, jarLoc.length() - path.length() - 2), "UTF-8")))) {
                Enumeration<JarEntry> entries = jar.entries();

                while (entries.hasMoreElements()) {
                    JarEntry entry = entries.nextElement();
                    if (!entry.getName().endsWith(".class")) {
                        continue;
                    }

                    String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);

                    if (!className.startsWith(path)) {
                        continue;
                    }

                    Class<?> clazz = Class.forName(className, true, loader);
                    if (entry.getName().contains("$") || clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                        continue;
                    }

                    if (filter.test(clazz)) {
                        consumer.accept(clazz);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadDir(File dir, ClassLoader parent, boolean deep, Predicate<Class<?>> filter, Consumer<Class<?>> consumer) {
        Preconditions.checkArgument(dir != null, "Attempted to dynamically load null directory");

        Set<File> files = new HashSet<>();
        List<URL> urls = new ArrayList<>();

        Dynamics.searchDir(dir, ".jar", deep, found -> {
            if (files.add(found)) {
                try {
                    urls.add(found.toURI().toURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try (URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[0]), parent)) {
            for (File file : files) {
                try (JarFile jar = new JarFile(file)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();

                        if (!entry.getName().endsWith(".class")) {
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

    public static <T> void loadDir(File dir, boolean deep, Class<T> type, Consumer<T> consumer) {
        Preconditions.checkArgument(dir != null, "Attempted to dynamically load null directory");

        Set<File> files = new HashSet<>();
        List<URL> urls = new ArrayList<>();

        Dynamics.searchDir(dir, ".jar", deep, found -> {
            if (files.add(found)) {
                try {
                    urls.add(found.toURI().toURL());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try (URLClassLoader loader = URLClassLoader.newInstance(urls.toArray(new URL[0]), type.getClassLoader())) {
            for (File file : files) {
                try (JarFile jar = new JarFile(file)) {
                    Enumeration<JarEntry> entries = jar.entries();
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();

                        if (!entry.getName().endsWith(".class")) {
                            continue;
                        }

                        String className = entry.getName().replace('/', '.').substring(0, entry.getName().length() - 6);
                        Class<?> clazz = Class.forName(className, true, loader);

                        if (entry.getName().contains("$") || clazz.isInterface() || java.lang.reflect.Modifier.isAbstract(clazz.getModifiers())) {
                            continue;
                        }

                        if (type.isAssignableFrom(clazz)) {
                            consumer.accept(type.cast(clazz.getDeclaredConstructor().newInstance()));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void searchDir(File dir, String extension, boolean deep, Consumer<File> found) {
        for (File file : dir.listFiles(file -> (deep && file.isDirectory() && !file.equals(dir)) || file.getName().endsWith(extension))) {
            if (file.isDirectory()) {
                searchDir(file, extension, deep, found);
            } else {
                found.accept(file);
            }
        }
    }
}
