package ui;

import lambda.Func;
import perspective.Camera;
import util.Maths;
import util.Vector;

import java.awt.*;

public class FuncButton extends SizedButton {

	private static final Vector defaultSize = new Vector(30, 30);
	private final Func func;

	private boolean hover;

	public FuncButton(Func func, float x, float y) {
		this.func = func;
		pos = new Vector(x, y);
		size = defaultSize.copy();
	}

	@Override
	protected void hover(Vector mousePos) {
		hover = true;
	}

	@Override
	public void update() {
		float newSize = Maths.approach(size.x, (hover) ? defaultSize.x * 1.4F : defaultSize.x, 5);
		size = new Vector(newSize, newSize);

		hover = false;
	}

	@Override
	protected void hitAction(Vector mousePos) {
		func.execute();

		float newSize = defaultSize.x * 0.8F;
		size = new Vector(newSize, newSize);
	}

	protected Color findColor() {
		return Color.LIGHT_GRAY;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		g.setColor(findColor());

		int arc = (int) (size.x / 4);
		g.fillRoundRect((int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)(size.x), (int)(size.y), arc, arc);
	}
}
