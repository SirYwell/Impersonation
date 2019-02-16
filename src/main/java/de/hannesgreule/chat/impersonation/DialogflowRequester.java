package de.hannesgreule.chat.impersonation;


import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.cloud.dialogflow.v2.TextInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

public class DialogflowRequester {
    private static final Logger LOGGER = LoggerFactory.getLogger(DialogflowRequester.class);
    private final String project;
    private final SessionsSettings sessionsSettings;

    public DialogflowRequester(String project, SessionsSettings sessionsSettings) {
        this.project = project;
        this.sessionsSettings = sessionsSettings;
    }

    public String request(String string) {
        try {
            try (var client = SessionsClient.create(sessionsSettings)) {
                var session = SessionName.of(project, UUID.randomUUID().toString());
                var textInput = TextInput.newBuilder().setText(string).setLanguageCode("de").build();
                var queryInput = QueryInput.newBuilder().setText(textInput).build();
                var response = client.detectIntent(session, queryInput);
                var result = response.getQueryResult();
                return result.getFulfillmentText();
            }
        } catch (IOException e) {
            LOGGER.warn("Request failed", e);
            return null;
        }
    }
}
