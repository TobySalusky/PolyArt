package ui;

import lambda.Func;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class IntTextBox extends TextBox {

	public IntTextBox(int startInt, float x, float y, float width, float height) {
		super("" + startInt, x, y, width, height);
	}

	@Override
	protected boolean untypedKey(KeyEvent e) {
		char key = e.getKeyChar();
		if (key < '0' || key > '9') {
			return true;
		}
		return super.untypedKey(e);
	}

	protected boolean canEmpty() {
		return false;
	}

	protected int parseInt() {
		return Integer.parseInt(text);
	}

	public static class Func extends IntTextBox {

		private final Consumer<Integer> func;

		public Func(Consumer<Integer> func, int startInt, float x, float y, float width, float height) {
			super(startInt, x, y, width, height);
			this.func = func;
		}

		protected int clamp(int val) {
			return val;
		}

		@Override
		protected void enterAction() {
			super.enterAction();
			int clamped = clamp(parseInt());
			if (parseInt() != clamped) {
				text = "" + clamped;
				textChanged();
			}
		}

		@Override
		protected void textChanged() {
			if (text.length() > 0) {
				func.accept(clamp(parseInt()));
			}
		}
	}
}
