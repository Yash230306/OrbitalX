package com.orbitalix.gui.render3d;

public record Part3D(String subsystem, Mesh mesh, Vec3 position, double health) {
    public Part3D {
        if (subsystem == null || subsystem.isBlank() || mesh == null || position == null
                || !Double.isFinite(health) || health < 0 || health > 1)
            throw new IllegalArgumentException("Invalid spacecraft part");
    }
}
