package ui;

import main.Main;
import perspective.Camera;
import util.Fonts;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TextBox extends TextField {

	int indexAt = 0;
	String lastText;

	public TextBox(String text, float x, float y, float width, float height) {
		super(text, x, y, width, height);
	}

	@Override
	protected void hitAction(Vector mousePos) {
		super.hitAction(mousePos);
		Main.typingIn = this;
		indexAt = text.length();
		lastText = text;
	}

	@Override
	public boolean mouseDown(MouseEvent e) {
		boolean result = super.mouseDown(e);
		if (!result && Main.typingIn == this) {
			clickOff();
		}
		return result;
	}

	private boolean specialKeys(KeyEvent e) { // returns true if special key is pressed
		int code = e.getKeyCode();

		switch (code) {
			case KeyEvent.VK_ESCAPE:
				escapeAction();
				return true;

			case KeyEvent.VK_ENTER:
				enterAction();
				return true;

			case KeyEvent.VK_BACK_SPACE:
				if (indexAt != 0) {
					if (e.isControlDown()) {
						boolean cont = true;
						int it = 0;
						while (indexAt != 0 && cont) {
							cont = text.charAt(indexAt - 1) != ' ' || it == 0;
							it++;
							removeChar(indexAt - 1);
							indexAt--;
						}
					} else {
						removeChar(indexAt - 1);
						indexAt--;
					}
					textChanged();
				}
				return true;

			case KeyEvent.VK_DELETE:
				if (indexAt != text.length()) {
					if (e.isControlDown()) {
						boolean cont = true;
						int it = 0;
						while (indexAt != text.length() && cont) {
							cont = text.charAt(indexAt) != ' ' || it == 0;
							it++;
							removeChar(indexAt);
						}
					} else {
						removeChar(indexAt);
					}
					textChanged();
				}
				return true;

			case KeyEvent.VK_LEFT:
			indexAt = Math.max(0, indexAt - 1);
				return true;

			case KeyEvent.VK_RIGHT:
				indexAt = Math.min(text.length(), indexAt + 1);
				return true;


			default:
				return false;
		}
	}

	protected boolean untypedKey(KeyEvent e) {
		int code = e.getKeyCode();

		switch (code) {
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_CONTROL:
			case KeyEvent.VK_TAB:
				return true;

			default:
				return false;
		}
	}

	private void addChar(char character, int index) {
		text = text.substring(0, index) + character + text.substring(index);
	}

	private void removeChar(int index) {
		text = text.substring(0, index) + text.substring(index + 1);
	}

	protected void clickOff() {
		enterAction();
	}

	protected void escapeAction() {
		Main.typingIn = null;
		text = lastText;
		textChanged();
		lastText = null;
	}

	protected boolean canEmpty() {
		return true;
	}

	protected void enterAction() {
		if (text.equals("") && !canEmpty()) {
			text = lastText;
			textChanged();
		}

		Main.typingIn = null;
		lastText = null;
	}

	private void charTyped(KeyEvent e) {
		addChar(e.getKeyChar(), indexAt);
		indexAt++;
		textChanged();
	}

	protected void textChanged() {

	}

	public void keyPressed(KeyEvent e) {
		if (!specialKeys(e) && !untypedKey(e)) {
			charTyped(e);
		}
	}

	@Override
	public void render(Graphics g, Camera camera) {
		boolean typing = this == Main.typingIn;
		if (typing) {
			g.setColor(Color.LIGHT_GRAY);
			int border = 1;
			g.fillRect((int) (pos.x - size.x / 2 - border), (int) (pos.y - size.y / 2 - border), (int) (size.x + border * 2), (int) (size.y + border * 2));
		}
		super.render(g, camera);

		if (typing) {
			float x = pos.x - size.x / 2 + Fonts.fontSize(text.substring(0, indexAt), g).x;
			Gizmo.drawLine(g, new Vector(x, pos.y - size.y * 0.4F), new Vector(x, pos.y + size.y * 0.4F));
		}
	}
}
