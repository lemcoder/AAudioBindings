package io.github.lemcoder.aaudio;

public enum AAudioSpatializationBehavior {

    /**
     * Constant indicating the audio content associated with these attributes will follow the
     * default platform behavior with regards to which content will be spatialized or not.
     */
    AAUDIO_SPATIALIZATION_BEHAVIOR_AUTO(1),

    /**
     * Constant indicating the audio content associated with these attributes should never
     * be spatialized.
     */
    AAUDIO_SPATIALIZATION_BEHAVIOR_NEVER(2);

    private final int value;

    AAudioSpatializationBehavior(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioSpatializationBehavior fromValue(int value) {
        for (AAudioSpatializationBehavior format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
