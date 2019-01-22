package de.hannesgreule.chat.impersonation;

import java.util.ArrayList;
import java.util.List;

public class ChatServiceRegistry {
    private static final ChatServiceRegistry INSTANCE = new ChatServiceRegistry();
    private List<ChatService> services = new ArrayList<>();

    private ChatServiceRegistry() { }

    public static ChatServiceRegistry getInstance() {
        return INSTANCE;
    }

    public void register(ChatService chatService) {
        services.add(chatService);
        new Thread(chatService, chatService.toString()).start();
    }
}
