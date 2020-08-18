package ui;

import screens.PolyScreen;

public class ToolButton extends SelectFuncButton {

	public ToolButton(PolyScreen.Tool tool, PolyScreen screen, float x, float y) {
		super(() -> screen.setTool(tool), () -> screen.using(tool), x, y);
	}
}
