package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.util.List;

public final class BoxMesh extends Mesh {
    public BoxMesh(double width, double height, double depth, Color color) {
        super(List.of(new Vec3(-width/2,-height/2,-depth/2), new Vec3(width/2,-height/2,-depth/2),
                new Vec3(width/2,height/2,-depth/2), new Vec3(-width/2,height/2,-depth/2),
                new Vec3(-width/2,-height/2,depth/2), new Vec3(width/2,-height/2,depth/2),
                new Vec3(width/2,height/2,depth/2), new Vec3(-width/2,height/2,depth/2)),
                List.of(new int[]{0,1,2,3},new int[]{5,4,7,6},new int[]{4,0,3,7},
                        new int[]{1,5,6,2},new int[]{3,2,6,7},new int[]{4,5,1,0}), color);
        if (width <= 0 || height <= 0 || depth <= 0) throw new IllegalArgumentException("Box dimensions must be positive");
    }
}
