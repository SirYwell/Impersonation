package de.hannesgreule.chat.impersonation.exception;

public class ChatServiceCreationException extends Exception {

    public ChatServiceCreationException(String message) {
        super(message);
    }

    public ChatServiceCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
