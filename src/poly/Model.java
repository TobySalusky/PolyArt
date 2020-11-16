package poly;

import perspective.Camera;
import util.VecRect;
import util.Vector;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Model {

	public List<Polygon> polygons;

	public Model() {
		polygons = new ArrayList<>();
	}

	public Model(List<Polygon> polygons) {
		this.polygons = polygons;
	}

	public Model copy() {
		ArrayList<Polygon> copies = new ArrayList<>();
		polygons.forEach(p -> copies.add(p.cloneGeom()));

		return new Model(copies);
	}

	public void recenter() {
		Vector[] range = findRange();

		Vector mid = range[0].added(range[1]).multed(0.5F);

		polygons.forEach(p -> p.getVertices().forEach(v -> v.sub(mid)));
	}

	public Vector[] findRange() {
		Vector[] init = polygons.get(0).findRange();
		Vector tl = init[0], br = init[1];

		for (Polygon poly : polygons) {
			Vector[] range = poly.findRange();
			tl = Vector.minEach(tl, range[0]);
			br = Vector.maxEach(br, range[1]);
		}

		return new Vector[]{tl, br};
	}

	public VecRect findRect() {
		Vector[] range = findRange();
		return new VecRect(range[0].added(range[1]).multed(0.5F), range[1].subbed(range[0]));
	}

	public boolean pointCollision(Vector point) {

		for (Polygon polygon : polygons) {
			if (polygon.pointInside(point)) {
				return true;
			}
		}

		return false;
	}

	public void render(Graphics g, Camera camera) {
		polygons.forEach(p -> p.render(g, camera));
	}
}
