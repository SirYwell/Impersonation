package de.hannesgreule.chat.impersonation.discord;

import de.hannesgreule.chat.impersonation.DelayedAnswerContext;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The context for discord channels.
 * Automatically delays typing and sending of messages.
 *
 * @author Hannes Greule
 *
 * @version 1.0.0
 */
public class DiscordChatServiceContext extends DelayedAnswerContext<MessageChannel> {
    private static final Consumer<MessageChannel> SEND_TYPING = c -> c.sendTyping().queue();
    private static final BiConsumer<MessageChannel, String> SEND_MESSAGE = (c, s) -> c.sendMessage(s).queue();

    /**
     * Create a new instance of the {@link DiscordChatServiceContext}
     * with a given channel.
     *
     * @param channel the channel to send the response to.
     */
    public DiscordChatServiceContext(MessageChannel channel) {
        super(SEND_TYPING, SEND_MESSAGE, channel);
    }

    @Override
    public boolean sendMessage(String response) {
        return sendMessageDelayed(response);
    }
}
