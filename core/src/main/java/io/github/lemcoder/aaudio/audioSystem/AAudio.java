package io.github.lemcoder.aaudio.audioSystem;

import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.C_BOOL;
import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.C_INT;
import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.C_POINTER;
import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.LINKER;

import com.v7878.foreign.Arena;
import com.v7878.foreign.FunctionDescriptor;
import com.v7878.foreign.MemorySegment;
import com.v7878.foreign.SymbolLookup;
import com.v7878.foreign.ValueLayout;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import io.github.lemcoder.aaudio.AAudioAllowedCapturePolicy;
import io.github.lemcoder.aaudio.AAudioAudioDirection;
import io.github.lemcoder.aaudio.AAudioCallbackResult;
import io.github.lemcoder.aaudio.AAudioChannelMask;
import io.github.lemcoder.aaudio.AAudioContentType;
import io.github.lemcoder.aaudio.AAudioFormat;
import io.github.lemcoder.aaudio.AAudioInputPreset;
import io.github.lemcoder.aaudio.AAudioPerformanceMode;
import io.github.lemcoder.aaudio.AAudioResult;
import io.github.lemcoder.aaudio.AAudioSessionId;
import io.github.lemcoder.aaudio.AAudioSharingMode;
import io.github.lemcoder.aaudio.AAudioSpatializationBehavior;
import io.github.lemcoder.aaudio.AAudioStreamState;
import io.github.lemcoder.aaudio.AAudioUsage;
import io.github.lemcoder.aaudio.api.AAudioErrorCallbackApi;
import io.github.lemcoder.aaudio.api.AAudioStreamDataCallbackApi;


public class AAudio {

    AAudio() {
        // Should not be called directly
    }

    public static int AAUDIO_UNSPECIFIED = 0;

    // ============================================================
    // Audio System
    // ============================================================

    /**
     * The text is the ASCII symbol corresponding to the returnCode,
     * or an English message saying the returnCode is unrecognized.
     * This is intended for developers to use when debugging.
     * It is not for display to users.
     * <p>
     * Available since API level 26.
     *
     * @return pointer to a text representation of an AAudio result code.
     */
    public static String AAudioConvertResultToText(AAudioResult result) throws Throwable {
        MemorySegment pResult = (MemorySegment) AAudio_convertResultToText.invokeExact(result.getValue());

        return pResult.getString(0);
    }

    private final static MethodHandle AAudio_convertResultToText = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudio_convertResultToText"),
            FunctionDescriptor.of(C_POINTER, C_INT)
    );

    /**
     * The text is the ASCII symbol corresponding to the stream state,
     * or an English message saying the state is unrecognized.
     * This is intended for developers to use when debugging.
     * It is not for display to users.
     * <p>
     * Available since API level 26.
     *
     * @return pointer to a text representation of an AAudio state.
     */
    public static String AAudioConvertStreamStateToText(AAudioStreamState state) throws Throwable {
        MemorySegment pResult = (MemorySegment) AAudio_convertStreamStateToText.invokeExact(state.getValue());

        return pResult.getString(0);
    }

    private final static MethodHandle AAudio_convertStreamStateToText = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudio_convertStreamStateToText"),
            FunctionDescriptor.of(C_POINTER, C_INT)
    );

    // ============================================================
    // StreamBuilder
    // ============================================================

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
     */
    public static MemorySegment AAudioCreateStreamBuilder() throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            MemorySegment ptr = scope.allocate(C_POINTER);
            int result = (int) AAudio_createStreamBuilder.invokeExact(ptr);
            if (result != AAudioResult.OK.getValue()) {
                throw new RuntimeException("Failed to create AAudio stream builder: " + result);
            }

            return ptr.get(ValueLayout.ADDRESS, 0);
        }
    }

    private final static MethodHandle AAudio_createStreamBuilder = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudio_createStreamBuilder"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Request an audio device identified by an ID.
     * <p>
     * The ID could be obtained from the Java AudioManager.
     * AudioManager.getDevices() returns an array of {AudioDeviceInfo},
     * which contains a getId() method. That ID can be passed to this function.
     * <p>
     * It is possible that you may not get the device that you requested.
     * So if it is important to you, you should call
     * AAudioStream_getDeviceId() after the stream is opened to
     * verify the actual ID.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED},
     * in which case the primary device will be used.
     * <p>
     * Available since API level 26.
     *
     * @param builder  reference provided by AAudio_createStreamBuilder()
     * @param deviceId device identifier or {@link #AAUDIO_UNSPECIFIED}
     */
    public static void AAudioStreamBuilderSetDeviceId(MemorySegment builder, int deviceId) throws Throwable {
        AAudioStreamBuilder_setDeviceId.invokeExact(builder, deviceId);
    }

    private final static MethodHandle AAudioStreamBuilder_setDeviceId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDeviceId"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Declare the name of the package creating the stream.
     * <p>
     * This is usually {@code Context#getPackageName()}.
     * <p>
     * The default, if you do not call this function, is a random package in the calling uid.
     * The vast majority of apps have only one package per calling UID.
     * If an invalid package name is set, input streams may not be given permission to
     * record when started.
     * <p>
     * The package name is usually the applicationId in your app's build.gradle file.
     * <p>
     * Available since API level 31.
     *
     * @param builder     reference provided by AAudio_createStreamBuilder()
     * @param packageName packageName of the calling app.
     */
    public static void AAudioStreamBuilderSetPackageName(MemorySegment builder, String packageName) throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            MemorySegment pPackageName = scope.allocateFrom(packageName);

            AAudioStreamBuilder_setPackageName.invokeExact(builder, pPackageName);
        }
    }

    private final static MethodHandle AAudioStreamBuilder_setPackageName = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPackageName"),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER)
    );

    /**
     * Declare the attribution tag of the context creating the stream.
     * <p>
     * This is usually {@code Context#getAttributionTag()}.
     * <p>
     * The default, if you do not call this function, is null.
     * <p>
     * Available since API level 31.
     *
     * @param builder        reference provided by AAudio_createStreamBuilder()
     * @param attributionTag attributionTag of the calling context.
     */
    public static void AAudioStreamBuilderSetAttributionTag(MemorySegment builder, String attributionTag) throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            MemorySegment pAttributionTag = scope.allocateFrom(attributionTag);

            AAudioStreamBuilder_setAttributionTag.invokeExact(builder, pAttributionTag);
        }
    }

    private final static MethodHandle AAudioStreamBuilder_setAttributionTag = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setAttributionTag"),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER)
    );

    /**
     * Request a sample rate in Hertz.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * An optimal value will then be chosen when the stream is opened.
     * After opening a stream with an unspecified value, the application must
     * query for the actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * Available since API level 26.
     *
     * @param builder    reference provided by AAudio_createStreamBuilder()
     * @param sampleRate frames per second. Common rates include 44100 and 48000 Hz.
     */
    public static void AAudioStreamBuilderSetSampleRate(MemorySegment builder, int sampleRate) throws Throwable {
        AAudioStreamBuilder_setSampleRate.invokeExact(builder, sampleRate);
    }

    private final static MethodHandle AAudioStreamBuilder_setSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSampleRate"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Request a number of channels for the stream.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * An optimal value will then be chosen when the stream is opened.
     * After opening a stream with an unspecified value, the application must
     * query for the actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * As the channel count provided here may be different from the corresponding channel count
     * of channel mask used in {AAudioChannelMask.AAudioStreamBuilderSetChannelMask}, the last called function
     * will be respected if both this function and {AAudioStreamBuilderSetChannelMask} are
     * called.
     * <p>
     * Note that if the channel count is two then it may get mixed to mono when the device only supports
     * one channel. If the channel count is greater than two but the device's supported channel count is
     * less than the requested value, the channels higher than the device channel will be dropped. If
     * higher channels should be mixed or spatialized, use {AAudioStreamBuilderSetChannelMask}
     * instead.
     * <p>
     * Available since API level 26.
     *
     * @param builder      reference provided by AAudio_createStreamBuilder()
     * @param channelCount Number of channels desired.
     */
    public static void AAudioStreamBuilderSetChannelCount(MemorySegment builder, int channelCount) throws Throwable {
        AAudioStreamBuilder_setChannelCount.invokeExact(builder, channelCount);
    }

    private final static MethodHandle AAudioStreamBuilder_setChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setChannelCount"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Identical to AAudioStreamBuilder_setChannelCount().
     * Available since API level 26.
     *
     * @param builder         reference provided by AAudio_createStreamBuilder()
     * @param samplesPerFrame Number of samples in a frame.
     */
    @Deprecated()
    public static void AAudioStreamBuilderSetSamplesPerFrame(MemorySegment builder, int samplesPerFrame) throws Throwable {
        AAudioStreamBuilder_setSamplesPerFrame.invokeExact(builder, samplesPerFrame);
    }

    private final static MethodHandle AAudioStreamBuilder_setSamplesPerFrame = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSamplesPerFrame"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Request a sample data format, for example {AAudioFormat.AAUDIO_FORMAT_PCM_I16}.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * An optimal value will then be chosen when the stream is opened.
     * After opening a stream with an unspecified value, the application must
     * query for the actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * Available since API level 26.
     *
     * @param builder reference provided by AAudio_createStreamBuilder()
     * @param format  common formats are {AAudioFormat.AAUDIO_FORMAT_PCM_FLOAT} and {AAudioFormat.AAUDIO_FORMAT_PCM_I16}.
     */
    public static void AAudioStreamBuilderSetFormat(MemorySegment builder, AAudioFormat format) throws Throwable {
        AAudioStreamBuilder_setFormat.invokeExact(builder, format.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setFormat"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Request a mode for sharing the device.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_SHARING_MODE_SHARED}.
     * <p>
     * The requested sharing mode may not be available.
     * The application can query for the actual mode after the stream is opened.
     * <p>
     * Available since API level 26.
     *
     * @param builder     reference provided by AAudio_createStreamBuilder()
     * @param sharingMode {AAUDIO_SHARING_MODE_SHARED} or {AAUDIO_SHARING_MODE_EXCLUSIVE}
     */
    public static void AAudioStreamBuilderSetSharingMode(MemorySegment builder, AAudioSharingMode sharingMode) throws Throwable {
        AAudioStreamBuilder_setSharingMode.invokeExact(builder, sharingMode.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setSharingMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSharingMode"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Request the direction for a stream.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_DIRECTION_OUTPUT}.
     * <p>
     * Available since API level 26.
     *
     * @param builder   reference provided by AAudio_createStreamBuilder()
     * @param direction {AAUDIO_DIRECTION_OUTPUT} or {AAUDIO_DIRECTION_INPUT}
     */
    public static void AAudioStreamBuilderSetDirection(MemorySegment builder, AAudioAudioDirection direction) throws Throwable {
        AAudioStreamBuilder_setDirection.invokeExact(builder, direction.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setDirection = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDirection"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Set the requested buffer capacity in frames.
     * The final AAudioStream capacity may differ, but will probably be at least this big.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * <p>
     * Available since API level 26.
     *
     * @param builder   reference provided by AAudio_createStreamBuilder()
     * @param numFrames the desired buffer capacity in frames or {@link #AAUDIO_UNSPECIFIED}
     */
    public static void AAudioStreamBuilderSetBufferCapacityInFrames(MemorySegment builder, int numFrames) throws Throwable {
        AAudioStreamBuilder_setBufferCapacityInFrames.invokeExact(builder, numFrames);
    }

    private final static MethodHandle AAudioStreamBuilder_setBufferCapacityInFrames = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setBufferCapacityInFrames"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Set the requested performance mode.
     * <p>
     * Supported modes are {AAUDIO_PERFORMANCE_MODE_NONE},
     * {AAUDIO_PERFORMANCE_MODE_POWER_SAVING} * and {AAUDIO_PERFORMANCE_MODE_LOW_LATENCY}.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_PERFORMANCE_MODE_NONE}.
     * <p>
     * You may not get the mode you requested.
     * You can call AAudioStream_getPerformanceMode()
     * to find out the final mode for the stream.
     * <p>
     * Available since API level 26.
     *
     * @param builder reference provided by AAudio_createStreamBuilder()
     * @param mode    the desired performance mode, eg. {AAUDIO_PERFORMANCE_MODE_LOW_LATENCY}
     */
    public static void AAudioStreamBuilderSetPerformanceMode(MemorySegment builder, AAudioPerformanceMode mode) throws Throwable {
        AAudioStreamBuilder_setPerformanceMode.invokeExact(builder, mode.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setPerformanceMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPerformanceMode"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Set the intended use case for the output stream.
     * <p>
     * The AAudio system will use this information to optimize the
     * behavior of the stream.
     * This could, for example, affect how volume and focus is handled for the stream.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_USAGE_MEDIA}.
     * <p>
     * Available since API level 28.
     *
     * @param builder reference provided by AAudio_createStreamBuilder()
     * @param usage   the desired usage, eg. {AAUDIO_USAGE_GAME}
     */
    public static void AAudioStreamBuilderSetUsage(MemorySegment builder, AAudioUsage usage) throws Throwable {
        AAudioStreamBuilder_setUsage.invokeExact(builder, usage.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setUsage = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setUsage"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Set the type of audio data that the output stream will carry.
     * <p>
     * The AAudio system will use this information to optimize the
     * behavior of the stream.
     * This could, for example, affect whether a stream is paused when a notification occurs.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_CONTENT_TYPE_MUSIC}.
     * <p>
     * Available since API level 28.
     *
     * @param builder     reference provided by AAudio_createStreamBuilder()
     * @param contentType the type of audio data, eg. {AAUDIO_CONTENT_TYPE_SPEECH}
     */
    public static void AAudioStreamBuilderSetContentType(MemorySegment builder, AAudioContentType contentType) throws Throwable {
        AAudioStreamBuilder_setContentType.invokeExact(builder, contentType.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setContentType = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setContentType"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Sets the behavior affecting whether spatialization will be used.
     * <p>
     * The AAudio system will use this information to select whether the stream will go
     * through a spatializer effect or not when the effect is supported and enabled.
     * <p>
     * Available since API level 32.
     *
     * @param builder                reference provided by AAudio_createStreamBuilder()
     * @param spatializationBehavior the desired behavior with regards to spatialization, eg.{AAUDIO_SPATIALIZATION_BEHAVIOR_AUTO}
     */
    public static void AAudioStreamBuilderSetSpatializationBehavior(MemorySegment builder, AAudioSpatializationBehavior spatializationBehavior) throws Throwable {
        AAudioStreamBuilder_setSpatializationBehavior.invokeExact(builder, spatializationBehavior.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setSpatializationBehavior = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSpatializationBehavior"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Specifies whether the audio data of this output stream has already been processed for
     * spatialization.
     * <p>
     * If the stream has been processed for spatialization, setting this to true will prevent
     * issues such as double-processing on platforms that will spatialize audio data.
     * <p>
     * Available since API level 32.
     *
     * @param builder       reference provided by AAudio_createStreamBuilder()
     * @param isSpatialized true if the content is already processed for binaural or transaural spatial
     *                      rendering, false otherwise.
     */
    public static void AAudioStreamBuilderSetIsContentSpatialized(MemorySegment builder, boolean isSpatialized) throws Throwable {
        AAudioStreamBuilder_setIsContentSpatialized.invokeExact(builder, isSpatialized);
    }

    private final static MethodHandle AAudioStreamBuilder_setIsContentSpatialized = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setIsContentSpatialized"),
            FunctionDescriptor.ofVoid(C_POINTER, C_BOOL)
    );

    /**
     * Set the input (capture) preset for the stream.
     * <p>
     * The AAudio system will use this information to optimize the
     * behavior of the stream.
     * This could, for example, affect which microphones are used and how the
     * recorded data is processed.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_INPUT_PRESET_VOICE_RECOGNITION}.
     * That is because VOICE_RECOGNITION is the preset with the lowest latency
     * on many platforms.
     * <p>
     * Available since API level 28.
     *
     * @param builder     reference provided by AAudio_createStreamBuilder()
     * @param inputPreset the desired configuration for recording
     */
    public static void AAudioStreamBuilderSetInputPreset(MemorySegment builder, AAudioInputPreset inputPreset) throws Throwable {
        AAudioStreamBuilder_setInputPreset.invokeExact(builder, inputPreset.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setInputPreset = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setInputPreset"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Specify whether this stream audio may or may not be captured by other apps or the system.
     * <p>
     * The default is {AAUDIO_ALLOW_CAPTURE_BY_ALL}.
     * <p>
     * Note that an application can also set its global policy, in which case the most restrictive
     * policy is always applied. See
     * <a href="/reference/android/media/AudioManager#setAllowedCapturePolicy(int)">
     * setAllowedCapturePolicy(int)</a>
     * <p>
     * Available since API level 29.
     *
     * @param builder       reference provided by AAudio_createStreamBuilder()
     * @param capturePolicy the desired level of opt-out from being captured.
     */
    public static void AAudioStreamBuilderSetAllowedCapturePolicy(MemorySegment builder, AAudioAllowedCapturePolicy capturePolicy) throws Throwable {
        AAudioStreamBuilder_setAllowedCapturePolicy.invokeExact(builder, capturePolicy.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setAllowedCapturePolicy = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setAllowedCapturePolicy"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Set the requested session ID.
     * <p>
     * The session ID can be used to associate a stream with effects processors.
     * The effects are controlled using the Android AudioEffect Java API.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_SESSION_ID_NONE}.
     * <p>
     * If set to {AAUDIO_SESSION_ID_ALLOCATE} then a session ID will be allocated
     * when the stream is opened.
     * <p>
     * The allocated session ID can be obtained by calling AAudioStream_getSessionId()
     * and then used with this function when opening another stream.
     * This allows effects to be shared between streams.
     * <p>
     * Session IDs from AAudio can be used with the Android Java APIs and vice versa.
     * So a session ID from an AAudio stream can be passed to Java
     * and effects applied using the Java AudioEffect API.
     * <p>
     * Note that allocating or setting a session ID may result in a stream with higher latency.
     * <p>
     * Allocated session IDs will always be positive and nonzero.
     * <p>
     * Available since API level 28.
     *
     * @param builder   reference provided by AAudio_createStreamBuilder()
     * @param sessionId an allocated sessionID or {AAUDIO_SESSION_ID_ALLOCATE}
     */
    public static void AAudioStreamBuilderSetAllowedCapturePolicy(MemorySegment builder, AAudioSessionId sessionId) throws Throwable {
        AAudioStreamBuilder_setSessionId.invokeExact(builder, sessionId.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setSessionId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSessionId"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Indicates whether this input stream must be marked as privacy sensitive or not.
     * <p>
     * When true, this input stream is privacy sensitive and any concurrent capture
     * is not permitted.
     * <p>
     * This is off (false) by default except when the input preset is {AAUDIO_INPUT_PRESET_VOICE_COMMUNICATION}
     * or {AAUDIO_INPUT_PRESET_CAMCORDER}.
     * <p>
     * Always takes precedence over default from input preset when set explicitly.
     * <p>
     * Only relevant if the stream direction is {AAUDIO_DIRECTION_INPUT}.
     * <p>
     * Added in API level 30.
     *
     * @param builder          reference provided by AAudio_createStreamBuilder()
     * @param privacySensitive true if capture from this stream must be marked as privacy sensitive,
     *                         false otherwise.
     */
    public static void AAudioStreamBuilderSetAllowedCapturePolicy(MemorySegment builder, boolean privacySensitive) throws Throwable {
        AAudioStreamBuilder_setPrivacySensitive.invokeExact(builder, privacySensitive);
    }

    private final static MethodHandle AAudioStreamBuilder_setPrivacySensitive = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPrivacySensitive"),
            FunctionDescriptor.ofVoid(C_POINTER, C_BOOL)
    );

    /**
     * Request that AAudio call this functions when the stream is running.
     * <p>
     * Note that when using this callback, the audio data will be passed in or out
     * of the function as an argument.
     * So you cannot call AAudioStream_write() or AAudioStream_read()
     * on the same stream that has an active data callback.
     * <p>
     * The callback function will start being called after AAudioStream_requestStart()
     * is called.
     * It will stop being called after AAudioStream_requestPause() or
     * AAudioStream_requestStop() is called.
     * <p>
     * This callback function will be called on a real-time thread owned by AAudio.
     * The low latency streams may have callback threads with higher priority than normal streams.
     * See {AAudioStream_dataCallback} for more information.
     * <p>
     * Note that the AAudio callbacks will never be called simultaneously from multiple threads.
     * <p>
     * Available since API level 26.
     *
     * @param builder  reference provided by AAudio_createStreamBuilder()
     * @param callback a function that will process audio data.
     */
    public static void AAudioStreamBuilderSetDataCallback(Arena lifetime, MemorySegment builder, AAudioStreamDataCallbackApi callback) throws Throwable {
        // Create a stub as a native symbol to be passed into native function.
        AAudioStreamDataCallbackInternal callbackInternal = (stream, userData, audioData, numFrames) -> {
            // TODO: All memory segments will be zero size because there is nowhere to get this information from. It is the same as a pointer in C
            //  The size will need to be specified here via MemorySegment.reinterpret (It should be known as in C)
            if (audioData == null || audioData.equals(MemorySegment.NULL) || audioData.byteSize() == 0) {
                System.err.println("Error: Invalid or empty audioData buffer!");
                return AAudioCallbackResult.CONTINUE.getValue(); // Stop if buffer is unusable
            }

            int numChannels = 2; // Change based on your stream format
            int bytesPerSample = Float.BYTES; // Assuming 32-bit float PCM
            int expectedSize = numFrames * numChannels * bytesPerSample;

            byte[] data = callback.onData(numFrames);

            // Ensure we don't copy beyond available space
            int copySize = Math.min(data.length, expectedSize);

            MemorySegment.copy(data, 0, audioData, ValueLayout.JAVA_BYTE, 0, copySize);

            // Zero out any remaining space if needed
            if (copySize < expectedSize) {
                audioData.asSlice(copySize, expectedSize - copySize).fill((byte) 0);
            }

            return AAudioCallbackResult.CONTINUE.getValue();
        };

        MemorySegment pCallback = createDataCallbackPtr(lifetime, callbackInternal);

        AAudioStreamBuilder_setDataCallback.invokeExact(builder, pCallback, MemorySegment.NULL);
    }

    private final static MethodHandle AAudioStreamBuilder_setDataCallback = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDataCallback"),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER, C_POINTER)
    );

    private static MemorySegment createDataCallbackPtr(Arena lifetime, AAudioStreamDataCallbackInternal callback) throws Throwable {
        // This is statically known data, no need to create and look for it every call
        class Holder {
            static final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(
                    ValueLayout.JAVA_INT,  // aaudio_data_callback_result_t is int
                    C_POINTER,   // AAudioStream* (non-null)
                    C_POINTER,  // void* userData (nullable)
                    C_POINTER,   // void* audioData (non-null)
                    ValueLayout.JAVA_INT   // int32_t numFrames
            );
            static final MethodHandle handle;

            static {
                try {
                    // TODO: All classes used in reflection must be added to keep rules for r8 - otherwise they may be changed during optimization.
                    MethodHandles.Lookup lookup = MethodHandles.lookup();
                    handle = lookup.findVirtual(
                            AAudioStreamDataCallbackInternal.class,
                            "onData",
                            MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class, int.class)
                    );
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }

        return LINKER.upcallStub(Holder.handle.bindTo(callback), Holder.descriptor, lifetime);
    }

    /**
     * Set the requested data callback buffer size in frames.
     * See {AAudioStream_dataCallback}.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * <p>
     * For the lowest possible latency, do not call this function. AAudio will then
     * call the dataProc callback function with whatever size is optimal.
     * That size may vary from one callback to another.
     * <p>
     * Only use this function if the application requires a specific number of frames for processing.
     * The application might, for example, be using an FFT that requires
     * a specific power-of-two sized buffer.
     * <p>
     * AAudio may need to add additional buffering in order to adapt between the internal
     * buffer size and the requested buffer size.
     * <p>
     * If you do call this function then the requested size should be less than
     * half the buffer capacity, to allow double buffering.
     * <p>
     * Available since API level 26.
     *
     * @param builder   reference provided by AAudio_createStreamBuilder()
     * @param numFrames the desired buffer size in frames or {@link #AAUDIO_UNSPECIFIED}
     */
    public static void AAudioStreamBuilderSetFramesPerDataCallback(MemorySegment builder, int numFrames) throws Throwable {
        AAudioStreamBuilder_setFramesPerDataCallback.invokeExact(builder, numFrames);
    }

    private final static MethodHandle AAudioStreamBuilder_setFramesPerDataCallback = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setFramesPerDataCallback"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    /**
     * Request that AAudio call this function if any error occurs or the stream is disconnected.
     * <p>
     * It will be called, for example, if a headset or a USB device is unplugged causing the stream's
     * device to be unavailable or "disconnected".
     * Another possible cause of error would be a timeout or an unanticipated internal error.
     * <p>
     * In response, this function should signal or create another thread to stop
     * and close this stream. The other thread could then reopen a stream on another device.
     * Do not stop or close the stream, or reopen the new stream, directly from this callback.
     * <p>
     * This callback will not be called because of actions by the application, such as stopping
     * or closing a stream.
     * <p>
     * Note that the AAudio callbacks will never be called simultaneously from multiple threads.
     * <p>
     * Available since API level 26.
     *
     * @param builder  reference provided by AAudio_createStreamBuilder()
     * @param callback pointer to a function that will be called if an error occurs.
     */
    public static void AAudioStreamBuilderSetErrorCallback(Arena lifetime, MemorySegment builder, AAudioErrorCallbackApi callback) throws Throwable {
        // Create a stub as a native symbol to be passed into native function.
        AAudioErrorCallbackInternal callbackInternal = (stream, userData, error) -> {
            callback.onError(AAudioResult.fromValue(error));
        };

        MemorySegment pCallback = createErrorCallbackPtr(lifetime, callbackInternal);

        AAudioStreamBuilder_setErrorCallback.invokeExact(builder, pCallback, MemorySegment.NULL);
    }

    private final static MethodHandle AAudioStreamBuilder_setErrorCallback = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setErrorCallback"),
            FunctionDescriptor.ofVoid(C_POINTER, C_POINTER, C_POINTER)
    );

    private static MemorySegment createErrorCallbackPtr(Arena lifetime, AAudioErrorCallbackInternal callback) {
        // This is statically known data, no need to create and look for it every call
        class Holder {
            static final FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(
                    C_POINTER,   // AAudioStream* (non-null)
                    C_POINTER,   // void* userData (nullable)
                    C_INT        // error (non-null)
            );
            static final MethodHandle handle;

            static {
                try {
                    // TODO: All classes used in reflection must be added to keep rules for r8 - otherwise they may be changed during optimization.
                    MethodHandles.Lookup lookup = MethodHandles.lookup();
                    handle = lookup.findVirtual(
                            AAudioErrorCallbackInternal.class,
                            "onError",
                            MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class, int.class)
                    );
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }

        return LINKER.upcallStub(Holder.handle.bindTo(callback), Holder.descriptor, lifetime);
    }

    /**
     * Open a stream based on the options in the StreamBuilder.
     * <p>
     * AAudioStream_close() must be called when finished with the stream to recover
     * the memory and to free the associated resources.
     * <p>
     * Available since API level 26.
     *
     * @param builder reference provided by AAudio_createStreamBuilder()
     * @return pointer to a variable to receive the new stream reference
     * @throws RuntimeException if the stream could not be opened
     */
    public static MemorySegment AAudioStreamBuilderOpenStream(MemorySegment builder) throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            // allocate a pointer to store the stream
            MemorySegment ptr = scope.allocate(C_POINTER);

            int result = (int) AAudioStreamBuilder_openStream.invokeExact(builder, ptr);
            if (result != AAudioResult.OK.getValue()) {
                throw new RuntimeException("Failed to open stream: " + result);
            }
            return ptr.get(ValueLayout.ADDRESS, 0);
        }
    }

    private final static MethodHandle AAudioStreamBuilder_openStream = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_openStream"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER)
    );

    /**
     * Delete the resources associated with the StreamBuilder.
     * <p>
     * Available since API level 26.
     *
     * @param builder reference provided by AAudio_createStreamBuilder()
     * @return {AAUDIO_OK} or a negative error.
     */
    public int AAudioStreamBuilderDelete(MemorySegment builder) throws Throwable {
        return (int) AAudioStreamBuilder_delete.invokeExact(builder);
    }

    private final static MethodHandle AAudioStreamBuilder_delete = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_delete"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Set audio channel mask for the stream.
     * <p>
     * The default, if you do not call this function, is {@link #AAUDIO_UNSPECIFIED}.
     * If both channel mask and count are not set, then stereo will then be chosen when the
     * stream is opened.
     * After opening a stream with an unspecified value, the application must query for the
     * actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * As the corresponding channel count of provided channel mask here may be different
     * from the channel count used in {AAudioStreamBuilder_setChannelCount} or
     * {AAudioStreamBuilder_setSamplesPerFrame}, the last called function will be
     * respected if this function and {AAudioStreamBuilder_setChannelCount} or
     * {AAudioStreamBuilder_setSamplesPerFrame} are called.
     * <p>
     * Available since API level 32.
     *
     * @param builder     reference provided by AAudio_createStreamBuilder()
     * @param channelMask Audio channel mask desired.
     */
    public void AAudioStreamBuilder_setChannelMask(MemorySegment builder, AAudioChannelMask channelMask) throws Throwable {
        AAudioStreamBuilder_setChannelMask.invokeExact(builder, channelMask.getValue());
    }

    private final static MethodHandle AAudioStreamBuilder_setChannelMask = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setChannelMask"),
            FunctionDescriptor.ofVoid(C_POINTER, C_INT)
    );

    // ============================================================
    // Stream Control
    // ============================================================

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFormat"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioFormat AAudioStreamGetFormat(MemorySegment stream) throws Throwable {
        return AAudioFormat.fromValue((int) AAudioStream_getFormat.invokeExact(stream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->
    private final static MethodHandle AAudioStream_getChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getChannelCount"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetChannelCount(MemorySegment stream) throws Throwable {
        return (int) AAudioStream_getChannelCount.invokeExact(stream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getHardwareChannelCount"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetHardwareChannelCount(MemorySegment stream) throws Throwable {
        return (int) AAudioStream_getHardwareChannelCount.invokeExact(stream);
    }


    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDeviceId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getDeviceId"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetDeviceId(MemorySegment stream) throws Throwable {
        return (int) AAudioStream_getDeviceId.invokeExact(stream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getDirection = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getDirection"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioAudioDirection AAudioStreamGetDirection(MemorySegment stream) throws Throwable {
        return AAudioAudioDirection.fromValue((int) AAudioStream_getDirection.invokeExact(stream));
    }
    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getPerformanceMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getPerformanceMode"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioPerformanceMode AAudioStreamGetPerformanceMode(MemorySegment stream) throws Throwable {
        return AAudioPerformanceMode.fromValue((int) AAudioStream_getPerformanceMode.invokeExact(stream));
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSampleRate"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetSampleRate(MemorySegment stream) throws Throwable {
        return (int) AAudioStream_getSampleRate.invokeExact(stream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getHardwareSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getHardwareSampleRate"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static int AAudioStreamGetHardwareSampleRate(MemorySegment stream) throws Throwable {
        return (int) AAudioStream_getHardwareSampleRate.invokeExact(stream);
    }

    // <------------------------------------------------------------->

    // <------------------------------------------------------------->

    private final static MethodHandle AAudioStream_getState = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getState"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioStreamState AAudioStreamGetState(MemorySegment stream) throws Throwable {
        return AAudioStreamState.fromValue((int) AAudioStream_getState.invokeExact(stream));
    }

    // ============================================================
    // Stream Control
    // ============================================================


    private final static MethodHandle AAudioStream_requestStart = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_requestStart"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    public static AAudioResult AAudioStreamRequestStart(MemorySegment stream) throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_requestStart.invokeExact(stream));
    }
}
