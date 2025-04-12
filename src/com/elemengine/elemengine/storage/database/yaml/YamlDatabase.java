package com.elemengine.elemengine.storage.database.yaml;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Optional;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.elemengine.elemengine.Elemengine;
import com.elemengine.elemengine.storage.database.DataSchema;
import com.elemengine.elemengine.storage.database.Database;

public class YamlDatabase extends Database {
    
    private final File file;
    private FileConfiguration config;
    
    public YamlDatabase() {
        this.file = new File(Elemengine.getFolder(), "storage.yml");
    }

    @Override
    public void setup() {
        if (!this.file.getParentFile().exists()) {
            this.file.mkdirs();
        }

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public boolean create(Class<? extends DataSchema> type) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean set(DataSchema schema) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <T extends DataSchema> Optional<T> get(T schema) {
        // TODO Auto-generated method stub
        return Optional.empty();
    }

    @Override
    public <T extends DataSchema> Deque<T> getAll(T schema) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean delete(DataSchema schema) {
        // TODO Auto-generated method stub
        return false;
    }

}
