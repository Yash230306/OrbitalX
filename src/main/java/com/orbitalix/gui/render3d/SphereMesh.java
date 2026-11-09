package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class SphereMesh extends Mesh {
    public SphereMesh(double radius, int rings, int segments, Color color) {
        super(vertices(radius, rings, segments), triangles(rings, segments), color);
        if (radius <= 0 || rings < 2 || segments < 3) throw new IllegalArgumentException("Invalid sphere dimensions");
    }
    private static List<Vec3> vertices(double radius, int rings, int segments) {
        if (rings < 2 || segments < 3) throw new IllegalArgumentException("Sphere tessellation is too small");
        List<Vec3> points = new ArrayList<>();
        for (int ring = 0; ring < rings; ring++) {
            double phi = Math.PI * ring / (rings - 1);
            for (int segment = 0; segment < segments; segment++) {
                double theta = 2 * Math.PI * segment / segments;
                points.add(new Vec3(radius * Math.sin(phi) * Math.cos(theta),
                        radius * Math.cos(phi), radius * Math.sin(phi) * Math.sin(theta)));
            }
        }
        return points;
    }
}
