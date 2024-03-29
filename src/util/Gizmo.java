package util;

import main.Main;
import perspective.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Gizmo {

    public static void dottedLine(Graphics g, Vector from, Vector to) {
        dottedLine(g, from, to, 10, 4);
    }

    public static void dot(Graphics g, Vector pos) {
        dot(g, pos, 4);
    }

    public static void dot(Graphics g, Vector pos, Color color) {
        g.setColor(color);
        dot(g, pos, 4);
    }

    public static void fillCenteredRect(Graphics g, Vector pos, Vector size) {
        g.fillRect((int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)size.x, (int)size.y);
    }

    public static void drawCenteredRect(Graphics g, Vector pos, Vector size) {
        g.drawRect((int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)size.x, (int)size.y);
    }

    public static void drawCenteredImage(Graphics g, BufferedImage image, Vector pos, Vector size) {
        g.drawImage(image, (int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)size.x, (int)size.y, null);
    }

    public static void drawSelectRect(Graphics g, Camera camera, Vector from, Vector to) {

        drawSelectRect(g, camera.toScreen(from), camera.toScreen(to));
    }
    public static void drawSelectRect(Graphics g, Vector from, Vector to) {

        Vector tl = new Vector(Math.min(from.x, to.x), Math.min(from.y, to.y));
        Vector br = new Vector(Math.max(from.x, to.x), Math.max(from.y, to.y));

        g.setColor(Colors.select);
        g.fillRect((int)tl.x, (int)tl.y, (int)(br.x - tl.x), (int)(br.y - tl.y));

        g.setColor(Color.BLACK);

        Vector tr = new Vector(br.x, tl.y);
        Vector bl = new Vector(tl.x, br.y);

        dottedLine(g, tl, tr);
        dottedLine(g, tl, bl);
        dottedLine(g, tr, br);
        dottedLine(g, bl, br);
    }

    public static void dot(Graphics g, Vector pos, float rad) {
        g.fillOval((int) (pos.x - rad), (int) (pos.y - rad), (int) (rad * 2), (int) (rad * 2));
    }

    public static void dottedLine(Graphics g, Vector from, Vector to, float dotLength, float midLength) {

        Vector diff = to.subbed(from);
        float angle = diff.angle(), mag = diff.mag();

        float progress = 0;

        do {
            drawLine(g, from.added(new Polar(progress, angle)), from.added(new Polar(Math.min(mag, progress + dotLength), angle)));

            progress += dotLength + midLength;
        } while (progress < mag);
    }

    public static void drawLine(Graphics g, Vector from, Vector to) {
        g.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
    }

    public static void fillScreen(Graphics g, Color color) {
        g.setColor(color);
        g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
    }
}
