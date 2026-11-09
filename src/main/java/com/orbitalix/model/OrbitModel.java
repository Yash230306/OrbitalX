package com.orbitalix.model;

import com.orbitalix.api.Vec2;
import com.orbitalix.api.Copyable;
import java.util.ArrayList;
import java.util.List;

/** Bezier orbital path with bounded actual trail and predicted continuation. */
public final class OrbitModel implements Copyable<OrbitModel> {
    private final double distance;
    private final ArrayList<Vec2> trail = new ArrayList<>(200);

    public OrbitModel(double distance) {
        if (!Double.isFinite(distance) || distance <= 0) throw new IllegalArgumentException("Distance must be positive");
        this.distance = distance;
    }
    private OrbitModel(double distance, List<Vec2> history) {
        this(distance);
        trail.addAll(history);
    }
    public Vec2 planned(double fraction) {
        double t = Math.max(0, Math.min(1, fraction));
        Vec2 p0 = new Vec2(0, 0), p1 = new Vec2(distance / 2, 28), p2 = new Vec2(distance, 0);
        double u = 1 - t;
        return new Vec2(u * u * p0.x() + 2 * u * t * p1.x() + t * t * p2.x(),
                u * u * p0.y() + 2 * u * t * p1.y() + t * t * p2.y());
    }
    public synchronized void record(double progress, double courseError) {
        Vec2 base = planned(progress / distance);
        trail.add(new Vec2(base.x(), base.y() + Math.tan(Math.toRadians(Math.min(courseError, 89))) * 0.1));
        if (trail.size() > 200) trail.remove(0);
    }
    public synchronized List<Vec2> trail() { return List.copyOf(trail); }
    public List<Vec2> plannedPath(int segments) {
        if (segments < 2) throw new IllegalArgumentException("At least two segments required");
        List<Vec2> points = new ArrayList<>(segments + 1);
        for (int i = 0; i <= segments; i++) points.add(planned(i / (double) segments));
        return List.copyOf(points);
    }
    public synchronized List<Vec2> predicted(double progress, double courseError, int steps) {
        if (steps < 1) throw new IllegalArgumentException("Prediction steps must be positive");
        List<Vec2> path = new ArrayList<>(steps + 1);
        for (int i = 0; i <= steps; i++) {
            double fraction = Math.min(1, progress / distance + (1 - progress / distance) * i / steps);
            Vec2 point = planned(fraction);
            path.add(new Vec2(point.x(), point.y() + Math.tan(Math.toRadians(Math.min(courseError, 89))) * (i / (double) steps)));
        }
        return List.copyOf(path);
    }
    @Override public synchronized OrbitModel copy() { return new OrbitModel(distance, trail); }
}
