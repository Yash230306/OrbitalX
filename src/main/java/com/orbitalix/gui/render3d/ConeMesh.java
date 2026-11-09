package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class ConeMesh extends Mesh {
    public ConeMesh(double radius, double height, int segments, Color color) {
        super(vertices(radius, height, segments), faces(segments), color);
        if (radius <= 0 || height <= 0 || segments < 3) throw new IllegalArgumentException("Invalid cone dimensions");
    }
    private static List<Vec3> vertices(double radius, double height, int segments) {
        if (segments < 3) throw new IllegalArgumentException("Cone needs at least three segments");
        List<Vec3> points = new ArrayList<>();
        points.add(new Vec3(0, height / 2, 0));
        for (int i = 0; i < segments; i++) {
            double a = 2 * Math.PI * i / segments;
            points.add(new Vec3(Math.cos(a) * radius, -height / 2, Math.sin(a) * radius));
        }
        return points;
    }
    private static List<int[]> faces(int segments) {
        List<int[]> faces = new ArrayList<>();
        for (int i = 0; i < segments; i++) faces.add(new int[]{0, i + 1, (i + 1) % segments + 1});
        return faces;
    }
}
