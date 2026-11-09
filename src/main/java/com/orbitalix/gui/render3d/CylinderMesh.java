package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public final class CylinderMesh extends Mesh {
    public CylinderMesh(double radius, double height, int segments, Color color) {
        super(vertices(radius, height, segments), faces(segments), color);
        if (radius <= 0 || height <= 0 || segments < 3) throw new IllegalArgumentException("Invalid cylinder dimensions");
    }
    private static List<Vec3> vertices(double radius, double height, int segments) {
        if (segments < 3) throw new IllegalArgumentException("Cylinder needs at least three segments");
        List<Vec3> points = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            double a = 2 * Math.PI * i / segments;
            points.add(new Vec3(-height / 2, Math.cos(a) * radius, Math.sin(a) * radius));
            points.add(new Vec3(height / 2, Math.cos(a) * radius, Math.sin(a) * radius));
        }
        return points;
    }
    private static List<int[]> faces(int segments) {
        List<int[]> faces = new ArrayList<>();
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            faces.add(new int[]{2*i, 2*next, 2*next+1, 2*i+1});
        }
        return faces;
    }
}
