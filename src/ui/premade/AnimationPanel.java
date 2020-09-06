package ui.premade;

import animation.Animation;
import animation.KeyFrame;
import animation.PolyAnimation;
import main.Main;
import perspective.Camera;
import poly.Polygon;
import ui.panels.UIPanel;
import util.Colors;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.List;

public class AnimationPanel extends UIPanel {

	public Animation animation = new Animation();

	private Vector viewRange = new Vector(0, 10); // x as start of time view and y and end
	private boolean focused = false;

	public AnimationPanel(boolean horizontal, float bindPos, float width) {
		super(horizontal, bindPos, width);
	}

	@Override
	public void update() {
		animation.update(Main.deltaTime);
	}

	@Override
	protected void onClick(MouseEvent e, Vector mousePos) {
		float time = (mousePos.x - topLeft.x) / size.x * (viewRange.y - viewRange.x) + viewRange.x;
		animation.changeTime(time);
		focused = true;
	}

	public boolean inFocus() {
		return focused;
	}

	@Override
	public boolean mouseDown(MouseEvent e) {
		focused = false;
		return super.mouseDown(e);
	}

	@Override
	protected void renderSelf(Graphics g, Camera camera) {
		super.renderSelf(g, camera);

		g.setColor(Colors.background);
		float x = topLeft.x + (animation.getTime() - viewRange.x) / (viewRange.y - viewRange.x) * size.x;
		Gizmo.drawLine(g, new Vector(x, topLeft.y), new Vector(x, topLeft.y + size.y));

		g.setColor(Color.BLACK);
		List<PolyAnimation> polyAnims = animation.getPolyAnims();
		for (int i = 0; i < polyAnims.size(); i++) {
			inner:
			for (KeyFrame frame : polyAnims.get(i).getFrames()) {
				float frameTime = frame.getTime();
				if (frameTime >= viewRange.x) {
					if (frameTime > viewRange.y) {
						break inner;
					}
					Gizmo.dot(g, topLeft.added((frameTime - viewRange.x) / (viewRange.y - viewRange.x) * size.x, 20 * (i + 1)));
				}
			}
		}
	}
}
