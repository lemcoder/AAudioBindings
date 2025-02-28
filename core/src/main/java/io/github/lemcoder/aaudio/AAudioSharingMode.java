package io.github.lemcoder.aaudio;

public enum AAudioSharingMode {
    /**
     * This will be the only stream using a particular source or sink.
     * This mode will provide the lowest possible latency.
     * You should close EXCLUSIVE streams immediately when you are not using them.
     */
    AAUDIO_SHARING_MODE_EXCLUSIVE(0),
    /**
     * Multiple applications will be mixed by the AAudio Server.
     * This will have higher latency than the EXCLUSIVE mode.
     */
    AAUDIO_SHARING_MODE_SHARED(0);

    private final int value;

    AAudioSharingMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioSharingMode fromValue(int value) {
        for (AAudioSharingMode format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
