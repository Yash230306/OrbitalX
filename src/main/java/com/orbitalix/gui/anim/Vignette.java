package com.orbitalix.gui.anim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.geom.Point2D;

public final class Vignette {
    public void paint(Graphics2D graphics, int width, int height, Color tint, float strength) {
        if (width <= 0 || height <= 0 || strength <= 0) return;
        float alpha = Math.min(.8f, strength);
        Color edge = new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), (int)(alpha * 255));
        Color clear = new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), 0);
        float radius = Math.max(width, height) * .75f;
        graphics.setPaint(new RadialGradientPaint(new Point2D.Float(width / 2f, height / 2f), radius,
                new float[]{0f, .65f, 1f}, new Color[]{clear, clear, edge}));
        graphics.fillRect(0, 0, width, height);
    }
}
