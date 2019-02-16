package de.hannesgreule.chat.impersonation.telegram;

import de.hannesgreule.chat.impersonation.DelayedAnswerContext;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TelegramChatServiceContext extends DelayedAnswerContext<Chat> {

    public TelegramChatServiceContext(Consumer<Chat> sendTyping, BiConsumer<Chat, String> sendMessage, Chat where) {
        super(sendTyping, sendMessage, where);
    }

    @Override
    public boolean sendMessage(String response) {
        return sendMessageDelayed(response);
    }
}
