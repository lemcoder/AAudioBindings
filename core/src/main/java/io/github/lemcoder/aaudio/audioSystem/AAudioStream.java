package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.*;
import io.github.lemcoder.aaudio.model.*;

import java.lang.invoke.MethodHandle;

import static io.github.lemcoder.aaudio.audioSystem.NativeHelper.*;

public class AAudioStream implements AutoCloseable {
    protected final Arena lifetimeArena = Arena.ofShared();
    private MemorySegment nativeInstance;

    protected AAudioStream() {

    }

    protected void open(AAudioStreamBuilder builder) throws Throwable {
        try {
            MemorySegment ptr = lifetimeArena.allocate(C_POINTER);
            builder.openStream(ptr);
            this.nativeInstance = ptr.get(ValueLayout.ADDRESS, 0);
        } catch (Throwable t) {
            close();
            throw t;
        }
    }

    /**
     * Free the audio resources associated with a stream created by
     * AAudioStreamBuilder_openStream().
     * AAudioStream_close() should be called at some point after calling
     * this function.
     * <p>
     * After this call, the stream will be in {AAUDIO_STREAM_STATE_CLOSING}
     * <p>
     * This function is useful if you want to release the audio resources immediately,
     * but still allow queries to the stream to occur from other threads. This often
     * happens if you are monitoring stream progress from a UI thread.
     * <p>
     * NOTE: This function is only fully implemented for MMAP streams,
     * which are low latency streams supported by some devices.
     * On other "Legacy" streams some audio resources will still be in use
     * and some callbacks may still be in process after this call.
     * <p>
     * Available since API level 30.
     *
     * @return {AAUDIO_OK} or a negative error.
     */
    // called when the stream is closed
    private AAudioResult release() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_release.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_release = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_release"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Delete the internal data structures associated with the stream created
     * by AAudioStreamBuilder_openStream().
     * <p>
     * If AAudioStream_release() has not been called then it will be called automatically.
     * <p>
     * Available since API level 26.
     *
     * @return {AAUDIO_OK} or a negative error.
     */
    private AAudioResult closeStream() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_close.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_close = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_close"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Asynchronously request to start playing the stream. For output streams, one should
     * write to the stream to fill the buffer before starting.
     * Otherwise it will underflow.
     * After this call the state will be in {AAUDIO_STREAM_STATE_STARTING} or
     * {AAUDIO_STREAM_STATE_STARTED}.
     * <p>
     * Available since API level 26.
     *
     * @return {AAUDIO_OK} or a negative error.
     */
    public AAudioResult requestStart() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_requestStart.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_requestStart = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_requestStart"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Asynchronous request for the stream to pause.
     * Pausing a stream will freeze the data flow but not flush any buffers.
     * Use requestStart() to resume playback after a pause.
     * <p>
     * This will return {AAUDIO_ERROR_UNIMPLEMENTED} for input streams.
     * For input streams use requestStop().
     * <p>
     * Available since API level 26.
     *
     * @return {AAudioResult#AAUDIO_OK} or a negative error.
     */
    public AAudioResult requestPause() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_requestPause.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_requestPause = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_requestPause"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Asynchronous request for the stream to flush.
     * Flushing will discard any pending data.
     * This call only works if the stream is OPEN, PAUSED, STOPPED, or FLUSHED.
     * Calling this function when in other states,
     * or calling from an AAudio callback function,
     * will have no effect and an error will be returned.
     * Frame counters are not reset by a flush. They may be advanced.
     * After this call the state will be in {AAUDIO_STREAM_STATE_FLUSHING} or
     * {AAUDIO_STREAM_STATE_FLUSHED}.
     * <p>
     * This will return {AAUDIO_ERROR_UNIMPLEMENTED} for input streams.
     * <p>
     * Available since API level 26.
     *
     * @return {AAUDIO_OK} or a negative error.
     */
    public AAudioResult requestFlush() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_requestFlush.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_requestFlush = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_requestFlush"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Asynchronous request for the stream to stop.
     * The stream will stop after all of the data currently buffered has been played.
     * After this call the state will be in {AAUDIO_STREAM_STATE_STOPPING} or
     * {AAUDIO_STREAM_STATE_STOPPED}.
     * <p>
     * Available since API level 26.
     *
     * @return {AAudioResult#AAUDIO_OK} or a negative error.
     */
    public AAudioResult requestStop() throws Throwable {
        return AAudioResult.fromValue((int) AAudioStream_requestStop.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_requestStop = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_requestStop"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the current state of the client, e.g., {AAUDIO_STREAM_STATE_PAUSING}.
     * <p>
     * This function will immediately return the state without updating the state.
     * If you want to update the client state based on the server state, then
     * call AAudioStream_waitForStateChange() with currentState
     * set to {AAUDIO_STREAM_STATE_UNKNOWN} and a zero timeout.
     * <p>
     * Available since API level 26.
     *
     * @return The current state of the stream.
     */
    public AAudioStreamState getState() throws Throwable {
        return AAudioStreamState.fromValue((int) AAudioStream_getState.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getState = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getState"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Wait until the current state no longer matches the input state.
     * <p>
     * This will update the current client state.
     * <p>
     * Available since API level 26.
     *
     * @param inputState         The state we want to avoid.
     * @param timeoutNanoseconds Maximum number of nanoseconds to wait for completion.
     * @return The new state of the stream.
     */
    public AAudioStreamState waitForStateChange(int inputState, long timeoutNanoseconds) throws Throwable {
        MemorySegment nextState = MemorySegment.ofAddress(0);
        AAudioStream_waitForStateChange.invokeExact(nativeInstance, inputState, nextState, timeoutNanoseconds);
        return AAudioStreamState.fromValue(nextState.get(C_INT, 0));
    }

    private final static MethodHandle AAudioStream_waitForStateChange = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_waitForStateChange"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER, C_LONG)
    );

    // ============================================================
    // Stream I/O
    // ============================================================

    /**
     * Read data from the stream.
     * <p>
     * The call will wait until the read is complete or until it runs out of time.
     * If timeoutNanos is zero then this call will not wait.
     * <p>
     * Note that timeoutNanoseconds is a relative duration in wall clock time.
     * Time will not stop if the thread is asleep.
     * So it will be implemented using CLOCK_BOOTTIME.
     * <p>
     * This call is "strong non-blocking" unless it has to wait for data.
     * <p>
     * If the call times out then zero or a partial frame count will be returned.
     * <p>
     * Available since API level 26.
     *
     * @param buffer The address of the first sample.
     * @param numFrames Number of frames to read. Only complete frames will be written.
     * @param timeoutNanoseconds Maximum number of nanoseconds to wait for completion.
     * @return The number of frames actually read or a negative error.
     */
//    public int read(MemorySegment buffer, int numFrames, long timeoutNanoseconds) throws Throwable {
//        // TODO
//        return 0;
//    }

    private final static MethodHandle AAudioStream_read = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_read"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER, C_INT, C_LONG)
    );

    /**
     * Write data to the stream.
     * <p>
     * The call will wait until the write is complete or until it runs out of time.
     * If timeoutNanos is zero then this call will not wait.
     * <p>
     * Note that timeoutNanoseconds is a relative duration in wall clock time.
     * Time will not stop if the thread is asleep.
     * So it will be implemented using CLOCK_BOOTTIME.
     * <p>
     * This call is "strong non-blocking" unless it has to wait for room in the buffer.
     * <p>
     * If the call times out then zero or a partial frame count will be returned.
     * <p>
     * Available since API level 26.
     *
     * @param buffer The address of the first sample.
     * @param numFrames Number of frames to write. Only complete frames will be written.
     * @param timeoutNanoseconds Maximum number of nanoseconds to wait for completion.
     * @return The number of frames actually written or a negative error.
     */
//    TODO
//    public int write(MemorySegment buffer, int numFrames, long timeoutNanoseconds) throws Throwable {
//    }

    private final static MethodHandle AAudioStream_write = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_write"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_POINTER, C_INT, C_LONG)
    );

    // ============================================================
    // Stream - queries
    // ============================================================

    /**
     * This can be used to adjust the latency of the buffer by changing
     * the threshold where blocking will occur.
     * By combining this with AAudioStream_getXRunCount(), the latency can be tuned
     * at run-time for each device.
     * <p>
     * This cannot be set higher than AAudioStream_getBufferCapacityInFrames().
     * <p>
     * Note that you will probably not get the exact size you request.
     * You can check the return value or call AAudioStream_getBufferSizeInFrames()
     * to see what the actual final size is.
     * <p>
     * Available since API level 26.
     *
     * @param numFrames requested number of frames that can be filled without blocking
     * @return actual buffer size in frames or a negative error
     */
    public int setBufferSizeInFrames(int numFrames) throws Throwable {
        return (int) AAudioStream_setBufferSizeInFrames.invokeExact(nativeInstance, numFrames);
        // TODO throw ex if error code
    }

    private final static MethodHandle AAudioStream_setBufferSizeInFrames = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_setBufferSizeInFrames"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_INT)
    );

    /**
     * Query the maximum number of frames that can be filled without blocking.
     * <p>
     * Available since API level 26.
     *
     * @return buffer size in frames.
     */
    public int getBufferSizeInFrames() throws Throwable {
        return (int) AAudioStream_getBufferSizeInFrames.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getBufferSizeInFrames = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getBufferSizeInFrames"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the number of frames that the application should read or write at
     * one time for optimal performance. It is OK if an application writes
     * a different number of frames. But the buffer size may need to be larger
     * in order to avoid underruns or overruns.
     * <p>
     * Note that this may or may not match the actual device burst size.
     * For some endpoints, the burst size can vary dynamically.
     * But these tend to be devices with high latency.
     * <p>
     * Available since API level 26.
     *
     * @return burst size
     */
    public int getFramesPerBurst() throws Throwable {
        return (int) AAudioStream_getFramesPerBurst.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getFramesPerBurst = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFramesPerBurst"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query maximum buffer capacity in frames.
     * <p>
     * Available since API level 26.
     *
     * @return buffer capacity in frames
     */
    public int getBufferCapacityInFrames() throws Throwable {
        return (int) AAudioStream_getBufferCapacityInFrames.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getBufferCapacityInFrames = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getBufferCapacityInFrames"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the size of the buffer that will be passed to the dataProc callback
     * in the numFrames parameter.
     * <p>
     * This call can be used if the application needs to know the value of numFrames before
     * the stream is started. This is not normally necessary.
     * <p>
     * If a specific size was requested by calling
     * AAudioStreamBuilder_setFramesPerDataCallback() then this will be the same size.
     * <p>
     * If AAudioStreamBuilder_setFramesPerDataCallback() was not called then this will
     * return the size chosen by AAudio, or {AAUDIO_UNSPECIFIED}.
     * <p>
     * {AAUDIO_UNSPECIFIED} indicates that the callback buffer size for this stream
     * may vary from one dataProc callback to the next.
     * <p>
     * Available since API level 26.
     *
     * @return callback buffer size in frames or {AAUDIO_UNSPECIFIED}
     */
    public int getFramesPerDataCallback() throws Throwable {
        return (int) AAudioStream_getFramesPerDataCallback.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getFramesPerDataCallback = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFramesPerDataCallback"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the number of underruns or overruns that have occurred since the stream was created.
     * <p>
     * An XRun is an Underrun or an Overrun.
     * During playing, an underrun will occur if the stream is not written in time
     * and the system runs out of valid data.
     * During recording, an overrun will occur if the stream is not read in time
     * and there is no place to put the incoming data so it is discarded.
     * <p>
     * An underrun or overrun can cause an audible "pop" or "glitch".
     * <p>
     * Note that some INPUT devices may not support this function.
     * In that case a 0 will always be returned.
     * <p>
     * Available since API level 26.
     *
     * @return the underrun or overrun count
     */
    public int getXRunCount() throws Throwable {
        return (int) AAudioStream_getXRunCount.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getXRunCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getXRunCount"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the actual sample rate of the stream.
     * <p>
     * Available since API level 26.
     *
     * @return actual sample rate of the stream
     */
    public int getSampleRate() throws Throwable {
        return (int) AAudioStream_getSampleRate.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSampleRate"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * There may be sample rate conversions in the Audio framework.
     * The sample rate set in the stream builder may not be the actual sample rate used in the hardware.
     * <p>
     * This returns the sample rate used by the hardware in Hertz.
     * <p>
     * If AAudioStreamBuilder_openStream() returned AAUDIO_OK, the result should always be valid.
     * <p>
     * Available since API level 34.
     *
     * @return actual sample rate of the underlying hardware
     */
    public int getHardwareSampleRate() throws Throwable {
        return (int) AAudioStream_getHardwareSampleRate.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getHardwareSampleRate = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getHardwareSampleRate"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * A stream has one or more channels of data.
     * A frame will contain one sample for each channel.
     * <p>
     * Available since API level 26.
     *
     * @return actual number of channels of the stream
     */
    public int getChannelCount() throws Throwable {
        return (int) AAudioStream_getChannelCount.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getChannelCount"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the actual number of channels used by the hardware.
     * <p>
     * Available since API level 34.
     *
     * @return actual number of channels of the underlying hardware
     */
    public int getHardwareChannelCount() throws Throwable {
        return (int) AAudioStream_getHardwareChannelCount.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getHardwareChannelCount = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getHardwareChannelCount"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );


    /**
     * Identical to AAudioStream_getChannelCount().
     * <p>
     * Available since API level 26.
     *
     * @return actual number of samples per frame
     */
    public int getSamplesPerFrame() throws Throwable {
        return (int) AAudioStream_getSamplesPerFrame.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getSamplesPerFrame = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSamplesPerFrame"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Query the actual device ID of the stream.
     * <p>
     * Available since API level 26.
     *
     * @return actual device ID
     */
    public int getDeviceId() throws Throwable {
        return (int) AAudioStream_getDeviceId.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getDeviceId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getDeviceId"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Available since API level 26.
     *
     * @return actual data format of the stream
     */
    public AAudioFormat getFormat() throws Throwable {
        return AAudioFormat.fromValue((int) AAudioStream_getFormat.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFormat"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * There may be data format conversions in the Audio framework.
     * The data format set in the stream builder may not be actual format used in the hardware.
     * <p>
     * This returns the audio format used by the hardware.
     * <p>
     * If AAudioStreamBuilder_openStream() returned AAUDIO_OK, this should always return an
     * aaudio_format_t.
     * <p>
     * AUDIO_FORMAT_PCM_8_24_BIT is currently not supported in AAudio, but the hardware may use it.
     * If AUDIO_FORMAT_PCM_8_24_BIT is used by the hardware, return AAUDIO_FORMAT_PCM_I24_PACKED.
     * <p>
     * If any other format used by the hardware is not supported by AAudio, this will return
     * AAUDIO_FORMAT_INVALID.
     * <p>
     * Available since API level 34.
     *
     * @return actual data format of the underlying hardware.
     */
    public AAudioFormat getHardwareFormat() throws Throwable {
        return AAudioFormat.fromValue((int) AAudioStream_getHardwareFormat.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getHardwareFormat = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getHardwareFormat"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Provide actual sharing mode.
     * <p>
     * Available since API level 26.
     *
     * @return actual sharing mode
     */
    public AAudioSharingMode getSharingMode() throws Throwable {
        return AAudioSharingMode.fromValue((int) AAudioStream_getSharingMode.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getSharingMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSharingMode"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Get the performance mode used by the stream.
     * <p>
     * Available since API level 26.
     *
     * @return performance mode
     */
    public AAudioPerformanceMode getPerformanceMode() throws Throwable {
        return AAudioPerformanceMode.fromValue((int) AAudioStream_getPerformanceMode.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getPerformanceMode = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getPerformanceMode"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Available since API level 26.
     *
     * @return direction
     */
    public AAudioAudioDirection getDirection() throws Throwable {
        return AAudioAudioDirection.fromValue((int) AAudioStream_getDirection.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getDirection = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getDirection"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Passes back the number of frames that have been written since the stream was created.
     * For an output stream, this will be advanced by the application calling write()
     * or by a data callback.
     * For an input stream, this will be advanced by the endpoint.
     * <p>
     * The frame position is monotonically increasing.
     * <p>
     * Available since API level 26.
     *
     * @return frames written
     */
    public long getFramesWritten() throws Throwable {
        return (long) AAudioStream_getFramesWritten.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getFramesWritten = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFramesWritten"),
            FunctionDescriptor.of(C_LONG, C_POINTER)
    );


    /**
     * Passes back the number of frames that have been read since the stream was created.
     * For an output stream, this will be advanced by the endpoint.
     * For an input stream, this will be advanced by the application calling read()
     * or by a data callback.
     * <p>
     * The frame position is monotonically increasing.
     * <p>
     * Available since API level 26.
     *
     * @return frames read
     */
    public long getFramesRead() throws Throwable {
        return (long) AAudioStream_getFramesRead.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_getFramesRead = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getFramesRead"),
            FunctionDescriptor.of(C_LONG, C_POINTER)
    );

    /**
     * Passes back the session ID associated with this stream.
     * <p>
     * The session ID can be used to associate a stream with effects processors.
     * The effects are controlled using the Android AudioEffect Java API.
     * <p>
     * If AAudioStreamBuilder_setSessionId() was
     * called with {AAUDIO_SESSION_ID_ALLOCATE}
     * then a new session ID should be allocated once when the stream is opened.
     * <p>
     * If AAudioStreamBuilder_setSessionId() was called with a previously allocated
     * session ID then that value should be returned.
     * <p>
     * If AAudioStreamBuilder_setSessionId() was not called then this function should
     * return {AAUDIO_SESSION_ID_NONE}.
     * <p>
     * The sessionID for a stream should not change once the stream has been opened.
     * <p>
     * Available since API level 28.
     *
     * @return session ID or {AAUDIO_SESSION_ID_NONE}
     */
    public AAudioSessionId getSessionId() throws Throwable {
        return AAudioSessionId.fromValue((int) AAudioStream_getSessionId.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getSessionId = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSessionId"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Passes back the time at which a particular frame was presented.
     * This can be used to synchronize audio with video or MIDI.
     * It can also be used to align a recorded stream with a playback stream.
     * <p>
     * Timestamps are only valid when the stream is in {AAUDIO_STREAM_STATE_STARTED}.
     * {AAUDIO_ERROR_INVALID_STATE} will be returned if the stream is not started.
     * Note that because requestStart() is asynchronous, timestamps will not be valid until
     * a short time after calling requestStart().
     * So {AAUDIO_ERROR_INVALID_STATE} should not be considered a fatal error.
     * Just try calling again later.
     * <p>
     * If an error occurs, then the position and time will not be modified.
     * <p>
     * The position and time passed back are monotonically increasing.
     * <p>
     * Available since API level 26.
     *
     * @param clockid         CLOCK_MONOTONIC or CLOCK_BOOTTIME
     * @param framePosition   pointer to a variable to receive the position
     * @param timeNanoseconds pointer to a variable to receive the time
     * @return {AAudioResult#AAUDIO_OK} or a negative error
     */
    // TODO
    public AAudioResult getTimestamp(int clockid, MemorySegment framePosition, MemorySegment timeNanoseconds) throws Throwable {
//        return AAudioResult.fromValue((int) AAudioStream_getTimestamp.invokeExact(nativeInstance, clockid, framePosition, timeNanoseconds));
        return AAudioResult.fromValue(0);
    }

    private final static MethodHandle AAudioStream_getTimestamp = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getTimestamp"),
            FunctionDescriptor.of(C_INT, C_POINTER, C_INT, C_POINTER, C_POINTER)
    );

    /**
     * Return the use case for the stream.
     * <p>
     * Available since API level 28.
     *
     * @return use case for the stream
     */
    public AAudioUsage getUsage() throws Throwable {
        return AAudioUsage.fromValue((int) AAudioStream_getUsage.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getUsage = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getUsage"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Return the content type for the stream.
     * <p>
     * Available since API level 28.
     *
     * @return content type, for example {AAUDIO_CONTENT_TYPE_MUSIC}
     */
    public AAudioContentType getContentType() throws Throwable {
        return AAudioContentType.fromValue((int) AAudioStream_getContentType.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getContentType = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getContentType"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Return the spatialization behavior for the stream.
     * <p>
     * If none was explicitly set, it will return the default
     * {@link AAudioSpatializationBehavior#AAUDIO_SPATIALIZATION_BEHAVIOR_AUTO} behavior.
     * <p>
     * Available since API level 32.
     *
     * @return spatialization behavior, for example {@link AAudioSpatializationBehavior#AAUDIO_SPATIALIZATION_BEHAVIOR_AUTO}
     */
    public AAudioSpatializationBehavior getSpatializationBehavior() throws Throwable {
        return AAudioSpatializationBehavior.fromValue((int) AAudioStream_getSpatializationBehavior.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getSpatializationBehavior = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getSpatializationBehavior"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    /**
     * Return whether the content of the stream is spatialized.
     * <p>
     * Available since API level 32.
     *
     * @return true if the content is spatialized
     */
    public boolean isContentSpatialized() throws Throwable {
        return (boolean) AAudioStream_isContentSpatialized.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_isContentSpatialized = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_isContentSpatialized"),
            FunctionDescriptor.of(C_BOOL, C_POINTER)
    );

    /**
     * Return the input preset for the stream.
     * <p>
     * Available since API level 28.
     *
     * @return input preset, for example {@link AAudioInputPreset#AAUDIO_INPUT_PRESET_CAMCORDER}
     */
    public AAudioInputPreset getInputPreset() throws Throwable {
        return AAudioInputPreset.fromValue((int) AAudioStream_getInputPreset.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getInputPreset = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getInputPreset"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );


    /**
     * Return the policy that determines whether the audio may or may not be captured
     * by other apps or the system.
     * <p>
     * Available since API level 29.
     *
     * @return the allowed capture policy, for example {@link AAudioAllowedCapturePolicy#AAUDIO_ALLOW_CAPTURE_BY_ALL}
     */
    public AAudioAllowedCapturePolicy getAllowedCapturePolicy() throws Throwable {
        return AAudioAllowedCapturePolicy.fromValue((int) AAudioStream_getAllowedCapturePolicy.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getAllowedCapturePolicy = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getAllowedCapturePolicy"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );


    /**
     * Return whether this input stream is marked as privacy sensitive or not.
     * <p>
     * See {@link AAudioStreamBuilder#setPrivacySensitive}.
     * <p>
     * Added in API level 30.
     *
     * @return true if privacy sensitive, false otherwise
     */
    public boolean isPrivacySensitive() throws Throwable {
        return (boolean) AAudioStream_isPrivacySensitive.invokeExact(nativeInstance);
    }

    private final static MethodHandle AAudioStream_isPrivacySensitive = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_isPrivacySensitive"),
            FunctionDescriptor.of(C_BOOL, C_POINTER)
    );

    /**
     * Return the channel mask for the stream. This will be the mask set using
     * {@link AAudioStreamBuilder#setChannelMask}, or {0} otherwise.
     * <p>
     * Available since API level 32.
     *
     * @return actual channel mask
     */
    public AAudioChannelMask getChannelMask() throws Throwable {
        return AAudioChannelMask.fromValue((int) AAudioStream_getChannelMask.invokeExact(nativeInstance));
    }

    private final static MethodHandle AAudioStream_getChannelMask = LINKER.downcallHandle(
            SymbolLookup.loaderLookup().findOrThrow("AAudioStream_getChannelMask"),
            FunctionDescriptor.of(C_INT, C_POINTER)
    );

    @Override
    public void close() throws Exception {
        try {
            closeStream();
            lifetimeArena.close();
        } catch (Throwable ignored) {

        }
    }
}
