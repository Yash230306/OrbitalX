package com.orbitalix.gui.render3d;

public record Vec3(double x, double y, double z) {
    public Vec3 {
        if (!Double.isFinite(x) || !Double.isFinite(y) || !Double.isFinite(z))
            throw new IllegalArgumentException("Vector coordinates must be finite");
    }
    public Vec3 add(Vec3 other) { return new Vec3(x + other.x, y + other.y, z + other.z); }
    public Vec3 subtract(Vec3 other) { return new Vec3(x - other.x, y - other.y, z - other.z); }
    public Vec3 scale(double factor) { return new Vec3(x * factor, y * factor, z * factor); }
    public double dot(Vec3 other) { return x * other.x + y * other.y + z * other.z; }
    public Vec3 cross(Vec3 other) { return new Vec3(y * other.z - z * other.y, z * other.x - x * other.z, x * other.y - y * other.x); }
    public double length() { return Math.sqrt(dot(this)); }
    public Vec3 normalized() { double length = length(); return length == 0 ? new Vec3(0, 0, 0) : scale(1 / length); }
}
