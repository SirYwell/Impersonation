package de.hannesgreule.chat.impersonation.telegram;

import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.gson.JsonObject;
import de.hannesgreule.chat.impersonation.ChatService;
import de.hannesgreule.chat.impersonation.ChatServiceContext;
import de.hannesgreule.chat.impersonation.DialogflowRequester;
import de.hannesgreule.chat.impersonation.GoogleUtil;
import de.hannesgreule.chat.impersonation.exception.ChatServiceCreationException;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class TelegramChatService extends TelegramLongPollingBot implements ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramChatService.class);
    private static final TelegramBotsApi BOTS_API;

    private final String token;
    private final String username;
    private DialogflowRequester requester;

    static {
        ApiContextInitializer.init();

        BOTS_API = new TelegramBotsApi();
    }

    public TelegramChatService(String token, String username, String project, SessionsSettings sessionsSettings) {
        this.token = token;
        this.username = username;
        this.requester = new DialogflowRequester(project, sessionsSettings);
    }

    public static TelegramChatService fromJsonObject(JsonObject object) throws ChatServiceCreationException {
        var token = object.get("telegram_token").getAsString();
        var username = object.get("username").getAsString();
        var project = object.get("dialogflow_project").getAsString();
        return new TelegramChatService(token, username, project, GoogleUtil.createSettings(object));

    }

    @Override
    public void onChatServiceMessage(@NotNull ChatServiceContext context, @NotNull String message) {
        if (!context.sendMessage(requester.request(message))) {
            LOGGER.debug(String.format("failed to send message '%s' to group.", message));
        }
    }

    @Override
    public void run() {
        try {
            BOTS_API.registerBot(this);
        } catch (TelegramApiException e) {
            LOGGER.warn("Failed to register bot", e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        String message;
        if (update.hasMessage() && update.getMessage().hasText()) {
            message = update.getMessage().getText();
        } else {
            return;
        }
        Consumer<Chat> typingConsumer = chat -> {
            SendChatAction action = new SendChatAction();
            action.setChatId(chat.getId());
            action.setAction(ActionType.TYPING);
            executeSafe(action);
        };
        BiConsumer<Chat, String> bi = (c, s) -> {
            SendMessage sendMessage = new SendMessage(c.getId(), s);
            executeSafe(sendMessage);
        };
        var context = new TelegramChatServiceContext(typingConsumer, bi, update.getMessage().getChat());
        onChatServiceMessage(context, message);
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    private void executeSafe(SendChatAction action) {
        try {
            execute(action);
        } catch (TelegramApiException e) {
            LOGGER.warn("Sending typing failed", e);
        }
    }

    private void executeSafe(SendMessage action) {
        try {
            execute(action);
        } catch (TelegramApiException e) {
            LOGGER.warn("Sending message failed", e);
        }
    }

    @Override
    public String toString() {
        return "TelegramChatService{"
                + "username='" + username + '\''
                + '}';
    }
}
