package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.MemorySegment;


@FunctionalInterface
interface AAudioStreamDataCallbackInternal {

    /**
     * Prototype for the data function that is passed to AAudioStreamBuilder_setDataCallback().
     *
     * For an output stream, this function should render and write numFrames of data
     * in the streams current data format to the audioData buffer.
     *
     * For an input stream, this function should read and process numFrames of data
     * from the audioData buffer. The data in the audioData buffer must not be modified
     * directly. Instead, it should be copied to another buffer before doing any modification.
     * In many cases, writing to the audioData buffer of an input stream will result in a
     * native exception.
     *
     * The audio data is passed through the buffer. So do NOT call AAudioStream_read() or
     * AAudioStream_write() on the stream that is making the callback.
     *
     * Note that numFrames can vary unless AAudioStreamBuilder_setFramesPerDataCallback()
     * is called.
     *
     * Also note that this callback function should be considered a "real-time" function.
     * It must not do anything that could cause an unbounded delay because that can cause the
     * audio to glitch or pop.
     *
     * These are things the function should NOT do:
     * <ul>
     * <li>allocate memory using, for example, malloc() or new</li>
     * <li>any file operations such as opening, closing, reading or writing</li>
     * <li>any network operations such as streaming</li>
     * <li>use any mutexes or other synchronization primitives</li>
     * <li>sleep</li>
     * <li>stop or close the stream</li>
     * <li>AAudioStream_read()</li>
     * <li>AAudioStream_write()</li>
     * </ul>
     *
     * The following are OK to call from the data callback:
     * <ul>
     * <li>AAudioStream_get*()</li>
     * <li>AAudio_convertResultToText()</li>
     * </ul>
     *
     * If you need to move data, eg. MIDI commands, in or out of the callback function then
     * we recommend the use of non-blocking techniques such as an atomic FIFO.
     *
     * @param stream reference provided by AAudioStreamBuilder_openStream()
     * @param userData the same address that was passed to AAudioStreamBuilder_setCallback()
     * @param audioData a pointer to the audio data
     * @param numFrames the number of frames to be processed, which can vary
     * @return AAUDIO_CALLBACK_RESULT_*
     */
    int onData(MemorySegment stream, MemorySegment userData, MemorySegment audioData, int numFrames);
}
