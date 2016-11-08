package de.jeha.j7.core;

/**
 * @author jenshadlich@googlemail.com
 */
public enum OpState {

    UP(true),
    DOWN(false);

    private final boolean up;

    OpState(boolean up) {
        this.up = up;
    }

    public boolean isUp() {
        return up;
    }

}
