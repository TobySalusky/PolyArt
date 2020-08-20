package modifiers;

import perspective.Camera;
import poly.Axis;
import poly.Polygon;
import util.Vector;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MirrorModifier implements Modifier { // dunno if asList is safe... (shallow/deep copy?)

	private final List<Axis> axes;

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

	@Override
	public Polygon[] create(Polygon[] polygons) {

		List<Polygon> toMirror = new ArrayList<>(Arrays.asList(polygons));

		for (int i = 0; i < toMirror.size(); i++) {
			toMirror.set(i, toMirror.get(i).cloneGeom());
		}

		for (Axis axis : axes) {

			Polygon[] newPoly = new Polygon[toMirror.size()];

			for (int i = 0; i < toMirror.size(); i++) {
				newPoly[i] = mirror(toMirror.get(i), axis);
			}
			toMirror.addAll(Arrays.asList(newPoly));
		}

		return toMirror.toArray(new Polygon[0]);
	}
}
