package modifiers;

import main.Main;
import perspective.Camera;
import poly.Polygon;
import screens.PolyScreen;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;

import java.awt.*;

public abstract class Modifier {

	private boolean visible = true, showEdit = true;

	public void apply(Polygon polygon) {
		System.out.println("DEBUG: " + getClass().toString() + "'s application functionality is yet to be implemented...");
	}

	public void render(Graphics g, Camera camera, Polygon polygon) {} // render extras

	public boolean shouldChange(Polygon polygon) {
		if (visible) {
			if (((PolyScreen)Main.screen).editMode()) {
				return showEdit;
			}
			return true;
		}
		return false;
	}

	public abstract Polygon[] create(Polygon[] polygon);

	public abstract ModifierTab createTab(ModifierPanel panel);

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public boolean isShowEdit() {
		return showEdit;
	}

	public void setShowEdit(boolean showEdit) {
		this.showEdit = showEdit;
	}
}
