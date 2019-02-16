package de.hannesgreule.chat.impersonation;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class DelayedAnswerContext<T> implements ChatServiceContext {
    private final Consumer<T> sendTyping;
    private final BiConsumer<T, String> sendMessage;
    private final T where;
    private final Timer timer;
    private long coolDown;

    public DelayedAnswerContext(Consumer<T> sendTyping, BiConsumer<T, String> sendMessage, T where) {
        this.sendTyping = sendTyping;
        this.sendMessage = sendMessage;

        this.where = where;

        this.timer = new Timer();
    }

    protected boolean sendMessageDelayed(String message) {
        if (message == null || message.isEmpty() || coolDown > System.currentTimeMillis()) {
            return false;
        }
        var typeTime = TimeUtil.calculateRandomizedTypeTime(message);
        coolDown = (long) (System.currentTimeMillis() + typeTime * 1.5);
        var randomDelay = (int) (Math.random() * 1250);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendTyping.accept(where);
            }
        }, randomDelay, 5000);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage.accept(where, message);
                timer.cancel();
            }
        }, typeTime + randomDelay);
        return true;
    }
}
