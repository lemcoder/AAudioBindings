package io.github.lemcoder.mikroaall;

import com.v7878.foreign.MemorySegment;

@FunctionalInterface
interface AAudioStreamDataCallbackInternal {
    int onData(MemorySegment stream, MemorySegment userData, MemorySegment audioData, int numFrames);
}
