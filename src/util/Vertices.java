package util;

import java.util.List;

public class Vertices {

	public static final Vector center(List<Vector> verts) {
		Vector min = verts.get(0);
		Vector max = verts.get(0);

		for (int i = 1; i < verts.size(); i++) {
			Vector vert = verts.get(i);

			min = Vector.minEach(min, vert);
			max = Vector.maxEach(max, vert);
		}

		return min.added(max.subbed(min).multed(0.5F));

	}

	public static final Vector average(List<Vector> verts) {
		Vector sum = new Vector();

		for (Vector vert : verts) {
			sum.add(vert);
		}

		sum.div(verts.size());
		return sum;
	}

}
