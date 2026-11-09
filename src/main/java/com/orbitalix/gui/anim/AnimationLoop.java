package com.orbitalix.gui.anim;

import javax.swing.Timer;
import java.util.ArrayList;
import java.util.List;

public final class AnimationLoop {
    private final List<Animatable> animations = new ArrayList<>();
    private final Timer timer;
    private long previousNanos;

    public AnimationLoop(Runnable repaint) {
        timer = new Timer(16, event -> {
            long now = System.nanoTime();
            double elapsed = previousNanos == 0 ? 0 : Math.min(.1, (now - previousNanos) / 1_000_000_000d);
            previousNanos = now;
            animations.removeIf(animation -> !animation.advance(elapsed));
            repaint.run();
        });
    }
    public void add(Animatable animation) { animations.add(animation); }
    public void start() { previousNanos = 0; timer.start(); }
    public void stop() { timer.stop(); }
    public boolean isRunning() { return timer.isRunning(); }
}
