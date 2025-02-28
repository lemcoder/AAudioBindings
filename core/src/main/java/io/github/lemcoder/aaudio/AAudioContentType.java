package io.github.lemcoder.aaudio;

public enum AAudioContentType {
    /**
     * Use this for spoken voice, audiobooks, etcetera.
     */
    AAUDIO_CONTENT_TYPE_SPEECH(1),

    /**
     * Use this for pre-recorded or live music.
     */
    AAUDIO_CONTENT_TYPE_MUSIC(2),

    /**
     * Use this for a movie or video soundtrack.
     */
    AAUDIO_CONTENT_TYPE_MOVIE(3),

    /**
     * Use this for sound is designed to accompany a user action,
     * such as a click or beep sound made when the user presses a button.
     */
    AAUDIO_CONTENT_TYPE_SONIFICATION(4);

    private final int value;

    AAudioContentType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioContentType fromValue(int value) {
        for (AAudioContentType format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
