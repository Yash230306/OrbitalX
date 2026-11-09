package com.orbitalix.gui.render3d;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.api.SimulationSnapshot;
import com.orbitalix.api.SubsystemSnapshot;
import java.awt.Color;
import java.util.List;
import java.util.Map;

public final class SpacecraftModel {
    public List<Part3D> build(SimulationSnapshot snapshot) {
        Map<String, SubsystemSnapshot> health = snapshot.subsystems().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.name().toLowerCase(), item -> item));
        return List.of(
                part("Propulsion", new ConeMesh(.52, 1.0, 12, new Color(230, 114, 68)), new Vec3(1.8, 0, 0), health),
                part("Power", new BoxMesh(1.7, .12, 1.2, new Color(59, 152, 205)), new Vec3(-.2, -1.05, 0), health),
                part("Thermal", new BoxMesh(.6, .35, .9, new Color(202, 106, 59)), new Vec3(.8, .45, 0), health),
                part("Communication", new SphereMesh(.3, 7, 10, new Color(102, 202, 218)), new Vec3(-1.35, .45, 0), health),
                part("Attitude", new CylinderMesh(.28, .9, 12, new Color(188, 180, 122)), new Vec3(0, .62, .68), health),
                part("Life Support", new BoxMesh(3.2, .62, .7, new Color(152, 177, 194)), new Vec3(0, 0, 0), health),
                part("Navigation", new SphereMesh(.22, 7, 10, new Color(185, 143, 226)), new Vec3(-1.12, -.18, .45), health),
                part("Computer", new BoxMesh(.55, .44, .7, new Color(98, 117, 153)), new Vec3(.25, .05, -.46), health)
        );
    }

    private Part3D part(String name, Mesh mesh, Vec3 position, Map<String, SubsystemSnapshot> health) {
        SubsystemSnapshot subsystem = health.get(name.toLowerCase());
        HealthStatus status = subsystem == null ? HealthStatus.NOMINAL : subsystem.status();
        if (subsystem != null && !subsystem.activeFaults().isEmpty() && status == HealthStatus.NOMINAL)
            status = HealthStatus.WARNING;
        double fraction = switch (status) { case NOMINAL -> 1; case WARNING -> .55; case CRITICAL, FAILED -> .2; };
        return new Part3D(name, mesh, position, fraction);
    }
}
