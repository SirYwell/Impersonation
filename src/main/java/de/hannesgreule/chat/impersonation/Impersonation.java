package de.hannesgreule.chat.impersonation;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;

public class Impersonation {

    public static void main(String[] args) {
        var impersonation = new Impersonation();
        impersonation.start();
    }

    private void start() {
        loadChatServices();
    }

    private void loadChatServices() {
        var registry = ChatServiceRegistry.getInstance();
        var stream = getClass().getResourceAsStream("/config.json");
        var object = new JsonParser().parse(new InputStreamReader(stream)).getAsJsonObject();
        var bots = object.get("bots").getAsJsonArray();
        for (JsonElement element : bots) {
            registry.register(ChatService.fromJsonObject(element.getAsJsonObject()));
        }
    }
}
