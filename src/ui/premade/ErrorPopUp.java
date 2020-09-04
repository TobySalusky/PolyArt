package ui.premade;

import main.Main;
import perspective.Camera;
import ui.SizedButton;
import util.Colors;
import util.Gizmo;
import util.Vector;

import java.awt.Graphics;

public class ErrorPopUp extends SizedButton {

	private final String errorText;
	private int ticksLeft = 300;
	private boolean delete;

	public ErrorPopUp(String errorText) {
		this.errorText = errorText;
		pos = Main.SCREEN_CENTER;
		size = new Vector(400, 250);
	}

	@Override
	public boolean shouldRemove() {
		return delete;
	}

	@Override
	public void update() {
		ticksLeft--;

		if (ticksLeft <= 0) {
			delete = true;
		}
	}

	@Override
	protected void hitAction(Vector mousePos) {
		delete = true;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		g.setColor(Colors.darkGrey);
		Gizmo.fillCenteredRect(g, pos, size);
		g.setColor(Colors.errorText);
		Vector stringPos = pos.subbed(size.multed(0.5F)).added(10, 50);
		g.drawString(errorText, (int)stringPos.x, (int)stringPos.y);
	}
}
