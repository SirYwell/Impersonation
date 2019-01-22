package de.hannesgreule.chat.impersonation.discord;

import de.hannesgreule.chat.impersonation.ChatServiceContext;
import de.hannesgreule.chat.impersonation.TimeUtil;
import net.dv8tion.jda.api.entities.MessageChannel;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The context for discord channels.
 * Automatically delays typing and sending of messages.
 *
 * @author Hannes Greule
 *
 * @version 1.0.0
 */
public class DiscordChatServiceContext implements ChatServiceContext {
    private MessageChannel channel;
    private long coolDown;
    private Timer timer = new Timer();

    /**
     * Create a new instance of the {@link DiscordChatServiceContext}
     * with a given channel.
     *
     * @param channel the channel to send the response to.
     */
    public DiscordChatServiceContext(MessageChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean sendMessage(String response) {
        if (response == null || response.isEmpty() || coolDown > System.currentTimeMillis()) {
            return false;
        }
        var typeTime = TimeUtil.calculateRandomizedTypeTime(response);
        coolDown = (long) (System.currentTimeMillis() + typeTime * 1.5);
        var randomDelay = (int) (Math.random() * 1250);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channel.sendTyping().queue();
            }
        }, randomDelay, 5000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                channel.sendMessage(response).queue();
                timer.cancel();
            }
        }, typeTime + randomDelay);
        return true;
    }
}
