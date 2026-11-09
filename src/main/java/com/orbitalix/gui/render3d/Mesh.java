package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public abstract class Mesh {
    private final List<Vec3> vertices;
    private final List<int[]> faces;
    private final Color color;

    protected Mesh(List<Vec3> vertices, List<int[]> faces, Color color) {
        this.vertices = List.copyOf(vertices);
        this.faces = faces.stream().map(int[]::clone).toList();
        this.color = color;
        if (this.vertices.isEmpty() || this.faces.isEmpty() || color == null)
            throw new IllegalArgumentException("A mesh needs vertices, faces, and a material color");
        for (int[] face : this.faces)
            for (int index : face) if (index < 0 || index >= this.vertices.size()) throw new IllegalArgumentException("Mesh face index out of bounds");
    }
    public List<Vec3> vertices() { return vertices; }
    public List<int[]> faces() { return faces.stream().map(int[]::clone).toList(); }
    public Color color() { return color; }

    protected static List<int[]> triangles(int ringCount, int segmentCount) {
        List<int[]> faces = new ArrayList<>();
        for (int ring = 0; ring < ringCount - 1; ring++)
            for (int segment = 0; segment < segmentCount; segment++) {
                int a = ring * segmentCount + segment, b = ring * segmentCount + (segment + 1) % segmentCount;
                int c = (ring + 1) * segmentCount + (segment + 1) % segmentCount, d = (ring + 1) * segmentCount + segment;
                faces.add(new int[]{a, b, d}); faces.add(new int[]{b, c, d});
            }
        return faces;
    }
}
