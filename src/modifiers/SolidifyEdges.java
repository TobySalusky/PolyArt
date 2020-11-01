package modifiers;

import main.Main;
import poly.EdgeSequence;
import poly.Polygon;
import ui.FuncButton;
import ui.IntTextBox;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;
import util.Maths;
import util.Polar;
import util.Vector;

import java.awt.Panel;
import java.util.List;

public class SolidifyEdges extends Modifier {

	private float width = 30F;
	private float[] widths;
	private Polygon weightPoly;

	@Override
	public Polygon[] create(Polygon[] polygons) {
		Polygon[] solids = new Polygon[polygons.length];

		for (int i = 0; i < polygons.length; i++) {
			solids[i] = solidify(polygons[i]);
		}

		return solids;
	}

	public Polygon solidify(Polygon polygon) {
		Polygon solid = new Polygon(polygon.getColor());

		List<Vector> verts = polygon.getVertices();
		if (verts.size() < 2) return solid;

		boolean wrap = !(polygon instanceof EdgeSequence);

		for (int i = 0; i < verts.size(); i++) {
			Vector point = verts.get(i);
			float angle;
			if (i == 0) {
				angle = verts.get(i + 1).subbed(point).angle();
				if (wrap) {
					float other = point.subbed(verts.get(verts.size() - 1)).angle();
					angle = Maths.correctAngle(angle, other);
					angle += other;
					angle /= 2;
				}
			} else if (i == verts.size() - 1) {
				angle = point.subbed(verts.get(i - 1)).angle();
				if (wrap) {
					float other = verts.get(0).subbed(point).angle();
					angle = Maths.correctAngle(angle, other);
					angle += other;
					angle /= 2;
				}
			} else {
				angle = point.subbed(verts.get(i - 1)).angle();
				float other = verts.get(i + 1).subbed(point).angle();
				angle = Maths.correctAngle(angle, other);
				angle += other;
				angle /= 2;
			}
			float half = (widths == null) ? width / 2 : widths[i];
			solid.insertPoint(point.added(new Polar(half, angle + Maths.HalfPI)), i);
			solid.insertPoint(point.added(new Polar(half, angle - Maths.HalfPI)), i + 1);
		}

		if (wrap) {
			verts = solid.getVertices();
			Vector newStart = verts.get(verts.size() / 2 - 1);
			Vector newEnd = verts.get(verts.size() / 2);

			solid.insertPoint(newStart, 0);
			solid.addPoint(newEnd);
		}

		return solid;
	}

	private void toggleWeights() {
		if (weightPoly == null) {

			Main.debugScreen().getLayer().getPolygons();
		} else {
			Main.debugScreen().getLayer().getPolygons().remove(weightPoly); // ?!!!!
			weightPoly = null;
		}
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
			elements.add(new IntTextBox.Func(i -> width = i, (int) width, 0, 0, 0, 0));
			elements.add(new FuncButton(SolidifyEdges.this::toggleWeights, 0, 0, 0, 0));

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
			return "Solidify";
		}
	}
}
