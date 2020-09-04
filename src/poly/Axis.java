package poly;

import perspective.Camera;
import util.*;

import java.awt.*;

public class Axis {

	private Vector pivot;
	private float radians;

	public static final Axis up = new Axis(Vector.zero, Maths.HalfPI), right = new Axis(Vector.zero, 0);

	public Axis(Vector pivot, float radians) {
		this.pivot = pivot;
		this.radians = radians;
	}

	public Vector normVec() {
		return new Polar(1, radians);
	}

	public void render(Graphics g, Camera camera) {
		g.setColor(Colors.axisBlue);
		Gizmo.drawLine(g, camera.toScreen(pivot.added(new Polar(500, radians + Maths.PI))), camera.toScreen(pivot.added(new Polar(500, radians))));
		Gizmo.dot(g, camera.toScreen(pivot), Colors.axisPivot);
	}

	public boolean onAxis(Vector vector) {
		return offsetFrom(vector).mag() < 0.1F; // TODO: perhaps add range
	}

	public Vector offsetFrom(Vector vector) {
		Vector off = vector.copy();

		off.rotateAround(-radians, pivot);

		off.x = 0;
		off.y = off.y - pivot.y;
		off.rotate(radians);

		return off;
	}

	public Vector flip(Vector vector) {
		Vector flipped = vector.copy();

		flipped.rotateAround(-radians, pivot);

		flipped.y = -(flipped.y - pivot.y) + pivot.y;

		flipped.rotateAround(radians, pivot);

		return flipped;
	}
}
