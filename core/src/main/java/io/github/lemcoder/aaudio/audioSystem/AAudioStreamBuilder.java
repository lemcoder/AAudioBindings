package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.*;
import io.github.lemcoder.aaudio.api.AAudioErrorCallback;
import io.github.lemcoder.aaudio.api.AAudioStreamDataCallback;
import io.github.lemcoder.aaudio.model.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.*;

public class AAudioStreamBuilder implements AutoCloseable {
    private final Arena lifetimeArena;
    private final MemorySegment nativeInstance;

    private static final MethodHandle _createStreamBuilder;
    private static final MethodHandle _setDeviceId;
    private static final MethodHandle _setPackageName;
    private static final MethodHandle _setAttributionTag;
    private static final MethodHandle _setSampleRate;
    private static final MethodHandle _setChannelCount;
    private static final MethodHandle _setSamplesPerFrame;
    private static final MethodHandle _setFormat;
    private static final MethodHandle _setSharingMode;
    private static final MethodHandle _setDirection;
    private static final MethodHandle _setBufferCapacityInFrames;
    private static final MethodHandle _setPerformanceMode;
    private static final MethodHandle _setUsage;
    private static final MethodHandle _setContentType;
    private static final MethodHandle _setSpatializationBehavior;
    private static final MethodHandle _setIsContentSpatialized;
    private static final MethodHandle _setInputPreset;
    private static final MethodHandle _setAllowedCapturePolicy;
    private static final MethodHandle _setSessionId;
    private static final MethodHandle _setPrivacySensitive;
    private static final MethodHandle _setDataCallback;
    private static final MethodHandle _setFramesPerDataCallback;
    private static final MethodHandle _setErrorCallback;
    private static final MethodHandle _openStream;
    private static final MethodHandle _delete;
    private static final MethodHandle _setChannelMask;

    protected AAudioStreamBuilder(Arena lifetime) throws Exception {
        this.lifetimeArena = lifetime;
        this.nativeInstance = createStreamBuilder();
    }

    // ============================================================
    // StreamBuilder
    // ============================================================

    private MemorySegment createStreamBuilder() throws Exception {
        try {
            MemorySegment ptr = lifetimeArena.allocate(C_POINTER);
            int result = (int) _createStreamBuilder.invokeExact(ptr);
            if (result != AAudioResult.OK.getValue()) {
                throw new RuntimeException("Failed to create AAudio stream builder: " + result);
            }

            return ptr.get(ValueLayout.ADDRESS, 0);
        } catch (Throwable t) {
            close();
            throw new RuntimeException("Error creating AAudioStreamBuilder", t);
        }
    }

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
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED},
     * in which case the primary device will be used.
     * <p>
     * Available since API level 26.
     *
     * @param deviceId device identifier or {AAUDIO_UNSPECIFIED}
     */
    public void setDeviceId(int deviceId) throws Throwable {
        _setDeviceId.invokeExact(nativeInstance, deviceId);
    }

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
     * @param packageName packageName of the calling app.
     */
    public void setPackageName(String packageName) throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            MemorySegment pPackageName = scope.allocateFrom(packageName);

            _setPackageName.invokeExact(nativeInstance, pPackageName);
        }
    }

    /**
     * Declare the attribution tag of the context creating the stream.
     * <p>
     * This is usually {@code Context#getAttributionTag()}.
     * <p>
     * The default, if you do not call this function, is null.
     * <p>
     * Available since API level 31.
     *
     * @param attributionTag attributionTag of the calling context.
     */
    public void setAttributionTag(String attributionTag) throws Throwable {
        try (Arena scope = Arena.ofConfined()) {
            MemorySegment pAttributionTag = scope.allocateFrom(attributionTag);

            _setAttributionTag.invokeExact(nativeInstance, pAttributionTag);
        }
    }

    /**
     * Request a sample rate in Hertz.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED}.
     * An optimal value will then be chosen when the stream is opened.
     * After opening a stream with an unspecified value, the application must
     * query for the actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * Available since API level 26.
     *
     * @param sampleRate frames per second. Common rates include 44100 and 48000 Hz.
     */
    public void setSampleRate(int sampleRate) throws Throwable {
        _setSampleRate.invokeExact(nativeInstance, sampleRate);
    }

    /**
     * Request a number of channels for the stream.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED}.
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
     * @param channelCount Number of channels desired.
     */
    public void setChannelCount(int channelCount) throws Throwable {
        _setChannelCount.invokeExact(nativeInstance, channelCount);
    }

    /**
     * Identical to AAudioStreamBuilder_setChannelCount().
     * Available since API level 26.
     *
     * @param samplesPerFrame Number of samples in a frame.
     */
    @Deprecated()
    public void setSamplesPerFrame(int samplesPerFrame) throws Throwable {
        _setSamplesPerFrame.invokeExact(nativeInstance, samplesPerFrame);
    }

    /**
     * Request a sample data format, for example {AAudioFormat.AAUDIO_FORMAT_PCM_I16}.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED}.
     * An optimal value will then be chosen when the stream is opened.
     * After opening a stream with an unspecified value, the application must
     * query for the actual value, which may vary by device.
     * <p>
     * If an exact value is specified then an opened stream will use that value.
     * If a stream cannot be opened with the specified value then the open will fail.
     * <p>
     * Available since API level 26.
     *
     * @param format common formats are {AAudioFormat.AAUDIO_FORMAT_PCM_FLOAT} and {AAudioFormat.AAUDIO_FORMAT_PCM_I16}.
     */
    public void setFormat(AAudioFormat format) throws Throwable {
        _setFormat.invokeExact(nativeInstance, format.getValue());
    }

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
     * @param sharingMode {AAUDIO_SHARING_MODE_SHARED} or {AAUDIO_SHARING_MODE_EXCLUSIVE}
     */
    public void setSharingMode(AAudioSharingMode sharingMode) throws Throwable {
        _setSharingMode.invokeExact(nativeInstance, sharingMode.getValue());
    }

    /**
     * Request the direction for a stream.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_DIRECTION_OUTPUT}.
     * <p>
     * Available since API level 26.
     *
     * @param direction {AAUDIO_DIRECTION_OUTPUT} or {AAUDIO_DIRECTION_INPUT}
     */
    public void setDirection(AAudioAudioDirection direction) throws Throwable {
        _setDirection.invokeExact(nativeInstance, direction.getValue());
    }

    /**
     * Set the requested buffer capacity in frames.
     * The final AAudioStream capacity may differ, but will probably be at least this big.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED}.
     * <p>
     * Available since API level 26.
     *
     * @param numFrames the desired buffer capacity in frames or {AAUDIO_UNSPECIFIED}
     */
    public void setBufferCapacityInFrames(int numFrames) throws Throwable {
        _setBufferCapacityInFrames.invokeExact(nativeInstance, numFrames);
    }

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
     * @param mode the desired performance mode, eg. {AAUDIO_PERFORMANCE_MODE_LOW_LATENCY}
     */
    public void setPerformanceMode(AAudioPerformanceMode mode) throws Throwable {
        _setPerformanceMode.invokeExact(nativeInstance, mode.getValue());
    }

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
     * @param usage the desired usage, eg. {AAUDIO_USAGE_GAME}
     */
    public void setUsage(AAudioUsage usage) throws Throwable {
        _setUsage.invokeExact(nativeInstance, usage.getValue());
    }

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
     * @param contentType the type of audio data, eg. {AAUDIO_CONTENT_TYPE_SPEECH}
     */
    public void setContentType(AAudioContentType contentType) throws Throwable {
        _setContentType.invokeExact(nativeInstance, contentType.getValue());
    }

    /**
     * Sets the behavior affecting whether spatialization will be used.
     * <p>
     * The AAudio system will use this information to select whether the stream will go
     * through a spatializer effect or not when the effect is supported and enabled.
     * <p>
     * Available since API level 32.
     *
     * @param spatializationBehavior the desired behavior with regards to spatialization, eg.{AAUDIO_SPATIALIZATION_BEHAVIOR_AUTO}
     */
    public void setSpatializationBehavior(AAudioSpatializationBehavior spatializationBehavior) throws Throwable {
        _setSpatializationBehavior.invokeExact(nativeInstance, spatializationBehavior.getValue());
    }

    /**
     * Specifies whether the audio data of this output stream has already been processed for
     * spatialization.
     * <p>
     * If the stream has been processed for spatialization, setting this to true will prevent
     * issues such as double-processing on platforms that will spatialize audio data.
     * <p>
     * Available since API level 32.
     *
     * @param isSpatialized true if the content is already processed for binaural or transaural spatial
     *                      rendering, false otherwise.
     */
    public void setIsContentSpatialized(boolean isSpatialized) throws Throwable {
        _setIsContentSpatialized.invokeExact(nativeInstance, isSpatialized);
    }

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
     * @param inputPreset the desired configuration for recording
     */
    public void setInputPreset(AAudioInputPreset inputPreset) throws Throwable {
        _setInputPreset.invokeExact(nativeInstance, inputPreset.getValue());
    }

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
     * @param capturePolicy the desired level of opt-out from being captured.
     */
    public void setAllowedCapturePolicy(AAudioAllowedCapturePolicy capturePolicy) throws Throwable {
        _setAllowedCapturePolicy.invokeExact(nativeInstance, capturePolicy.getValue());
    }

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
     * @param sessionId an allocated sessionID or {AAUDIO_SESSION_ID_ALLOCATE}
     */
    public void setSessionId(AAudioSessionId sessionId) throws Throwable {
        _setSessionId.invokeExact(nativeInstance, sessionId.getValue());
    }

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
     * @param privacySensitive true if capture from this stream must be marked as privacy sensitive,
     *                         false otherwise.
     */
    public void setPrivacySensitive(boolean privacySensitive) throws Throwable {
        _setPrivacySensitive.invokeExact(nativeInstance, privacySensitive);
    }

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
     * @param callback a function that will process audio data.
     */
    public void setDataCallback(AAudioStreamDataCallback callback) throws Throwable {
        // Create a stub as a native symbol to be passed into native function.
        AAudioStreamDataCallbackInternal callbackInternal = (stream, userData, audioData, numFrames) -> {
            // TODO: All memory segments will be zero size because there is nowhere to get this information from. It is the same as a pointer in C
            //  The size will need to be specified here via MemorySegment.reinterpret (It should be known as in C)

            if (audioData == null || audioData.equals(MemorySegment.NULL) || audioData.byteSize() == 0) {
                System.err.println("Error: Invalid or empty audioData buffer!");
                return AAudioCallbackResult.CONTINUE.getValue(); // Stop if buffer is unusable
            }

            byte[] data = callback.onOutputReady(numFrames);
            callback.onInputReady(audioData.asByteBuffer().array(), numFrames); // FIXME

            MemorySegment.copy(data, 0, audioData, ValueLayout.JAVA_BYTE, 0, data.length);

            return AAudioCallbackResult.CONTINUE.getValue();
        };

        MemorySegment pCallback = createDataCallbackPtr(callbackInternal);

        _setDataCallback.invokeExact(nativeInstance, pCallback, MemorySegment.NULL);
    }

    private MemorySegment createDataCallbackPtr(AAudioStreamDataCallbackInternal callback) throws Throwable {
        // TODO This is statically known data, no need to create and look for it every call
        FunctionDescriptor callbackDescriptor = FunctionDescriptor.of(
                ValueLayout.JAVA_INT,  // aaudio_data_callback_result_t is int
                C_POINTER,   // AAudioStream* (non-null)
                C_POINTER,  // void* userData (nullable)
                C_POINTER,   // void* audioData (non-null)
                ValueLayout.JAVA_INT   // int32_t numFrames
        );

        MethodHandles.Lookup lookup = MethodHandles.lookup();

        // TODO: All classes used in reflection must be added to keep rules for r8 - otherwise they may be changed during optimization.
        MethodHandle mh = lookup.findVirtual(
                AAudioStreamDataCallbackInternal.class,
                "onData",
                MethodType.methodType(int.class, MemorySegment.class, MemorySegment.class, MemorySegment.class, int.class)
        );

        return LINKER.upcallStub(mh.bindTo(callback), callbackDescriptor, lifetimeArena);

    }

    /**
     * Set the requested data callback buffer size in frames.
     * See {AAudioStream_dataCallback}.
     * <p>
     * The default, if you do not call this function, is {AAUDIO_UNSPECIFIED}.
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
     * @param numFrames the desired buffer size in frames or {AAUDIO_UNSPECIFIED}
     */
    public void setFramesPerDataCallback(int numFrames) throws Throwable {
        _setFramesPerDataCallback.invokeExact(nativeInstance, numFrames);
    }

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
     * @param callback pointer to a function that will be called if an error occurs.
     */
    public void setErrorCallback(Arena lifetime, AAudioErrorCallback callback) throws Throwable {
        AAudioErrorCallbackInternal callbackInternal = (stream, userData, error) -> {
            callback.onError(AAudioResult.fromValue(error));
        };

        MemorySegment pCallback = createErrorCallbackPtr(callbackInternal);

        _setErrorCallback.invokeExact(nativeInstance, pCallback, MemorySegment.NULL);
    }

    private MemorySegment createErrorCallbackPtr(AAudioErrorCallbackInternal callback) {
        // TODO This is statically known data, no need to create and look for it every call
        FunctionDescriptor descriptor = FunctionDescriptor.ofVoid(
                C_POINTER,   // AAudioStream* (non-null)
                C_POINTER,   // void* userData (nullable)
                C_INT        // error (non-null)
        );
        MethodHandle handle;

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

        return LINKER.upcallStub(handle.bindTo(callback), descriptor, lifetimeArena);
    }

    /**
     * Open a stream based on the options in the StreamBuilder.
     * <p>
     * AAudioStream_close() must be called when finished with the stream to recover
     * the memory and to free the associated resources.
     * <p>
     * Available since API level 26.
     *
     * @return AAudioStream
     * @throws RuntimeException if the stream could not be opened
     */
    public AAudioStream openStream() throws Throwable {
        AAudioStream s = new AAudioStream();
        s.open(this);
        return s;
    }

    protected void openStream(MemorySegment streamPtr) throws Throwable {
        try {
            int result = (int) _openStream.invokeExact(nativeInstance, streamPtr);
            if (result != AAudioResult.OK.getValue()) {
                throw new RuntimeException("Failed to open stream: " + result);
            }
        } catch (Throwable t) {
            throw new RuntimeException("Error opening AAudioStream", t);
        }
    }

    /**
     * Delete the resources associated with the StreamBuilder.
     * <p>
     * Available since API level 26.
     *
     * @return {AAUDIO_OK} or a negative error.
     */
    public int delete() throws Throwable {
        return (int) _delete.invokeExact(nativeInstance);
    }

    /**
     * Set audio channel mask for the stream.
     * <p>
     * The default, if you do not call this function, is {0}.
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
     * @param channelMask Audio channel mask desired.
     */
    public void setChannelMask(AAudioChannelMask channelMask) throws Throwable {
        _setChannelMask.invokeExact(nativeInstance, channelMask.getValue());
    }

    @Override
    public void close() throws Exception {
        try {
            lifetimeArena.close();
        } catch (Throwable ignored) {

        }
    }

    static {
        _createStreamBuilder = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudio_createStreamBuilder"), FunctionDescriptor.of(NativeHelper.C_INT, NativeHelper.C_POINTER));
        _setDeviceId = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDeviceId"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setPackageName = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPackageName"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_POINTER));
        _setAttributionTag = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setAttributionTag"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_POINTER));
        _setSampleRate = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSampleRate"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setChannelCount = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setChannelCount"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setSamplesPerFrame = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSamplesPerFrame"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setFormat = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setFormat"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setSharingMode = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSharingMode"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setDirection = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDirection"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setBufferCapacityInFrames = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setBufferCapacityInFrames"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setPerformanceMode = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPerformanceMode"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setUsage = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setUsage"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setContentType = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setContentType"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setSpatializationBehavior = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSpatializationBehavior"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setIsContentSpatialized = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setIsContentSpatialized"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_BOOL));
        _setInputPreset = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setInputPreset"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setAllowedCapturePolicy = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setAllowedCapturePolicy"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setSessionId = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setSessionId"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setPrivacySensitive = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setPrivacySensitive"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_BOOL));
        _setDataCallback = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setDataCallback"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_POINTER, NativeHelper.C_POINTER));
        _setFramesPerDataCallback = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setFramesPerDataCallback"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
        _setErrorCallback = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setErrorCallback"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_POINTER, NativeHelper.C_POINTER));
        _openStream = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_openStream"), FunctionDescriptor.of(NativeHelper.C_INT, NativeHelper.C_POINTER, NativeHelper.C_POINTER));
        _delete = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_delete"), FunctionDescriptor.of(NativeHelper.C_INT, NativeHelper.C_POINTER));
        _setChannelMask = NativeHelper.LINKER.downcallHandle(SymbolLookup.loaderLookup().findOrThrow("AAudioStreamBuilder_setChannelMask"), FunctionDescriptor.ofVoid(NativeHelper.C_POINTER, NativeHelper.C_INT));
    }
}
