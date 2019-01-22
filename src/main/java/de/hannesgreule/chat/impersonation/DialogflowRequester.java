package de.hannesgreule.chat.impersonation;


import com.google.cloud.dialogflow.v2.QueryInput;
import com.google.cloud.dialogflow.v2.SessionName;
import com.google.cloud.dialogflow.v2.SessionsClient;
import com.google.cloud.dialogflow.v2.TextInput;

import java.io.IOException;
import java.util.UUID;

public class DialogflowRequester {
    private String project;

    public DialogflowRequester(String project) {
        this.project = project;
    }

    public String request(String string) {
        try {
            try (var client = SessionsClient.create()) {
                var session = SessionName.of(project, UUID.randomUUID().toString());
                var textInput = TextInput.newBuilder().setText(string).setLanguageCode("de").build();
                var queryInput = QueryInput.newBuilder().setText(textInput).build();
                var response = client.detectIntent(session, queryInput);
                var result = response.getQueryResult();
                return result.getFulfillmentText();
            }
        } catch (IOException ignore) {
            return null;
        }
    }
}
