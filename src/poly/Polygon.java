package poly;

import main.Main;
import modifiers.Modifier;
import perspective.Camera;
import screens.PolyScreen;
import util.Gizmo;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private final List<Vector> vertices = new ArrayList<>(3);
    private Color color;

    private final List<Modifier> modifiers = new ArrayList<>(1);

    public Polygon(Color color) {
        this.color = color;
    }

    public void insertPoint(Vector vertex, int index) {
        vertices.add(index, vertex);
    }

    public void addPoint(Vector vertex) {
        vertices.add(vertex);
    }

    public void removeAll(List<Vector> verts) {
        vertices.removeAll(verts);
    }

    public void addToEdge(Vector point, Edge edge) {
        for (int i = 0; i < vertices.size(); i++) {

            if (edge.getStart() == vertices.get(i)) {
                insertPoint(point, i + 1);
                break;
            }
        }
    }

    public Polygon cloneGeom() { // clones polygon without modifiers
        Polygon copy = new Polygon(color);

        for (Vector vert : vertices) {
            copy.addPoint(vert.copy());
        }

        return copy;
    }

    public boolean pointInside(Vector vector) {
        Edge[] edges = genEdges();

        int left = 0;
        for (Edge edge : edges) {
            if (edge.yInRange(vector.y) && edge.xAtY(vector.y) < vector.x) {
                left++;
            }
        }

        return left % 2 == 1;
    }

    public Edge[] genEdges() {

        Edge[] edges = new Edge[vertices.size()];

        for (int i = 0; i < vertices.size() - 1; i++) {
            edges[i] = new Edge(vertices.get(i), vertices.get(i + 1));
        }
        edges[edges.length - 1] = new Edge(vertices.get(vertices.size() - 1), vertices.get(0));

        return edges;
    }

    public void addModifier(Modifier modifier) {
        modifiers.add(modifier);
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }

    public void render(Graphics g, Camera camera, boolean renderEdges, boolean renderVerts) {
        g.setColor(color);

        int[] xPoints = new int[vertices.size()];
        int[] yPoints = new int[xPoints.length];

        for (int i = 0; i < vertices.size(); i++) {
            Vector vert = vertices.get(i);
            xPoints[i] = (int) camera.getRenderX(vert.x);
            yPoints[i] = (int) camera.getRenderY(vert.y);
        }

        if (modifiers.size() > 0) { // MODIFIER OUTPUT
            Polygon[] modified = new Polygon[]{this};

            for (Modifier modifier : modifiers) {
                modified = modifier.create(modified);
            }

            for (Polygon polygon : modified) {
                //if (polygon != this) { NOTE: if caught in infinite loop- you are probably copying this and having it render modifier output infinitely
                polygon.render(g, camera, false, false);
                //}
            }
        } else {
            g.fillPolygon(xPoints, yPoints, xPoints.length);
        }

        if (renderEdges) {
            g.setColor(Color.ORANGE);
            g.drawPolygon(xPoints, yPoints, xPoints.length);
        }

        if (renderVerts) {
            for (int i = 0; i < xPoints.length; i++) { // TODO

                float progress = (float) (i + 1) / xPoints.length;
                //g.setColor(new Color((int) (255 * progress), 0, (int) (255 * (1 - progress))));
                g.setColor(((PolyScreen) Main.screen).vertSelected(vertices.get(i)) ? Gizmo.midOrange : Gizmo.nearBlack);
                Gizmo.dot(g, new Vector(xPoints[i], yPoints[i]), 4);
            }
        }

        if (modifiers.size() > 0) {
            for (Modifier modifier : modifiers) { // TODO: give own category
                modifier.render(g, camera, this);
            }
        }
    }

    public boolean insideRange(Vector from, Vector to) {
        for (Vector vert : vertices) {
            if (!vert.between(from, to)) { // not very efficient, finds tl and br each iteration...
                return false;
            }
        }
        return true;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public List<Vector> getVertices() {
        return vertices;
    }
}
