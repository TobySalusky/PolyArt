package poly;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import kotlin.jvm.Transient;
import main.Main;
import modifiers.Modifier;
import perspective.Camera;
import screens.PolyScreen;
import util.Gizmo;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class Polygon {

    private final List<Vector> vertices = new ArrayList<>(3);
    private Color color;

    private final List<Modifier> modifiers = new ArrayList<>(1);
    private Polygon[] lastOutput;

    @Transient
    private Edge[] storedEdges;

    public Polygon(Color color) {
        this.color = color;
    }

    public void insertPoint(Vector vertex, int index) {
        vertices.add(index, vertex);
        fixStoredEdges();
    }

    public void addPoint(Vector vertex) {
        vertices.add(vertex);
        fixStoredEdges();
    }

    public void removeAll(List<Vector> verts) {
        vertices.removeAll(verts);
        fixStoredEdges();
    }

    public Edge[] extrude(List<Edge> edges) {
        if (edges.size() == storedEdges.length) { // can not extrude all
            return new Edge[0];
        }

        Edge[] newEdges = new Edge[edges.size()];

        loop:
        for (int i = 0; i < edges.size(); i++) {
            boolean newStart = true, newEnd = true;
            Edge edge = edges.get(i);
            for (int j = 0; j < edges.size(); j++) {
                Edge other = edges.get(j);
                if (j != i) {
                    if (newStart && edge.getStart() == other.getEnd()) {
                        newStart = false;
                    }
                    if (newEnd && edge.getEnd() == other.getStart()) {
                        newEnd = false;
                    }
                }
                if (!newStart && !newEnd) {
                    newEdges[i] = edge;
                    continue loop;
                }
            }

            Vector start = edge.getStart();
            if (newStart) {
                start = start.copy();
                addToEdge(start, edge);
            }

            Vector end = edge.getEnd();
            if (newEnd) {
                end = end.copy();
                addToEdge(end, new Edge(start, edge.getEnd()));
            }

            newEdges[i] = new Edge(start, end);
        }

        updateEdgeRefs(newEdges);
        return newEdges;
    }

    private void updateEdgeRefs(Edge[] edges) {
        for (int i = 0; i < edges.length; i++) {
            Edge edge = edges[i];
            for (Edge store : getStoredEdges()) {
                if (edge.sameEdge(store)) {
                    edges[i] = store;
                    break;
                }
            }
        }
    }

    public void addPointsToEdge(Edge edge, List<Vector> points) {
        for (int i = 0; i < vertices.size(); i++) {

            if (edge.getStart() == vertices.get(i)) {
                for (int j = 0; j < points.size(); j++) {
                    insertPoint(points.get(j), i + 1 + j);
                }
                break;
            }
        }
        System.out.println("NO SUCH EDGE FOUND!!?");
    }

    public void addToEdge(Vector point, Edge edge) {
        for (int i = 0; i < vertices.size(); i++) {

            if (edge.getStart() == vertices.get(i)) {
                insertPoint(point, i + 1);
                return;
            }
        }
        System.out.println("NO SUCH EDGE FOUND");
    }

    public Vector[] findRange() { // returns top-left and bottom-right points of rectangle around polygon
        Vector min = vertices.get(0);
        Vector max = vertices.get(0);

        for (int i = 1; i < vertices.size(); i++) {
            Vector vert = vertices.get(i);

            min = Vector.minEach(min, vert);
            max = Vector.maxEach(max, vert);
        }

        return new Vector[] {min, max};
    }

    public Polygon unlinkedFullClone() { // deep copy via json
        Polygon copy = (Polygon) JsonReader.jsonToJava(JsonWriter.objectToJson(this));
        copy.offloadEdges();
        return copy;
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

    private void fixStoredEdges() { // should be calling if edge order/amount is ever changed
        if (storedEdges != null) {
            Edge[] newEdges = genEdges();

            for (int i = 0; i < newEdges.length; i++) { // rather inefficient
                for (Edge storedEdge : storedEdges) {
                    if (newEdges[i].sameEdge(storedEdge)) {
                        newEdges[i] = storedEdge;
                        break;
                    }
                }
            }
            storedEdges = newEdges;
        }
    }

    public void offloadEdges() {
        storedEdges = null;
    }

    public Edge[] getStoredEdges() {

        if (storedEdges == null) {
            storedEdges = genEdges();
        }
        return storedEdges;
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

    public void render(Graphics g, Camera camera) {
        g.setColor(color);

        if (modifiers.size() > 0) { // MODIFIER OUTPUT
            Polygon[] modified = new Polygon[]{cloneGeom()}; // clones geometry as to not get stuck in loop

            for (Modifier modifier : modifiers) {
                if (modifier.shouldChange(this)) {
                    modified = modifier.create(modified);
                }
            }

            for (Polygon polygon : modified) {
                //NOTE: if caught in infinite loop- you are probably copying this and having it render modifier output infinitely
                polygon.render(g, camera);
            }
            lastOutput = modified;
        } else {
            int[] xPoints = new int[vertices.size()];
            int[] yPoints = new int[xPoints.length];

            for (int i = 0; i < vertices.size(); i++) {
                Vector vert = vertices.get(i);
                xPoints[i] = (int) camera.getRenderX(vert.x);
                yPoints[i] = (int) camera.getRenderY(vert.y);
            }

            g.fillPolygon(xPoints, yPoints, xPoints.length);
        }
    }
    public void renderModifierGizmos(Graphics g, Camera camera) {
        if (modifiers.size() > 0) {
            for (Modifier modifier : modifiers) {
                modifier.render(g, camera, this);
            }
        }
    }


    public void renderEdges(Graphics g, Camera camera, Function<Edge, Color> colorFunction) {
        for (Edge edge : (storedEdges == null) ? genEdges() : storedEdges) {
            g.setColor(colorFunction.apply(edge));
            Gizmo.drawLine(g, camera.toScreen(edge.getStart()), camera.toScreen(edge.getEnd()));
        }
    }

    public void renderVerts(Graphics g, Camera camera, Function<Vector, Color> colorFunction) {
        for (Vector vert : vertices) {
            g.setColor(colorFunction.apply(vert));
            Gizmo.dot(g, camera.toScreen(vert));
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

    public Polygon[] getLastOutput() {
        return lastOutput;
    }
}
