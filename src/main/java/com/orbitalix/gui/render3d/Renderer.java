package com.orbitalix.gui.render3d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class Renderer {
    private record Face(Part3D part, Polygon polygon, double depth, Vec3 normal) {}
    private List<Face> lastFaces = List.of();

    public void render(Graphics2D graphics, List<Part3D> parts, Camera camera, int x, int y, int width, int height) {
        Graphics2D g = (Graphics2D) graphics.create();
        g.clipRect(x, y, width, height);
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
                    polygon.addPoint((int)Math.round(point.x()), (int)Math.round(point.y()));
                    depth += point.depth();
                }
                if (!visible || polygon.npoints < 3) continue;
                Vec3 a = world.get(indices[0]), b = world.get(indices[1]), c = world.get(indices[2]);
                Vec3 normal = b.subtract(a).cross(c.subtract(a)).normalized();
                faces.add(new Face(part, polygon, depth / indices.length, normal));
            }
        }
        faces.sort(Comparator.comparingDouble(Face::depth).reversed());
        for (Face face : faces) {
            Color base = face.part().mesh().color();
            double light = .28 + .72 * Math.max(0, face.normal().dot(new Vec3(-.35, .8, -.5).normalized()));
            double damageTint = Math.max(0, 1 - face.part().health()) * .72;
            g.setColor(new Color(channel((base.getRed() * (1 - damageTint) + 255 * damageTint) * light),
                    channel((base.getGreen() * (1 - damageTint) + 48 * damageTint) * light),
                    channel((base.getBlue() * (1 - damageTint) + 52 * damageTint) * light)));
            g.fillPolygon(face.polygon());
            g.setColor(new Color(10, 20, 32, 170)); g.drawPolygon(face.polygon());
        }
        lastFaces = List.copyOf(faces);
        g.dispose();
    }

    public String pick(int x, int y) {
        for (int i = lastFaces.size() - 1; i >= 0; i--)
            if (lastFaces.get(i).polygon().contains(x, y)) return lastFaces.get(i).part().subsystem();
        return null;
    }
    private static int channel(double value) { return (int)Math.max(0, Math.min(255, Math.round(value))); }
}
