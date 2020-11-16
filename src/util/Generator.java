package util;

import poly.EdgeSequence;
import poly.Polygon;

import java.awt.Color;

public class Generator {

	public static Polygon genSquare(Vector mid, float size) {
		size /= 2;
		Polygon square = new Polygon(Color.WHITE);
		square.addPoint(mid.added(Vector.one.multed2(-size,-size)));
		square.addPoint(mid.added(Vector.one.multed2( size,-size)));
		square.addPoint(mid.added(Vector.one.multed2( size, size)));
		square.addPoint(mid.added(Vector.one.multed2(-size, size)));
		return square;
	}

	public static Polygon genScreenRect() {
		Polygon square = new Polygon(Color.WHITE);
		square.addPoint(new Vector(0, 0));
		square.addPoint(new Vector(1980, 0));
		square.addPoint(new Vector(1980, 1080));
		square.addPoint(new Vector(0, 1080));
		return square;
	}

	public static Polygon genEdgePoly(Vector mid, float size) {
		Polygon edge = new EdgeSequence(Color.WHITE);
		edge.addPoint(mid.added(Vector.up.multed(size *  0.5F)));
		edge.addPoint(mid.added(Vector.up.multed(size * -0.5F)));
		return edge;
	}

	public static Polygon genNGon(Vector mid, float radiusSize, int n) {
		Polygon nGon = new Polygon(Color.WHITE);

		for (int i = 0; i < n; i++) {
			float angle = Maths.TwoPI * ((float)n / i);

			nGon.getVertices().add(new Polar(radiusSize, angle));
		}

		return nGon;
	}
}
