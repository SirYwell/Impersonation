package de.hannesgreule.chat.impersonation;

/**
 * Holds methods for time calculation.
 *
 * @author Hannes Greule
 *
 * @version 1.0.0
 */
public final class TimeUtil {

    /**
     * How to it needs to type one letter. Given in milliseconds.
     */
    private static final int MILLIS_PER_LETTER = 560;

    /**
     * The multiplier of the random value added to the calculation.
     * Given in milliseconds.
     */
    private static final int RANDOM_ADDITION_MULTIPLIER = 275;

    private TimeUtil() { }

    /**
     * Calculates a value for how much time it requires to type a given {@link String}.
     * The time is given in milliseconds and varies randomly in a specific range.
     *
     * @param string the {@link String} to calculate the typing duration for.
     * @return the duration of typing the given {@link String}.
     */
    public static int calculateRandomizedTypeTime(String string) {
        return (int) (string.length() * MILLIS_PER_LETTER + (RANDOM_ADDITION_MULTIPLIER * Math.random()));
    }
}
