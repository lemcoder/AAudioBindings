package io.github.lemcoder.aaudio.api;

@FunctionalInterface
public interface AAudioStreamDataCallbackApi {
    byte[] onData(int numFrames);
}
