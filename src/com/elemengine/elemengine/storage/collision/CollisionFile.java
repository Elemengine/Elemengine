package com.elemengine.elemengine.storage.collision;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;

import com.elemengine.elemengine.storage.collision.CollisionData.CollisionParseException;
import com.google.common.base.Preconditions;

public class CollisionFile {

    private File file;

    private CollisionFile(File file) {
        this.file = file;
        this.init();
    }

    private void init() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads through the file for any valid collisions
     * 
     * @return Set of the valid CollisionData parsed
     */
    public Set<CollisionData> read() {
        Set<CollisionData> datas = new HashSet<>();

        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                try {
                    datas.add(CollisionData.parse(reader.nextLine()));
                } catch (CollisionParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return datas;
    }

    /**
     * Reads through the file for any valid collisions and passes them to the given
     * Consumer
     * 
     * @param consumer what to do with the valid parsed CollisionData
     */
    public void readAnd(Consumer<CollisionData> consumer) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    consumer.accept(CollisionData.parse(line));
                } catch (CollisionParseException e) {
                    e.printStackTrace();
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes information about a collision to the file
     * 
     * @param data collision to write
     * @return false on IO exception, true otherwise
     */
    public boolean write(CollisionData data) {
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.write(data.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static CollisionFile open(File file) {
        Preconditions.checkArgument(file != null, "Cannot open a null file");
        Preconditions.checkArgument(file.getName().endsWith(".txt"), "A collision file must have a txt extension!");

        return new CollisionFile(file);
    }
}