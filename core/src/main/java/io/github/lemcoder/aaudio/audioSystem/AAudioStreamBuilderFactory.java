package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.*;

public class AAudioStreamBuilderFactory {

    /**
     * Create a StreamBuilder that can be used to open a Stream.
     * <p>
     * The deviceId is initially unspecified, meaning that the current default device will be used.
     * <p>
     * The default direction is {AAudioAudioDirection.AAUDIO_DIRECTION_OUTPUT}.
     * The default sharing mode is {AAudioSharingMode.AAUDIO_SHARING_MODE_SHARED}.
     * The data format, samplesPerFrames and sampleRate are unspecified and will be
     * chosen by the device when it is opened.
     * <p>
     * AAudioStreamBuilder_delete() must be called when you are done using the builder.
     * <p>
     * Available since API level 26.
     *
     * @return AAudioStreamBuilder instance
     */
    public static AAudioStreamBuilder createStreamBuilder() throws Exception {
        Arena lifetimeArena = Arena.ofShared();
        return new AAudioStreamBuilder(lifetimeArena);
    }
}
