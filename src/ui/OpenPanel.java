package ui;

import main.Main;
import perspective.Camera;
import poly.Edge;
import util.Gizmo;
import util.Maths;

import java.awt.*;
import java.awt.event.MouseEvent;

public class OpenPanel extends UIPanel {
	public OpenPanel(boolean horizontal, float bindPos, float width) {
		super(horizontal, bindPos, width);
	}

	@Override
	public void render(Graphics g, Camera camera) {
		renderEdge(g, camera);
	}

	@Override
	public boolean mouseDown(MouseEvent e) {

		if (edgeCollision(Main.screen.mousePos(e))) {
			edgeGrabbed = true;
			return true;
		}

		return false;
	}

	@Override
	protected float clamp(float amount) {
		return Maths.clamp(amount, 10, Main.WIDTH - 100);
	}
}
