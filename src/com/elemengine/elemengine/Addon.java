package com.elemengine.elemengine;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.event.Listener;

import com.elemengine.elemengine.command.SubCommand;
import com.elemengine.elemengine.storage.configuration.Configurable;

/**
 * Process of addon loading:
 * <ol>
 * <li>{@link Addon#startup()}
 * <li>Configuration
 * <li>Listener
 * </ol>
 */
public abstract class Addon implements Listener, Configurable {

    private final String name, internal, version, author;
    final List<UUID> cmdIds = new ArrayList<>();
    
    public Addon(String name, String version, String author) {
        this.name = name;
        this.version = version;
        this.author = author;
        this.internal = name.toLowerCase().replace(" ", "_");
    }
    
    public String getInternalName() {
        return internal;
    }
    
    public String getName() {
        return name;
    }
    
    public String getVersion() {
        return version;
    }
    
    public String getAuthor() {
        return author;
    }
    
    @Override
    public final String getFileName() {
        return internal;
    }
    
    @Override
    public final String getFolderName() {
        return "addons";
    }
    
    public abstract String getManagerPath();
    public abstract String getDescription();
    protected abstract void startup();
    protected abstract void cleanup();
    protected abstract Supplier<List<SubCommand>> commandRegistrator();
}
