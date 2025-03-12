package io.github.lemcoder.aaudio.api;

public interface AAudioStreamDataCallback {
    byte[] onOutputReady(int numFrames);
    void onInputReady(byte[] input, int numFrames);
}
