package com.orbitalix.gui.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Generates short tones directly as PCM without bundled sound assets. */
public final class SynthSoundPlayer implements SoundPlayer {
    private static final AudioFormat FORMAT = new AudioFormat(22_050, 16, 1, true, false);
    private final SourceDataLine line;
    private final ExecutorService audio = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "orbitalix-audio"); thread.setDaemon(true); return thread;
    });

    public SynthSoundPlayer() throws LineUnavailableException {
        line = AudioSystem.getSourceDataLine(FORMAT);
        line.open(FORMAT);
    }

    public static SoundPlayer createDefault() {
        try { return new SynthSoundPlayer(); }
        catch (LineUnavailableException | IllegalArgumentException ex) { return new SilentSoundPlayer(); }
    }

    @Override public void caution() { tone(660, 120); }
    @Override public void alarm() { tone(880, 230); }
    @Override public void chime() { tone(1046, 160); }
    @Override public void click() { tone(480, 35); }

    private void tone(int frequency, int durationMillis) {
        audio.execute(() -> {
            int samples = Math.round(FORMAT.getSampleRate() * durationMillis / 1000f);
            byte[] pcm = new byte[samples * 2];
            for (int i = 0; i < samples; i++) {
                double envelope = Math.min(1, Math.min(i / 200d, (samples - i) / 400d));
                short sample = (short)(Math.sin(2 * Math.PI * frequency * i / FORMAT.getSampleRate()) * 6500 * envelope);
                pcm[i * 2] = (byte)sample; pcm[i * 2 + 1] = (byte)(sample >> 8);
            }
            synchronized (line) {
                if (!line.isOpen()) return;
                line.start(); line.write(pcm, 0, pcm.length); line.drain();
            }
        });
    }

    @Override public void close() {
        audio.shutdownNow();
        synchronized (line) { line.close(); }
    }
}
