package com.orbitalix.gui.render3d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * High-fidelity 3D software rasterizer with dual-source space lighting,
 * Blinn-Phong specular reflections, subsystem picking, and active subsystem glow halos.
 */
public final class Renderer {
    private record Face(Part3D part, Polygon polygon, double depth, Vec3 normal) {}
    private List<Face> lastFaces = List.of();

    public void render(Graphics2D graphics, List<Part3D> parts, Camera camera, int x, int y, int width, int height) {
        render(graphics, parts, camera, x, y, width, height, null);
    }

    public void render(Graphics2D graphics, List<Part3D> parts, Camera camera, int x, int y, int width, int height, String selectedSubsystem) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.clipRect(x, y, width, height);

        Vec3 sunDir = new Vec3(-0.45, 0.78, -0.44).normalized();
        Vec3 fillDir = new Vec3(0.32, -0.35, 0.58).normalized();
        Vec3 viewDir = new Vec3(0, 0, 1.0).normalized();
        Vec3 halfVector = sunDir.add(viewDir).normalized();

        List<Face> faces = new ArrayList<>();
        for (Part3D part : parts) {
            List<Vec3> world = part.mesh().vertices().stream().map(point -> point.add(part.position())).toList();
            List<Camera.Projection> points = world.stream().map(point -> camera.project(point, x, y, width, height)).toList();

            for (int[] indices : part.mesh().faces()) {
                if (indices.length < 3) continue;
                boolean visible = true;
                Polygon polygon = new Polygon();
                double depth = 0;
                for (int index : indices) {
                    Camera.Projection point = points.get(index);
                    visible &= point.visible();
                    polygon.addPoint((int) Math.round(point.x()), (int) Math.round(point.y()));
                    depth += point.depth();
                }
                if (!visible || polygon.npoints < 3) continue;

                Vec3 a = world.get(indices[0]), b = world.get(indices[1]), c = world.get(indices[2]);
                Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalized();
                faces.add(new Face(part, polygon, depth / indices.length, normal));
            }
        }

        faces.sort(Comparator.comparingDouble(Face::depth).reversed());

        long time = System.currentTimeMillis();
        float pulse = (float) ((Math.sin(time * 0.006) + 1.0) / 2.0);

        for (Face face : faces) {
            Color base = face.part().mesh().color();
            boolean isSelected = selectedSubsystem != null && face.part().subsystem().equalsIgnoreCase(selectedSubsystem);

            // Lighting: Ambient + Direct Sun + Earth Fill
            double nDotL = Math.max(0, face.normal().dot(sunDir));
            double nDotFill = Math.max(0, face.normal().dot(fillDir));
            double diffuse = 0.26 + 0.62 * nDotL + 0.12 * nDotFill;

            // Specular highlight (Blinn-Phong)
            double nDotH = Math.max(0, face.normal().dot(halfVector));
            double specular = nDotL > 0 ? Math.pow(nDotH, 14) * 0.38 : 0.0;

            // Damage / Warning tint
            double damageTint = Math.max(0, 1.0 - face.part().health()) * 0.75;
            double pulseEffect = damageTint > 0 ? (0.2 + 0.3 * pulse) : 0.0;

            int r = channel((base.getRed() * (1 - damageTint) + 255 * (damageTint + pulseEffect)) * diffuse + 255 * specular);
            int gr = channel((base.getGreen() * (1 - damageTint) + 40 * damageTint) * diffuse + 255 * specular);
            int bl = channel((base.getBlue() * (1 - damageTint) + 45 * damageTint) * diffuse + 255 * specular);

            if (isSelected) {
                // Subtle energized cyan tint for selected subsystem
                r = channel(r * 0.85 + 40);
                gr = channel(gr * 0.85 + 130);
                bl = channel(bl * 0.85 + 200);
            }

            g.setColor(new Color(r, gr, bl));
            g.fillPolygon(face.polygon());

            // Edge styling
            if (isSelected) {
                g.setColor(new Color(90, 215, 255, 190));
                g.setStroke(new BasicStroke(1.2f));
                g.drawPolygon(face.polygon());
            } else if (damageTint > 0) {
                g.setColor(new Color(255, 60, 60, (int) (120 + 80 * pulse)));
                g.setStroke(new BasicStroke(1.0f));
                g.drawPolygon(face.polygon());
            } else {
                g.setColor(new Color(15, 25, 40, 75));
                g.setStroke(new BasicStroke(0.7f));
                g.drawPolygon(face.polygon());
            }
        }

        lastFaces = List.copyOf(faces);
        g.dispose();
    }

    public String pick(int x, int y) {
        for (int i = lastFaces.size() - 1; i >= 0; i--) {
            if (lastFaces.get(i).polygon().contains(x, y)) {
                return lastFaces.get(i).part().subsystem();
            }
        }
        return null;
    }

    private static int channel(double value) {
        return (int) Math.max(0, Math.min(255, Math.round(value)));
    }
}
