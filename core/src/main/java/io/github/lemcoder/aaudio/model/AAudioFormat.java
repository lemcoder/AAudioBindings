package io.github.lemcoder.aaudio.model;

public enum AAudioFormat {
    INVALID(-1),
    UNSPECIFIED(0),
    /**
     * This format uses the int16_t data type.
     * The maximum range of the data is -32768 (0x8000) to 32767 (0x7FFF).
     */
    PCM_I16(1),

    /**
     * This format uses the float data type.
     * The nominal range of the data is [-1.0f, 1.0f).
     * Values outside that range may be clipped.
     * See also the float Data in
     * <a href="/reference/android/media/AudioTrack#write(float[],%20int,%20int,%20int)">
     *   write(float[], int, int, int)</a>.
     */
    PCM_FLOAT(2),

    /**
     * This format uses 24-bit samples packed into 3 bytes.
     * The bytes are in little-endian order, so the least significant byte
     * comes first in the byte array.
     * The maximum range of the data is -8388608 (0x800000)
     * to 8388607 (0x7FFFFF).
     * Note that the lower precision bits may be ignored by the device.
     * Available since API level 31.
     */
    PCM_I24_PACKED(3),

    /**
     * This format uses 32-bit samples stored in an int32_t data type.
     * The maximum range of the data is -2147483648 (0x80000000)
     * to 2147483647 (0x7FFFFFFF).
     * Note that the lower precision bits may be ignored by the device.
     * Available since API level 31.
     */
    PCM_I32(4),

    /**
     * This format is used for compressed audio wrapped in IEC61937 for HDMI
     * or S/PDIF passthrough.
     * Unlike PCM playback, the Android framework is not able to do format
     * conversion for IEC61937. In that case, when IEC61937 is requested, sampling
     * rate and channel count or channel mask must be specified. Otherwise, it may
     * fail when opening the stream. Apps are able to get the correct configuration
     * for the playback by calling
     * <a href="/reference/android/media/AudioManager#getDevices(int)">
     *   AudioManager#getDevices(int)</a>.
     * Available since API level 34.
     */
    IEC61937(5);

    private final int value;

    AAudioFormat(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioFormat fromValue(int value) {
        for (AAudioFormat format : values()) {
            if (format.value == value) {
                return format;
            }
        }
        return null;
    }
}
