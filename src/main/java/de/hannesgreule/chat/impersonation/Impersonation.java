package de.hannesgreule.chat.impersonation;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

public class Impersonation {
    private final File configFile;

    public Impersonation(String configFile) {
        String complete = configFile.endsWith(".json") ? configFile : configFile + ".json";
        this.configFile = new File(complete);
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("No config file given!");
        }
        var impersonation = new Impersonation(args[0]);
        try {
            impersonation.start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void start() throws FileNotFoundException {
        loadChatServices();
    }

    private void loadChatServices() throws FileNotFoundException {
        var registry = ChatServiceRegistry.getInstance();
        var stream = new FileInputStream(configFile);
        var object = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        var bots = object.get("bots").getAsJsonArray();
        for (JsonElement element : bots) {
            registry.register(ChatService.fromJsonObject(element.getAsJsonObject()));
        }
    }
}
