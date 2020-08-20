package ui;

import lambda.Func;
import perspective.Camera;
import util.Maths;
import util.Vector;

import java.awt.*;

public class FuncButton extends SizedButton {

	private final Vector defaultSize;
	private final Func func;

	protected boolean hover;

	public FuncButton(Func func, float x, float y) {
		this(func, x, y, 30, 30);
	}

	public FuncButton(Func func, float x, float y, float width, float height) {
		this.func = func;
		pos = new Vector(x, y);
		size = new Vector(width, height);
		defaultSize = new Vector(width, height);
	}

	@Override
	public void mouseAt(Vector pos) {
		hover = false;
		super.mouseAt(pos);
	}

	@Override
	protected void hover(Vector mousePos) {
		hover = true;
	}

	@Override
	public void update() {
		float newX = Maths.approach(size.x, (hover) ? defaultSize.x * 1.4F : defaultSize.x, 8F);
		float newY = Maths.approach(size.y, (hover) ? defaultSize.y * 1.4F : defaultSize.y, 8F);

		size = new Vector(newX, newY);
	}

	@Override
	protected void hitAction(Vector mousePos) {
		func.execute();
		size = defaultSize.multed(0.8F);
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
