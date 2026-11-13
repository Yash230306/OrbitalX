package com.orbitalix.gui.render3d;

import com.orbitalix.api.HealthStatus;
import com.orbitalix.api.SimulationSnapshot;
import com.orbitalix.api.SubsystemSnapshot;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Realistic eight-part aerospace spacecraft model.
 * Faithfully constructs authentic command capsule, service module, propulsion rocket bell,
 * deployable photovoltaic solar wings, thermal radiators, parabolic dish, RCS thrusters,
 * forward star tracker, and avionics guidance core.
 */
public final class SpacecraftModel {

    public List<Part3D> build(SimulationSnapshot snapshot) {
        Map<String, SubsystemSnapshot> health = snapshot.subsystems().stream()
                .collect(java.util.stream.Collectors.toMap(item -> item.name().toLowerCase(), item -> item));

        return List.of(
                part("Propulsion", buildPropulsionEngine(), new Vec3(1.5, 0, 0), health),
                part("Power", buildSolarWings(), new Vec3(0, 0, 0), health),
                part("Thermal", buildRadiatorPanels(), new Vec3(0, 0, 0), health),
                part("Communication", buildParabolicDish(), new Vec3(-0.6, 0.95, 0.55), health),
                part("Attitude", buildRcsThrusters(), new Vec3(0, 0, 0), health),
                part("Life Support", buildFuselage(), new Vec3(0, 0, 0), health),
                part("Navigation", buildStarTracker(), new Vec3(-1.45, 0.48, 0.35), health),
                part("Computer", buildAvionicsBay(), new Vec3(0.3, 0.45, -0.65), health)
        );
    }

    private Part3D part(String name, Mesh mesh, Vec3 position, Map<String, SubsystemSnapshot> health) {
        SubsystemSnapshot subsystem = health.get(name.toLowerCase());
        HealthStatus status = subsystem == null ? HealthStatus.NOMINAL : subsystem.status();
        if (subsystem != null && !subsystem.activeFaults().isEmpty() && status == HealthStatus.NOMINAL) {
            status = HealthStatus.WARNING;
        }
        double fraction = switch (status) {
            case NOMINAL -> 1.0;
            case WARNING -> 0.55;
            case CRITICAL, FAILED -> 0.2;
        };
        return new Part3D(name, mesh, position, fraction);
    }

    /** Central crew capsule & service module fuselage centered at (0, 0, 0). */
    private Mesh buildFuselage() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color hullColor = new Color(232, 236, 244);

        int segments = 16;
        double[][] profile = {
                {-2.1, 0.30}, // Docking hatch
                {-1.6, 0.60}, // Forward capsule nose
                {-1.1, 0.86}, // Command capsule shoulder
                { 0.0, 0.86}, // Habitat cylinder mid
                { 1.0, 0.84}, // Service module bay
                { 1.5, 0.70}  // Aft adapter collar
        };

        for (double[] ring : profile) {
            double x = ring[0], r = ring[1];
            for (int i = 0; i < segments; i++) {
                double angle = 2 * Math.PI * i / segments;
                vertices.add(new Vec3(x, Math.cos(angle) * r, Math.sin(angle) * r));
            }
        }

        // Hull quads
        for (int ring = 0; ring < profile.length - 1; ring++) {
            int baseA = ring * segments;
            int baseB = (ring + 1) * segments;
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                faces.add(new int[]{baseA + i, baseA + next, baseB + next, baseB + i});
            }
        }

        // Nose cap
        int noseCenter = vertices.size();
        vertices.add(new Vec3(-2.15, 0, 0));
        for (int i = 0; i < segments; i++) {
            faces.add(new int[]{noseCenter, i, (i + 1) % segments});
        }

        // Aft bulkhead
        int aftCenter = vertices.size();
        vertices.add(new Vec3(1.5, 0, 0));
        int aftBase = (profile.length - 1) * segments;
        for (int i = 0; i < segments; i++) {
            faces.add(new int[]{aftCenter, aftBase + (i + 1) % segments, aftBase + i});
        }

        return new CustomMesh(vertices, faces, hullColor);
    }

    /** Main orbital rocket engine bell nozzle. */
    private Mesh buildPropulsionEngine() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color nozzleColor = new Color(68, 72, 82);

        int segments = 14;
        double[][] profile = {
                {0.00, 0.36}, // Gimbal mount
                {0.20, 0.22}, // Throat constriction
                {0.55, 0.40}, // Mid expansion bell
                {0.95, 0.65}  // Exit nozzle skirt rim
        };

        for (double[] ring : profile) {
            double x = ring[0], r = ring[1];
            for (int i = 0; i < segments; i++) {
                double angle = 2 * Math.PI * i / segments;
                vertices.add(new Vec3(x, Math.cos(angle) * r, Math.sin(angle) * r));
            }
        }

        for (int ring = 0; ring < profile.length - 1; ring++) {
            int baseA = ring * segments;
            int baseB = (ring + 1) * segments;
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                faces.add(new int[]{baseA + i, baseA + next, baseB + next, baseB + i});
            }
        }

        // Glowing combustion chamber disc
        int throatCenter = vertices.size();
        vertices.add(new Vec3(0.20, 0, 0));
        int throatBase = segments;
        for (int i = 0; i < segments; i++) {
            faces.add(new int[]{throatCenter, throatBase + i, throatBase + (i + 1) % segments});
        }

        return new CustomMesh(vertices, faces, nozzleColor);
    }

    /** Twin deployable photovoltaic solar array wings with structural mounting trusses. */
    private Mesh buildSolarWings() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color cellColor = new Color(24, 76, 140);

        // Port wing panel (z from 1.05 to 3.35, x from -0.65 to 0.65, y = 0.02)
        addBox(vertices, faces, -0.65, 0.65, -0.02, 0.02, 1.05, 3.35);
        // Starboard wing panel (z from -1.05 to -3.35)
        addBox(vertices, faces, -0.65, 0.65, -0.02, 0.02, -3.35, -1.05);

        // Structural connecting struts (port & starboard)
        addBox(vertices, faces, -0.08, 0.08, -0.04, 0.04, 0.84, 1.05);
        addBox(vertices, faces, -0.08, 0.08, -0.04, 0.04, -1.05, -0.84);

        return new CustomMesh(vertices, faces, cellColor);
    }

    /** Active heat-rejection radiator panels on dorsal and ventral stations. */
    private Mesh buildRadiatorPanels() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color radColor = new Color(215, 226, 238);

        // Dorsal radiator (extends +Y from 0.88 to 2.15, x from 0.35 to 1.25)
        addBox(vertices, faces, 0.35, 1.25, 0.88, 2.15, -0.025, 0.025);
        // Ventral radiator (extends -Y from -0.88 to -2.15)
        addBox(vertices, faces, 0.35, 1.25, -2.15, -0.88, -0.025, 0.025);

        return new CustomMesh(vertices, faces, radColor);
    }

    /** High-gain deep-space parabolic dish antenna with gimbal boom and feed horn. */
    private Mesh buildParabolicDish() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color dishColor = new Color(225, 192, 105);

        int segments = 12;
        double[][] dishRings = {
                {-0.15, 0.08},
                {-0.06, 0.28},
                { 0.12, 0.48}
        };

        for (double[] ring : dishRings) {
            double z = ring[0], r = ring[1];
            for (int i = 0; i < segments; i++) {
                double angle = 2 * Math.PI * i / segments;
                vertices.add(new Vec3(Math.cos(angle) * r, Math.sin(angle) * r, z));
            }
        }

        for (int ring = 0; ring < dishRings.length - 1; ring++) {
            int baseA = ring * segments;
            int baseB = (ring + 1) * segments;
            for (int i = 0; i < segments; i++) {
                int next = (i + 1) % segments;
                faces.add(new int[]{baseA + i, baseA + next, baseB + next, baseB + i});
            }
        }

        // Feed horn stalk
        int hornTip = vertices.size();
        vertices.add(new Vec3(0, 0, 0.32));
        for (int i = 0; i < segments; i += 3) {
            faces.add(new int[]{hornTip, i, (i + 1) % segments});
        }

        // Support mounting boom
        addBox(vertices, faces, -0.05, 0.05, -0.05, 0.05, -0.45, -0.15);

        return new CustomMesh(vertices, faces, dishColor);
    }

    /** 4-quadrant Reaction Control System (RCS) thruster pods for 3-axis control. */
    private Mesh buildRcsThrusters() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color rcsColor = new Color(192, 160, 92);

        double stationX = 0.82;
        double offset = 0.86;
        double s = 0.12;

        // Top block (+Y)
        addBox(vertices, faces, stationX - s, stationX + s, offset, offset + 0.14, -s, s);
        // Bottom block (-Y)
        addBox(vertices, faces, stationX - s, stationX + s, -offset - 0.14, -offset, -s, s);
        // Port block (+Z)
        addBox(vertices, faces, stationX - s, stationX + s, -s, s, offset, offset + 0.14);
        // Starboard block (-Z)
        addBox(vertices, faces, stationX - s, stationX + s, -s, s, -offset - 0.14, -offset);

        return new CustomMesh(vertices, faces, rcsColor);
    }

    /** Forward optical star tracker and rendezvous lidar sensor suite. */
    private Mesh buildStarTracker() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color trackerColor = new Color(75, 170, 225);

        // Twin star tracker optical sensor baffles
        addBox(vertices, faces, -0.18, 0.18, -0.14, 0.14, -0.12, 0.12);
        addBox(vertices, faces, -0.12, 0.12, 0.14, 0.30, -0.08, 0.08);

        return new CustomMesh(vertices, faces, trackerColor);
    }

    /** Guidance computer and avionics bay with thermal finning. */
    private Mesh buildAvionicsBay() {
        List<Vec3> vertices = new ArrayList<>();
        List<int[]> faces = new ArrayList<>();
        Color avionicsColor = new Color(78, 120, 168);

        addBox(vertices, faces, -0.32, 0.32, -0.22, 0.22, -0.18, 0.18);
        // Heatsink ridge
        addBox(vertices, faces, -0.28, 0.28, 0.22, 0.28, -0.15, 0.15);

        return new CustomMesh(vertices, faces, avionicsColor);
    }

    private static void addBox(List<Vec3> vertices, List<int[]> faces,
                               double minX, double maxX, double minY, double maxY, double minZ, double maxZ) {
        int base = vertices.size();
        vertices.add(new Vec3(minX, minY, minZ)); // 0
        vertices.add(new Vec3(maxX, minY, minZ)); // 1
        vertices.add(new Vec3(maxX, maxY, minZ)); // 2
        vertices.add(new Vec3(minX, maxY, minZ)); // 3
        vertices.add(new Vec3(minX, minY, maxZ)); // 4
        vertices.add(new Vec3(maxX, minY, maxZ)); // 5
        vertices.add(new Vec3(maxX, maxY, maxZ)); // 6
        vertices.add(new Vec3(minX, maxY, maxZ)); // 7

        // 6 box faces
        faces.add(new int[]{base + 0, base + 1, base + 2, base + 3}); // Front
        faces.add(new int[]{base + 5, base + 4, base + 7, base + 6}); // Back
        faces.add(new int[]{base + 4, base + 0, base + 3, base + 7}); // Left
        faces.add(new int[]{base + 1, base + 5, base + 6, base + 2}); // Right
        faces.add(new int[]{base + 3, base + 2, base + 6, base + 7}); // Top
        faces.add(new int[]{base + 4, base + 5, base + 1, base + 0}); // Bottom
    }
}
