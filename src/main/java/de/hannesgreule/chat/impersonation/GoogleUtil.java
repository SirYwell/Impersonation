package de.hannesgreule.chat.impersonation;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.dialogflow.v2.SessionsSettings;
import com.google.gson.JsonObject;
import de.hannesgreule.chat.impersonation.exception.ChatServiceCreationException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

public class GoogleUtil {

    private GoogleUtil() {
        throw new UnsupportedOperationException("Util class");
    }

    public static CredentialsProvider setupCredentials(InputStream stream) throws IOException {
        var credentials = GoogleCredentials.fromStream(stream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));
        return FixedCredentialsProvider.create(credentials);
    }

    public static SessionsSettings createSettings(File file) throws ChatServiceCreationException {
        if (!file.exists()) {
            throw new ChatServiceCreationException("Credentials file not found in path " + file.getAbsolutePath());
        }
        try {
            return SessionsSettings.newBuilder()
                    .setCredentialsProvider(setupCredentials(Files.newInputStream(Paths.get(file.toURI()))))
                    .build();
        } catch (IOException e) {
            throw new ChatServiceCreationException("Failed to load credentials", e);
        }
    }

    public static SessionsSettings createSettings(JsonObject object) throws ChatServiceCreationException {
        if (!object.has("google_credentials")) {
            throw new ChatServiceCreationException("Missing google_credentials.");
        }
        if (object.get("google_credentials").isJsonPrimitive()) {
            var filePath = object.get("google_credentials").getAsString();
            var file = new File(filePath);
            if (!file.exists()) {
                throw new ChatServiceCreationException("Credentials file not found in path " + file.getAbsolutePath());
            }
            return GoogleUtil.createSettings(file);
        } else {
            throw new UnsupportedOperationException("Must be file path");
        }
    }
}
