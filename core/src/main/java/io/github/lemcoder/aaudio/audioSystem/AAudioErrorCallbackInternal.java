package io.github.lemcoder.aaudio.audioSystem;

import com.v7878.foreign.MemorySegment;

@FunctionalInterface
interface AAudioErrorCallbackInternal {

    /**
     * Prototype for the callback function that is passed to
     * AAudioStreamBuilder_setErrorCallback().
     * <p>
     * The following may NOT be called from the error callback:
     * <ul>
     * <li>AAudioStream_requestStop()</li>
     * <li>AAudioStream_requestPause()</li>
     * <li>AAudioStream_close()</li>
     * <li>AAudioStream_waitForStateChange()</li>
     * <li>AAudioStream_read()</li>
     * <li>AAudioStream_write()</li>
     * </ul>
     * <p>
     * The following are OK to call from the error callback:
     * <ul>
     * <li>AAudioStream_get*()</li>
     * <li>AAudio_convertResultToText()</li>
     * </ul>
     *
     * @param stream   reference provided by AAudioStreamBuilder_openStream()
     * @param userData the same address that was passed to AAudioStreamBuilder_setErrorCallback()
     * @param error    an AAUDIO_ERROR_* value.
     */
    void onError(MemorySegment stream, MemorySegment userData, int error);
}
