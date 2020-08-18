package ui;

import screens.PolyScreen;

public class ModeButton extends SelectFuncButton {

	public ModeButton(PolyScreen.Mode mode, PolyScreen screen, float x, float y) {
		super(() -> screen.setMode(mode), () -> screen.getMode() == mode, x, y);
	}
}
