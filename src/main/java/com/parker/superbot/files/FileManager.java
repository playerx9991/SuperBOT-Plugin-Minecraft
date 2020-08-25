package com.parker.superbot.files;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FileManager {
    public void checkFiles() throws IOException {
        File file = new File("plugins/SuperBotPlugin/config/conf.json");
        file.getParentFile().mkdirs();
        if (file.createNewFile()) {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("CommandsChannelID", "879526448174620723");
                map.put("ChatChannelID", "879528154115477524");

                Writer writer = new FileWriter(file);

                Gson gson = new Gson();

                gson.toJson(map, writer);

                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getChatChannelID() {
        try {
            File file = new File("plugins/SuperBotPlugin/config/conf.json");
            if (file.exists()) {
                System.out.println("Found File");
                // create Gson instance
                Gson gson = new Gson();

                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("plugins/SuperBotPlugin/config/conf.json"));

                // convert JSON file to map
                JsonObject object = gson.fromJson(reader, JsonObject.class);

                String chatChannelID = object.get("ChatChannelID").getAsString();

                System.out.println(chatChannelID);

                // close reader
                reader.close();

                return chatChannelID;
            } else {
                return null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    public String getCommandsChannelID() {
        try {
            File file = new File("plugins/SuperBotPlugin/config/conf.json");
            if (file.exists()) {
                System.out.println("Found File");
                // create Gson instance
                Gson gson = new Gson();

                // create a reader
                Reader reader = Files.newBufferedReader(Paths.get("plugins/SuperBotPlugin/config/conf.json"));

                // convert JSON file to map
                JsonObject object = gson.fromJson(reader, JsonObject.class);

                String commandsChannelID = object.get("CommandsChannelID").getAsString();

                System.out.println(commandsChannelID);

                // close reader
                reader.close();

                return commandsChannelID;
            } else {
                return null;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
