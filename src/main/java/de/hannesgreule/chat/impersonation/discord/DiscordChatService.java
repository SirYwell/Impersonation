package de.hannesgreule.chat.impersonation.discord;

import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.gson.JsonObject;
import de.hannesgreule.chat.impersonation.ChatService;
import de.hannesgreule.chat.impersonation.ChatServiceContext;
import de.hannesgreule.chat.impersonation.DialogflowRequester;
import de.hannesgreule.chat.impersonation.GoogleUtil;
import de.hannesgreule.chat.impersonation.exception.ChatServiceCreationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

@SuppressWarnings("unused")
public class DiscordChatService extends ListenerAdapter implements ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordChatService.class);

    private JDA jda;
    private String token;
    private DialogflowRequester requester;

    public DiscordChatService(String token, String project, SessionsSettings sessionsSettings) {
        this.token = token;
        this.requester = new DialogflowRequester(project, sessionsSettings);
    }

    public static ChatService fromJsonObject(JsonObject object) throws ChatServiceCreationException {
        var token = object.get("discord_token").getAsString();
        var project = object.get("dialogflow_project").getAsString();
        return new DiscordChatService(token, project, GoogleUtil.createSettings(object));
    }

    @Override
    public void onChatServiceMessage(@NotNull ChatServiceContext context, @NotNull String message) {
        if (!context.sendMessage(requester.request(message))) {
            LOGGER.debug(String.format("failed to send message '%s' to channel.", message));
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().equals(jda.getSelfUser())) {
            return; // ignore own messages to prevent loops
        }
        var channel = event.getChannel();
        var context = new DiscordChatServiceContext(channel);
        var content = event.getMessage().getContentStripped();
        if (content == null || content.isEmpty()) {
            return;
        }
        onChatServiceMessage(context, content);
    }

    @Override
    public void run() {
        try {
            this.jda = new JDABuilder()
                    .addEventListeners(this)
                    .setToken(token)
                    .build();
        } catch (LoginException e) {
            // TODO
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "DiscordChatService" + (jda == null ? "" : "-" + jda.getSelfUser().getName());
    }
}
