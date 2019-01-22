package de.hannesgreule.chat.impersonation;

/**
 * A context in which messages are sent.
 *
 * @author Hannes Greule
 *
 * @version 1.0.0
 */
public interface ChatServiceContext {

    /**
     * Sends a message in the context.
     *
     * @param response the message to send.
     * @return {@link true} if message could be sent successfully, {@link false} otherwise.
     */
    boolean sendMessage(String response);
}
