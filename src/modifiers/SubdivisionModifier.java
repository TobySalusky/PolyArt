package modifiers;

import poly.Edge;
import poly.Polygon;
import ui.FuncButton;
import ui.IntTextBox;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;
import util.Vector;

public class SubdivisionModifier extends Modifier {

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

	@Override
	public ModifierTab createTab(ModifierPanel panel) {
		return new Panel(panel);
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
		}

		@Override
		protected float openHeight() {
			return 70;
		}

		@Override
		protected void resizeOpen(Vector pos, Vector size) {
			elements.get(openStart).resize(new Vector(pos.x, findTopLeft().y + 50), new Vector(size.x - 40, 20));
		}

		@Override
		protected String getName() {
			return "Subdivision";
		}
	}
}
