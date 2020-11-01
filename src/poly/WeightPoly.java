package poly;

import perspective.Camera;
import util.Colors;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class WeightPoly extends Polygon {

	private Polygon base;

	public WeightPoly(Polygon base) {
		super(Color.WHITE);
		this.base = base;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		super.render(g, camera);
		for (int i = 0; i < vertices.size(); i++) {
			Vector point = camera.toWorld(vertices.get(i));
			g.setColor(Colors.darkGrey);
			Gizmo.drawCenteredRect(g, point, camera.toWorld(base.vertices.get(i)));
			Gizmo.dot(g, point, Colors.errorText);
		}
	}
}
