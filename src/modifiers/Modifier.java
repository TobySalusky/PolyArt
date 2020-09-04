package modifiers;

import main.Main;
import perspective.Camera;
import poly.Axis;
import poly.Polygon;
import screens.PolyScreen;
import ui.panels.ModifierPanel;
import ui.premade.ModifierTab;

import java.awt.*;

public abstract class Modifier {

	private boolean visible = true, showEdit = true;

	public boolean apply(Polygon polygon) {
		Polygon[] output = create(new Polygon[]{polygon});

		if (output.length == 1) {
			polygon.copyGeom(output[0]);
			return true;
		}
		return false;
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

	public Axis[] snappingAxes(Polygon polygon) {
		return null;
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
