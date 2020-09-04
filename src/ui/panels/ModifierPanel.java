package ui.panels;

import modifiers.Modifier;
import poly.Polygon;
import screens.PolyScreen;
import ui.UIElement;
import ui.premade.ModifierTab;
import util.Vector;

public class ModifierPanel extends UIPanel {

	private Polygon polygon;
	private final PolyScreen screen;

	public ModifierPanel(boolean horizontal, float bindPos, float width, PolyScreen screen) {
		super(horizontal, bindPos, width);
		this.screen = screen;
	}

	public void tryApply(ModifierTab tab) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) == tab) {
				if (polygon.getModifiers().get(i).apply(polygon)) {
					polygon.getModifiers().remove(i);
				} else {
					screen.errorPopup("Modifier could not be applied");
				}
				break;
			}
		}
	}

	public void remove(ModifierTab tab) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i) == tab) {
				polygon.getModifiers().remove(i);
				break;
			}
		}
	}

	@Override
	protected void onDragChange() {
		super.onDragChange();
		float y = topLeft.y;
		for (int i = 0; i < elements.size(); i++) {
			UIElement element = elements.get(i);
			elements.get(i).resize(new Vector(findCenter().x, y + element.getHeight() * 0.5F), new Vector(Math.max(size.x * 0.5F, size.x - 10), element.getHeight()));
			y += element.getHeight();
		}
	}

	@Override
	public void update() {
		super.update();


		Polygon edit = screen.getEditPoly();
		if (edit != polygon || (edit != null && edit.getModifiers().size() != elements.size())) {
			initTo(edit);
		}
	}

	private void initTo(Polygon polygon) {
		this.polygon = polygon;
		elements.clear();

		if (polygon == null) return;
		for (Modifier modifier : polygon.getModifiers()) {
			elements.add(modifier.createTab(this));
		}
	}
}
