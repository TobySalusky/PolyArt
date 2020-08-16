package util;

import java.util.List;

public class Vertices {

	public static final Vector average(List<Vector> verts) {
		Vector sum = new Vector();

		for (Vector vert : verts) {
			sum.add(vert);
		}

		sum.div(verts.size());
		return sum;
	}

}
