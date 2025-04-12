package com.elemengine.elemengine.storage.database;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class Database {
    
    public abstract void setup();
    
    /**
     * Creates the structure of the given DataSchema within the database. This operation
     * may not necessarily be actually implemented, depending on the backing database, in
     * which case this method will just return true.
     * @param type The type of DataSchema to create definition of within the database
     * @return true if the definition was able to be created
     */
    public abstract boolean create(Class<? extends DataSchema> type);
    
    /**
     * Inserts or updates the DataSchema object within the database. 
     * @param schema The DataSchema object to store.
     * @return true if the object is able to be inserted/updated
     */
    public abstract boolean set(DataSchema schema);
    
    /**
     * Attempts to retrieve the given DataSchema type from the database with values matching
     * the passed through schema. Null values within the input schema will be ignored.
     * @param <T> Extension type of DataSchema
     * @param schema 
     * @return 
     */
    public abstract <T extends DataSchema> Optional<T> get(T schema);
    
    public abstract <T extends DataSchema> Deque<T> getAll(T schema);
    
    /**
     * Attempts to delete any data that matches the given DataSchema
     * @param type
     * @param keys
     * @return
     */
    public abstract boolean delete(DataSchema schema);
    
    public final <T extends DataSchema> Optional<T> get(Class<T> type, Consumer<T> setter) {
        try {
            T filter = type.getConstructor().newInstance();
            
            setter.accept(filter);
            
            return this.get(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return Optional.empty();
    }
    
    public final <T extends DataSchema> Deque<T> getAll(Class<T> type, Consumer<T> setter) {
        try {
            T filter = type.getConstructor().newInstance();
            
            setter.accept(filter);
            
            return this.getAll(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return new ArrayDeque<>();
    }
    
    public final <T extends DataSchema> boolean set(Class<T> type, Consumer<T> setter) {
        try {
            T filter = type.getConstructor().newInstance();
            
            setter.accept(filter);
            
            return this.set(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public final <T extends DataSchema> boolean delete(Class<T> type, Consumer<T> setter) {
        try {
            T filter = type.getConstructor().newInstance();
            
            setter.accept(filter);
            
            return this.delete(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return false;
    }
}
