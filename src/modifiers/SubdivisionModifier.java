package modifiers;

import poly.Edge;
import poly.Polygon;

public class SubdivisionModifier implements Modifier {

	private int times;

	public SubdivisionModifier(int times) {
		this.times = times;
	}

	private Polygon subDivide(Polygon poly) {
		Polygon sub = new Polygon(poly.getColor());

		for (Edge edge : poly.genEdges()) {
			sub.addPoint(edge.progressPoint(1 / 3F));
			sub.addPoint(edge.progressPoint(2 / 3F));
		}

		return sub;
	}

	@Override
	public Polygon[] create(Polygon[] polygons) {

		Polygon[] sub = new Polygon[polygons.length];

		for (int i = 0; i < polygons.length; i++) {
			Polygon div = polygons[i];

			for (int j = 0; j < times; j++) {
				div = subDivide(div);
			}

			sub[i] = div;
		}

		return sub;
	}
}
