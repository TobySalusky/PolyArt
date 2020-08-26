package util;

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

}
