package io.github.lemcoder.aaudio.model;

public enum AAudioAllowedCapturePolicy {

    /**
     * Indicates that the audio may be captured by any app.
     * For privacy, the following usages can not be recorded: AAUDIO_VOICE_COMMUNICATION*,
     * AAUDIO_USAGE_NOTIFICATION*, AAUDIO_USAGE_ASSISTANCE* and {@link #AAUDIO_USAGE_ASSISTANT}.
     * On <a href="/reference/android/os/Build.VERSION_CODES#Q">Build.VERSION_CODES</a>,
     * this means only {@link #AAUDIO_USAGE_MEDIA} and {@link #AAUDIO_USAGE_GAME} may be captured.
     * See <a href="/reference/android/media/AudioAttributes.html#ALLOW_CAPTURE_BY_ALL">
     * ALLOW_CAPTURE_BY_ALL</a>.
     */
    AAUDIO_ALLOW_CAPTURE_BY_ALL(1),

    /**
     * Indicates that the audio may only be captured by system apps.
     * System apps can capture for many purposes like accessibility, user guidance...
     * but have strong restriction. See
     * <a href="/reference/android/media/AudioAttributes.html#ALLOW_CAPTURE_BY_SYSTEM">
     * ALLOW_CAPTURE_BY_SYSTEM</a>
     * for what the system apps can do with the capture audio.
     */
    AAUDIO_ALLOW_CAPTURE_BY_SYSTEM(2),

    /**
     * Indicates that the audio may not be recorded by any app, even if it is a system app.
     * It is encouraged to use {@link #AAUDIO_ALLOW_CAPTURE_BY_SYSTEM} instead of this value as system apps
     * provide significant and useful features for the user (eg. accessibility).
     * See <a href="/reference/android/media/AudioAttributes.html#ALLOW_CAPTURE_BY_NONE">
     * ALLOW_CAPTURE_BY_NONE</a>.
     */
    AAUDIO_ALLOW_CAPTURE_BY_NONE(3);

    private final int value;

    AAudioAllowedCapturePolicy(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioAllowedCapturePolicy fromValue(int value) {
        for (AAudioAllowedCapturePolicy format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
