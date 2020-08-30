package modifiers;

import perspective.Camera;
import poly.Axis;
import poly.Polygon;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MirrorModifier extends Modifier { // dunno if asList is safe... (shallow/deep copy?)

	private final List<Axis> axes;

	public MirrorModifier() {
		axes = new ArrayList<>();
	}

	public MirrorModifier(Axis... args) {
		axes = Arrays.asList(args);
	}

	@Override
	public void render(Graphics g, Camera camera, Polygon polygon) {
		for (Axis axis : axes) {
			axis.render(g, camera);
		}
	}

	private Polygon mirror(Polygon poly, Axis axis) {
		Polygon mirrored = new Polygon(poly.getColor());

		for (Vector vert : poly.getVertices()) {
			mirrored.addPoint(axis.flip(vert));
		}

		return mirrored;
	}

	public Axis[] snappingAxes(Polygon polygon) {
		return axes.toArray(new Axis[0]);
	}

	@Override
	public Polygon[] create(Polygon[] polygons) {

		List<Polygon> toMirror = new ArrayList<>(Arrays.asList(polygons));

		for (Axis axis : axes) {

			Polygon[] newPoly = new Polygon[toMirror.size()];

			for (int i = 0; i < toMirror.size(); i++) {
				newPoly[i] = mirror(toMirror.get(i), axis);
			}
			toMirror.addAll(Arrays.asList(newPoly));
		}

		return toMirror.toArray(new Polygon[0]);
	}

	@Override
	public ModifierTab createTab(ModifierPanel panel) {
		return new Panel(panel);
	}

	private static class Panel extends ModifierTab {
		public Panel(ModifierPanel panel) {
			super(panel);
		}

		@Override
		protected String getName() {
			return "Mirror";
		}
	}
}
