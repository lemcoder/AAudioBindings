package io.github.lemcoder.aaudio.model;

public enum AAudioChannelMask {
    /**
     * Invalid channel mask
     */
    AAUDIO_CHANNEL_INVALID(-1),
    AAUDIO_CHANNEL_FRONT_LEFT(1 << 0),
    AAUDIO_CHANNEL_FRONT_RIGHT(1 << 1),
    AAUDIO_CHANNEL_FRONT_CENTER(1 << 2),
    AAUDIO_CHANNEL_LOW_FREQUENCY(1 << 3),
    AAUDIO_CHANNEL_BACK_LEFT(1 << 4),
    AAUDIO_CHANNEL_BACK_RIGHT(1 << 5),
    AAUDIO_CHANNEL_FRONT_LEFT_OF_CENTER(1 << 6),
    AAUDIO_CHANNEL_FRONT_RIGHT_OF_CENTER(1 << 7),
    AAUDIO_CHANNEL_BACK_CENTER(1 << 8),
    AAUDIO_CHANNEL_SIDE_LEFT(1 << 9),
    AAUDIO_CHANNEL_SIDE_RIGHT(1 << 10),
    AAUDIO_CHANNEL_TOP_CENTER(1 << 11),
    AAUDIO_CHANNEL_TOP_FRONT_LEFT(1 << 12),
    AAUDIO_CHANNEL_TOP_FRONT_CENTER(1 << 13),
    AAUDIO_CHANNEL_TOP_FRONT_RIGHT(1 << 14),
    AAUDIO_CHANNEL_TOP_BACK_LEFT(1 << 15),
    AAUDIO_CHANNEL_TOP_BACK_CENTER(1 << 16),
    AAUDIO_CHANNEL_TOP_BACK_RIGHT(1 << 17),
    AAUDIO_CHANNEL_TOP_SIDE_LEFT(1 << 18),
    AAUDIO_CHANNEL_TOP_SIDE_RIGHT(1 << 19),
    AAUDIO_CHANNEL_BOTTOM_FRONT_LEFT(1 << 20),
    AAUDIO_CHANNEL_BOTTOM_FRONT_CENTER(1 << 21),
    AAUDIO_CHANNEL_BOTTOM_FRONT_RIGHT(1 << 22),
    AAUDIO_CHANNEL_LOW_FREQUENCY_2(1 << 23),
    AAUDIO_CHANNEL_FRONT_WIDE_LEFT(1 << 24),
    AAUDIO_CHANNEL_FRONT_WIDE_RIGHT(1 << 25),

    // Grouped Channels
    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_MONO(AAUDIO_CHANNEL_FRONT_LEFT.value),

    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_STEREO(AAUDIO_CHANNEL_FRONT_LEFT.value | AAUDIO_CHANNEL_FRONT_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_2POINT1(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_LOW_FREQUENCY.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_TRI(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_FRONT_CENTER.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_TRI_BACK(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_BACK_CENTER.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_3POINT1(AAUDIO_CHANNEL_TRI.value | AAUDIO_CHANNEL_LOW_FREQUENCY.value),

    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_2POINT0POINT2(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_TOP_SIDE_LEFT.value | AAUDIO_CHANNEL_TOP_SIDE_RIGHT.value),

    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_2POINT1POINT2(AAUDIO_CHANNEL_2POINT0POINT2.value | AAUDIO_CHANNEL_LOW_FREQUENCY.value),

    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_3POINT0POINT2(AAUDIO_CHANNEL_TRI.value | AAUDIO_CHANNEL_TOP_SIDE_LEFT.value | AAUDIO_CHANNEL_TOP_SIDE_RIGHT.value),

    /**
     * Supported for Input and Output
     */
    AAUDIO_CHANNEL_3POINT1POINT2(AAUDIO_CHANNEL_3POINT0POINT2.value | AAUDIO_CHANNEL_LOW_FREQUENCY.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_QUAD(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_BACK_LEFT.value | AAUDIO_CHANNEL_BACK_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_QUAD_SIDE(AAUDIO_CHANNEL_STEREO.value | AAUDIO_CHANNEL_SIDE_LEFT.value | AAUDIO_CHANNEL_SIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_SURROUND(AAUDIO_CHANNEL_TRI.value | AAUDIO_CHANNEL_BACK_CENTER.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_PENTA(AAUDIO_CHANNEL_QUAD.value | AAUDIO_CHANNEL_FRONT_CENTER.value),

    /**
     * Supported for Input and Output. aka 5POINT1_BACK
     */
    AAUDIO_CHANNEL_5POINT1(AAUDIO_CHANNEL_QUAD.value | AAUDIO_CHANNEL_FRONT_CENTER.value | AAUDIO_CHANNEL_LOW_FREQUENCY.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_5POINT1_SIDE(AAUDIO_CHANNEL_5POINT1.value | AAUDIO_CHANNEL_SIDE_LEFT.value | AAUDIO_CHANNEL_SIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_6POINT1(AAUDIO_CHANNEL_5POINT1.value | AAUDIO_CHANNEL_BACK_CENTER.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_7POINT1(AAUDIO_CHANNEL_5POINT1.value | AAUDIO_CHANNEL_SIDE_LEFT.value | AAUDIO_CHANNEL_SIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_5POINT1POINT2(AAUDIO_CHANNEL_5POINT1.value | AAUDIO_CHANNEL_TOP_SIDE_LEFT.value | AAUDIO_CHANNEL_TOP_SIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_5POINT1POINT4(AAUDIO_CHANNEL_5POINT1.value | AAUDIO_CHANNEL_TOP_FRONT_LEFT.value | AAUDIO_CHANNEL_TOP_FRONT_RIGHT.value | AAUDIO_CHANNEL_TOP_BACK_LEFT.value | AAUDIO_CHANNEL_TOP_BACK_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_7POINT1POINT2(AAUDIO_CHANNEL_7POINT1.value | AAUDIO_CHANNEL_TOP_SIDE_LEFT.value | AAUDIO_CHANNEL_TOP_SIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_7POINT1POINT4(AAUDIO_CHANNEL_7POINT1.value | AAUDIO_CHANNEL_TOP_FRONT_LEFT.value | AAUDIO_CHANNEL_TOP_FRONT_RIGHT.value | AAUDIO_CHANNEL_TOP_BACK_LEFT.value | AAUDIO_CHANNEL_TOP_BACK_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_9POINT1POINT4(AAUDIO_CHANNEL_7POINT1POINT4.value | AAUDIO_CHANNEL_FRONT_WIDE_LEFT.value | AAUDIO_CHANNEL_FRONT_WIDE_RIGHT.value),

    /**
     * Supported for only Output
     */
    AAUDIO_CHANNEL_9POINT1POINT6(AAUDIO_CHANNEL_9POINT1POINT4.value | AAUDIO_CHANNEL_TOP_SIDE_LEFT.value | AAUDIO_CHANNEL_TOP_SIDE_RIGHT.value),

    /**
     * Supported for only Input
     */
    AAUDIO_CHANNEL_FRONT_BACK(AAUDIO_CHANNEL_FRONT_CENTER.value | AAUDIO_CHANNEL_BACK_CENTER.value);

    private final int value;

    AAudioChannelMask(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioChannelMask fromValue(int value) {
        for (AAudioChannelMask channel : values()) {
            if (channel.value == value) {
                return channel;
            }
        }
        return AAUDIO_CHANNEL_INVALID;
    }
}
