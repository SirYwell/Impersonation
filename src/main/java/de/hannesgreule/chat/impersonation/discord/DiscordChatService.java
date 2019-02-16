package de.hannesgreule.chat.impersonation.discord;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.gson.JsonObject;
import de.hannesgreule.chat.impersonation.ChatService;
import de.hannesgreule.chat.impersonation.ChatServiceContext;
import de.hannesgreule.chat.impersonation.DialogflowRequester;
import de.hannesgreule.chat.impersonation.exception.ChatServiceCreationException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

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

    @SuppressWarnings("unused")
    public static ChatService fromJsonObject(JsonObject object) throws ChatServiceCreationException {
        var token = object.get("discord_token").getAsString();
        var project = object.get("dialogflow_project").getAsString();
        if (!object.has("google_credentials")) {
            throw new ChatServiceCreationException("Missing google_credentials.");
        }
        var credentials = object.get("google_credentials");
        var factory = JacksonFactory.getDefaultInstance();
        CredentialsProvider credentialsProvider;
        if (object.get("google_credentials").isJsonPrimitive()) {
            var filePath = object.get("google_credentials").getAsString();
            try {
                LOGGER.info("Trying to load file " + filePath);
                var file = new File(filePath);
                if (!file.exists()) {
                    throw new ChatServiceCreationException("Credentials file not found in path " + file.getAbsolutePath());
                }
                var stream = new FileInputStream(file);
                credentialsProvider = setupCredentials(stream);
                LOGGER.info("Credentials found. Using now.");
            } catch (IOException e) {
                throw new ChatServiceCreationException("Credential problem", e);
            }
        } else {
            throw new UnsupportedOperationException("Must be file path");
        }
        try {
            return new DiscordChatService(token, project,
                    SessionsSettings.newBuilder().setCredentialsProvider(credentialsProvider).build());
        } catch (IOException e) {
            throw new ChatServiceCreationException("Failed to create " + DiscordChatService.class.getName());
        }
    }

    private static CredentialsProvider setupCredentials(InputStream stream) throws IOException {
        var credentials = GoogleCredentials.fromStream(stream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        return FixedCredentialsProvider.create(credentials);
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
