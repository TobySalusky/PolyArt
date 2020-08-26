package ui;

import main.Main;
import perspective.Camera;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class UIContainer extends SizedButton {

	protected List<UIElement> elements = new ArrayList<>();

	public void update() {
		for (UIElement element : elements) {
			element.update();
		}
	}

	@Override
	public boolean mouseUp(MouseEvent e) {
		for (UIElement element : elements) {
			element.mouseUp(e);
		}
		return false;
	}

	@Override
	public void mouseAt(Vector pos) {
		for (UIElement element : elements) {
			element.mouseAt(pos);
		}
	}

	protected Color findColor() {
		return Color.LIGHT_GRAY;
	}

	protected void renderSelf(Graphics g, Camera camera) {
		g.setColor(findColor());

		int arc = (int) (size.x / 4);
		g.fillRoundRect((int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)(size.x), (int)(size.y), arc, arc);

	}

	@Override
	public boolean mouseDown(MouseEvent e) {

		Vector mousePos = Main.screen.mousePos(e);

		if (mousePos.between(pos.subbed(size.multed(0.5F)), pos.added(size.multed(0.5F)))) {
			for (int i = 0; i < elements.size(); i++) { // sus
				elements.get(i).mouseDown(e);
			}
			return true;
		}
		return false;
	}


	@Override
	public void render(Graphics g, Camera camera) {
		renderSelf(g, camera);

		for (UIElement element : elements) {
			element.render(g, camera);
		}
	}
}
