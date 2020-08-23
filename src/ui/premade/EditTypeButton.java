package ui.premade;

import screens.PolyScreen;
import ui.SelectFuncButton;

public class EditTypeButton extends SelectFuncButton {

	public EditTypeButton(PolyScreen.EditType editType, PolyScreen screen, float x, float y) {
		super(() -> screen.setEditType(editType), () -> screen.getEditType() == editType, x, y);
	}
}
