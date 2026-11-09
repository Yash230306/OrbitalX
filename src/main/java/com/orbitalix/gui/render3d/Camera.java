package com.orbitalix.gui.render3d;

public final class Camera {
    public record Projection(double x, double y, double depth, boolean visible) {}
    private double yaw = -.35, pitch = .24, distance = 7.5;

    public void rotate(double yawDelta, double pitchDelta) {
        yaw += yawDelta;
        pitch = Math.max(-1.25, Math.min(1.25, pitch + pitchDelta));
    }
    public void zoom(double amount) { distance = Math.max(3, Math.min(18, distance + amount)); }
    public double distance() { return distance; }
    public Projection project(Vec3 point, int x, int y, int width, int height) {
        Matrix rotation = Matrix.rotationX(pitch).multiply(Matrix.rotationY(yaw));
        Vec3 cameraPoint = rotation.transform(point);
        double depth = cameraPoint.z() + distance;
        if (depth <= .25) return new Projection(0, 0, depth, false);
        double focal = Math.min(width, height) * 1.15;
        return new Projection(x + width / 2d + cameraPoint.x() * focal / depth,
                y + height / 2d - cameraPoint.y() * focal / depth, depth, true);
    }
}
