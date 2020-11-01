package transformation;

import poly.Polygon;
import util.Vector;

import java.util.List;

public abstract class Transform {

	public abstract void apply(Vector vert);
	public abstract Transform reverse();

	public void apply(List<Vector> verts) {
		for (Vector vert : verts) {
			apply(vert);
		}
	}

	public void remove(Vector vert) {
		reverse().apply(vert);
	}

	public void remove(List<Vector> verts) {
		reverse().apply(verts);
	}

	public Polygon appliedTo(Polygon polygon) {
		Polygon poly = polygon.cloneGeom();
		apply(poly.getVertices());
		return poly;
	}

}
