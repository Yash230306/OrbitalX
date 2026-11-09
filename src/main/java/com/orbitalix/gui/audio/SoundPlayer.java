package com.orbitalix.gui.audio;

public interface SoundPlayer extends AutoCloseable {
    void caution();
    void alarm();
    void chime();
    void click();
    @Override void close();
}
