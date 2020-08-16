package poly;

import perspective.Camera;
import util.Gizmo;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Polygon {

    private final List<Vector> vertices = new ArrayList<>(3);
    private Color color;

    public Polygon(Color color) {
        this.color = color;
    }

    public void insertPoint(Vector vertex, int index) {
        vertices.add(index, vertex);
    }

    public void addPoint(Vector vertex) {
        vertices.add(vertex);
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

    private Edge[] genEdges() {

        Edge[] edges = new Edge[vertices.size()];

        for (int i = 0; i < vertices.size() - 1; i++) {
            edges[i] = new Edge(vertices.get(i), vertices.get(i + 1));
        }
        edges[edges.length - 1] = new Edge(vertices.get(vertices.size() - 1), vertices.get(0));

        return edges;
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

        g.fillPolygon(xPoints, yPoints, xPoints.length);

        if (renderEdges) {
            g.setColor(Color.ORANGE);
            g.drawPolygon(xPoints, yPoints, xPoints.length);
        }

        if (renderVerts) {
            for (int i = 0; i < xPoints.length; i++) { // TODO

                float progress = (float) (i + 1) / xPoints.length;
                g.setColor(new Color((int) (255 * progress), 0, (int) (255 * (1 - progress))));
                Gizmo.dot(g, new Vector(xPoints[i], yPoints[i]), 4);
            }
        }
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
