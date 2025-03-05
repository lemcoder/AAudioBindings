package io.github.lemcoder.aaudio.model;

public enum AAudioAudioDirection {
    OUTPUT(0),
    INPUT(1);

    private final int value;

    AAudioAudioDirection(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static AAudioAudioDirection fromValue(int value) {
        for (AAudioAudioDirection direction : AAudioAudioDirection.values()) {
            if (direction.getValue() == value) {
                return direction;
            }
        }
        return null;
    }
}
