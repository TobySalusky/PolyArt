package modifiers;

import poly.Edge;
import poly.Polygon;
import ui.FuncButton;
import ui.IntTextBox;
import ui.TextField;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;
import util.Vector;

import java.util.List;

public class SubdivisionModifier extends Modifier {

	private int times;
	private SubType type = SubType.spline;

	private enum SubType {
		simple, spline
	}

	public SubdivisionModifier(int times) {
		this.times = times;
	}

	private Vector bezierPoint(float t, Vector c1, Vector p1, Vector p2, Vector c2) {
		float tt = t*t;
		float ttt = tt * t;

		float q1 = -ttt + 2F * tt - t;
		float q2 = 3F * ttt - 5F * tt + 2F;
		float q3 = -3F * ttt + 4F * tt + t;
		float q4 = ttt - tt;

		return c1.multed(q1).added(p1.multed(q2)).added(p2.multed(q3)).added(c2.multed(q4)).multed(0.5F);
	}

	private Polygon subDivide(Polygon poly) {
		Polygon sub = new Polygon(poly.getColor());

		for (Edge edge : poly.genEdges()) {
			sub.addPoint(edge.progressPoint(1 / 3F));
			sub.addPoint(edge.progressPoint(2 / 3F));
		}

		return sub;
	}

	private Polygon bezierDivide(Polygon polygon, int points) {
		Polygon sub = new Polygon(polygon.getColor());

		List<Vector> verts = polygon.getVertices();
		for (int i = 0; i < verts.size(); i++) {
			sub.addPoint(verts.get(i));
			for (int rep = 0; rep < points; rep++) {
				float amount = (rep + 1F) / (points + 1F);
				sub.addPoint(bezierPoint(amount,wrappedGet(polygon, i - 1),verts.get(i),wrappedGet(polygon, i+1),wrappedGet(polygon, i+2)));
			}
		}
		return sub;
	}

	private Vector wrappedGet(Polygon polygon, int i) {
		int size = polygon.getVertices().size();
		return (i < 0) ? polygon.getVertices().get(size + i) : polygon.getVertices().get(i % size); // will crash with large negatives TODO: fix
	}

	private Polygon[] splineCreate(Polygon[] polygons) {
		Polygon[] sub = new Polygon[polygons.length];

		for (int i = 0; i < polygons.length; i++) {
			sub[i] = bezierDivide(polygons[i], times);
		}

		return sub;
	}

		@Override
	public Polygon[] create(Polygon[] polygons) {

		if (type == SubType.spline) return splineCreate(polygons);

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

	@Override
	public ModifierTab createTab(ModifierPanel panel) {
		return new Panel(panel);
	}

	private void shiftType() {
		type = SubType.values()[(type.ordinal() + 1) % SubType.values().length];
	}

	private class Panel extends ModifierTab {
		public Panel(ModifierPanel panel) {
			super(panel);
		}

		@Override
		protected void onOpen() {
			elements.add(new IntTextBox.Func((i) -> times = i, times, 0, 0, 0, 0) {
				@Override
				protected int clamp(int val) {
					return Math.min(10, val);
				}
			});
			TextField typeName = new TextField(type.name(), 0, 0, 0, 0);
			elements.add(typeName);
			elements.add(new FuncButton(() -> {
				shiftType();
				typeName.changeTo(type.name());
			}, 0, 0, 0, 0));
		}

		@Override
		protected float openHeight() {
			return 100;
		}

		@Override
		protected void resizeOpen(Vector pos, Vector size) {
			elements.get(openStart).resize(new Vector(pos.x, findTopLeft().y + 50), new Vector(size.x - 40, 20));
			elements.get(openStart + 1).resize(new Vector(pos.x + 20, findTopLeft().y + 70), new Vector(size.x - 60, 20));
			elements.get(openStart + 2).resize(findTopLeft().added(40, 70), new Vector(15, 15));
		}

		@Override
		protected String getName() {
			return "Subdivision";
		}
	}
}
