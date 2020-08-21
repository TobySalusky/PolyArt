package poly;

import perspective.Camera;
import util.*;

import java.awt.*;

public class Axis {

	private Vector pivot;
	private float radians;

	public Axis(Vector pivot, float radians) {
		this.pivot = pivot;
		this.radians = radians;
	}

	public void render(Graphics g, Camera camera) {
		g.setColor(Colors.axisBlue);
		Gizmo.drawLine(g, camera.toScreen(pivot.added(new Polar(500, radians + Maths.PI))), camera.toScreen(pivot.added(new Polar(500, radians))));
		Gizmo.dot(g, camera.toScreen(pivot), Colors.axisPivot);
	}

	public Vector flip(Vector vector) {
		Vector flipped = vector.copy();

		flipped.rotateAround(-radians, pivot);

		flipped.y = -(flipped.y - pivot.y) + pivot.y;

		flipped.rotateAround(radians, pivot);

		return flipped;
	}
}
