package io.github.lemcoder.mikroaall;

@FunctionalInterface
public interface AAudioStreamDataCallbackApi {
    byte[] onData(int numFrames);
}
