package io.github.lemcoder.aaudio.model;

public enum AAudioInputPreset {
    /**
     * Use this preset when other presets do not apply.
     */
    AAUDIO_INPUT_PRESET_GENERIC(1),

    /**
     * Use this preset when recording video.
     */
    AAUDIO_INPUT_PRESET_CAMCORDER(5),

    /**
     * Use this preset when doing speech recognition.
     */
    AAUDIO_INPUT_PRESET_VOICE_RECOGNITION(6),

    /**
     * Use this preset when doing telephony or voice messaging.
     */
    AAUDIO_INPUT_PRESET_VOICE_COMMUNICATION(7),

    /**
     * Use this preset to obtain an input with no effects.
     * Note that this input will not have automatic gain control
     * so the recorded volume may be very low.
     */
    AAUDIO_INPUT_PRESET_UNPROCESSED(9),

    /**
     * Use this preset for capturing audio meant to be processed in real time
     * and played back for live performance (e.g karaoke).
     * The capture path will minimize latency and coupling with playback path.
     * Available since API level 29.
     */
    AAUDIO_INPUT_PRESET_VOICE_PERFORMANCE(10),

    /**
     * Use this preset for an echo canceller to capture the reference signal.
     * Reserved for system components.
     * Requires CAPTURE_AUDIO_OUTPUT permission
     * Available since API level 35.
     */
    AAUDIO_INPUT_PRESET_SYSTEM_ECHO_REFERENCE(1997),

    /**
     * Use this preset for preemptible, low-priority software hotword detection.
     * Reserved for system components.
     * Requires CAPTURE_AUDIO_HOTWORD permission.
     * Available since API level 35.
     */
    AAUDIO_INPUT_PRESET_SYSTEM_HOTWORD(1999);

    private final int value;

    AAudioInputPreset(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioInputPreset fromValue(int value) {
        for (AAudioInputPreset format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
