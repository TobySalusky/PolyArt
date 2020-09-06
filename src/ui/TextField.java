package ui;

import lambda.Func;
import perspective.Camera;
import util.Colors;
import util.Fonts;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class TextField extends SizedButton {

	protected String text;

	public TextField(String text, float x, float y, float width, float height) {
		pos = new Vector(x, y);
		size = new Vector(width, height);
		this.text = text;
	}

	@Override
	protected void hitAction(Vector mousePos) {
		super.hitAction(mousePos);
	}

	protected Color findColor() {
		return Colors.darkGrey;
	}

	public void render(Graphics g, Camera camera) {
		g.setFont(Fonts.textBox);
		g.setColor(findColor());
		g.fillRect((int)(pos.x - size.x / 2), (int)(pos.y - size.y / 2), (int)(size.x), (int)(size.y));
		g.setColor(Color.LIGHT_GRAY);
		g.drawString(text, (int)(pos.x - size.x / 2), (int)(pos.y + size.y / 4));
	}

	public void changeTo(String str) {
		text = str;
	}
}
