package ui;

import util.Vector;

public abstract class SizedButton implements UIElement {

	protected Vector pos, size;

	public boolean on(Vector mousePos) {
		if (mousePos.x >= pos.x - size.x / 2 && mousePos.x <= pos.x + size.x / 2) {
			return (mousePos.y >= pos.y - size.y / 2 && mousePos.y <= pos.y + size.y / 2);
		}
		return false;
	}

	protected void hitAction(Vector mousePos) {

	}

	protected void hover(Vector mousePos) {

	}

	@Override
	public boolean mouseDown(Vector pos) {
		if (on(pos)) {
			hitAction(pos);
			return true;
		}
		return false;
	}

	@Override
	public void mouseAt(Vector pos) {
		if (on(pos)) {
			hover(pos);
		}
	}

	public Vector findTopLeft() {
		return pos.subbed(size.multed(0.5F));
	}

	@Override
	public float getHeight() {
		return size.y;
	}

	@Override
	public void resize(Vector pos, Vector size) {
		this.pos = pos;
		this.size = size;
	}
}
