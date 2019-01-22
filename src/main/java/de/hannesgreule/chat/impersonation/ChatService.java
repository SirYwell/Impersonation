package de.hannesgreule.chat.impersonation;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;

/**
 * Any chat service providing basic functions like sending and receiving messages.
 *
 * @author Hannes Greule
 *
 * @version 1.0.0
 */
public interface ChatService extends Runnable {

    /**
     * Gets called whenever a message in the given context was received.
     *
     * @param context the context of the received message.
     * @param message the received message.
     */
    void onChatServiceMessage(@NotNull ChatServiceContext context, @NotNull String message);

    /**
     * Parses a JSON object to an implementation of a ChatService.
     *
     * @param object the {@link JsonObject} holding the data for the ChatService.
     * @return the created ChatService.
     */
    @SuppressWarnings("unchecked")
    static ChatService fromJsonObject(JsonObject object) {
        var logger = LoggerFactory.getLogger(ChatService.class);
        ChatService chatService;
        try {
            var chatServiceClass = (Class<? extends ChatService>) Class.forName(object.get("chat_service").getAsString());
            logger = LoggerFactory.getLogger(chatServiceClass);
            var method = chatServiceClass.getMethod("fromJsonObject", JsonObject.class);
            chatService = (ChatService) method.invoke(null, object);
        } catch (IllegalAccessException | ClassNotFoundException e) {
            logger.error("Failed creating ChatService: " + e.getMessage());
            return null;
        } catch (InvocationTargetException e) {
            logger.error("Failed to call method: " + e.getMessage());
            return null;
        } catch (NoSuchMethodException e) {
            logger.error("Your ChatService class must implement static method 'fromJsonObject': " + e.getMessage());
            return null;
        } catch (ClassCastException e) {
            logger.error("This class needs to implement ChatService: " + e.getMessage());
            return null;
        }
        logger.info(String.format("Loaded ChatService '%s' successfully.", chatService));
        return chatService;
    }

}
