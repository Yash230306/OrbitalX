package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.List;

/** Arbitrary 3D polygon mesh with explicit vertices, faces, and material color. */
public final class CustomMesh extends Mesh {
    public CustomMesh(List<Vec3> vertices, List<int[]> faces, Color color) {
        super(vertices, faces, color);
    }
}
