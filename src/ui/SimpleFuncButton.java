package ui;

import lambda.Func;
import perspective.Camera;
import util.Colors;
import util.Gizmo;
import util.Maths;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class SimpleFuncButton extends SizedButton {

	private final Func func;

	public SimpleFuncButton(Func func, float x, float y, float width, float height) {
		this.func = func;
		pos = new Vector(x, y);
		size = new Vector(width, height);
	}


	@Override
	protected void hitAction(Vector mousePos) {
		func.execute();
	}

	protected Color findColor() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		g.setColor(findColor());
		Gizmo.fillCenteredRect(g, pos, size);
		g.setColor(Colors.darkGrey);
		Gizmo.drawCenteredRect(g, pos, size);
	}
}
