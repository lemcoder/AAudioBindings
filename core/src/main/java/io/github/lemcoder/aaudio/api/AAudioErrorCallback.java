package io.github.lemcoder.aaudio.api;

import io.github.lemcoder.aaudio.model.AAudioResult;

@FunctionalInterface
public interface AAudioErrorCallback {
    void onError(AAudioResult error);
}
