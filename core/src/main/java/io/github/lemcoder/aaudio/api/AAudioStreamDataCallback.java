package io.github.lemcoder.aaudio.api;

@FunctionalInterface
public interface AAudioStreamDataCallback {
    byte[] onData(int numFrames);
}
