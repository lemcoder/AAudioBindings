package io.github.lemcoder.aaudio.api;

import io.github.lemcoder.aaudio.AAudioResult;

@FunctionalInterface
public interface AAudioErrorCallbackApi {
    void onError(AAudioResult error);
}
