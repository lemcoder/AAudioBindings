package io.github.lemcoder.aaudio;

/**
 * These may be used with AAudioStreamBuilder_setSessionId().
 * Added in API level 28.
 */
public enum AAudioSessionId {
    /**
     * Do not allocate a session ID.
     * Effects cannot be used with this stream.
     * Default.
     * Added in API level 28.
     */
    AAUDIO_SESSION_ID_NONE(-1),

    /**
     * Allocate a session ID that can be used to attach and control
     * effects using the Java AudioEffects API.
     * Note that using this may result in higher latency.
     * Note that this matches the value of AudioManager.AUDIO_SESSION_ID_GENERATE.
     * Added in API level 28.
     */
    AAUDIO_SESSION_ID_ALLOCATE(0);

    private final int value;

    AAudioSessionId(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioSessionId fromValue(int value) {
        for (AAudioSessionId format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
