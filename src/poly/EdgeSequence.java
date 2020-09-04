package poly;

import perspective.Camera;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class EdgeSequence extends Polygon { // TODO: click detection (selecting)

	public EdgeSequence(Color color) {
		super(color);
	}

	@Override
	public boolean pointInside(Vector vector) {
		for (Edge edge : genEdges()) {
			if (edge.distTo(vector) < 50) { // TODO: make dependant on camera view please
				return true;
			}
		}
		return false;
	}

	@Override
	public Edge[] genEdges() {
		Edge[] edges = new Edge[vertices.size() - 1];

		for (int i = 0; i < vertices.size() - 1; i++) {
			edges[i] = new Edge(vertices.get(i), vertices.get(i + 1));
		}

		return edges;
	}

	@Override
	protected void unmodifiedRender(Graphics g, Camera camera) {
		g.setColor(Color.BLACK);
		for (int i = 0; i < vertices.size() - 1; i ++) {
			Gizmo.drawLine(g, camera.toScreen(vertices.get(i)), camera.toScreen(vertices.get(i + 1)));
		}
	}

	public Polygon cloneGeom() { // clones polygon without modifiers
		Polygon copy = new EdgeSequence(getColor());

		for (Vector vert : vertices) {
			copy.addPoint(vert.copy());
		}

		return copy;
	}
}
