package ui.premade;

import perspective.Camera;
import ui.FuncButton;
import ui.TextField;
import ui.UIContainer;
import ui.panels.ModifierPanel;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class ModifierTab extends UIContainer {

	private boolean open;
	protected static final int openStart = 3;

	public ModifierTab(ModifierPanel panel) {
		pos = Vector.zero;
		size = Vector.one.multed(100);
		elements.add(new FuncButton(this::toggleOpen, 0, 0, 0, 0));
		elements.add(new TextField(getName(), 0, 0, 0, 0));
		elements.add(new FuncButton(() -> panel.remove(this), 0, 0, 0, 0));
	}

	public final float getHeight() {
		return (open) ? openHeight() : 40;
	}

	protected float openHeight() {
		return 60;
	}

	@Override
	protected void renderSelf(Graphics g, Camera camera) {
		g.setColor(Color.LIGHT_GRAY);
		Gizmo.drawCenteredRect(g, pos, size);
	}

	private void onClose() {
		elements = elements.subList(0, openStart);
	}

	protected void onOpen() {

	}

	private void toggleOpen() {

		if (open) {
			onClose();
		} else {
			onOpen();
		}

		open = !open;
	}

	@Override
	public void resize(Vector pos, Vector size) {
		super.resize(pos, size);
		elements.get(0).resize(findTopLeft().added(20, 20), new Vector(15, 15));
		elements.get(1).resize(pos.subbed(-20, size.y / 2 - 20), new Vector(size.x - 40, 30));
		elements.get(2).resize(findTopLeft().added(size.x - 12, 20), new Vector(15, 15));

		if (open) {
			resizeOpen(pos, size);
		}
	}

	protected void resizeOpen(Vector pos, Vector size) {

	}

	protected String getName() {
		return "Nameless Modifier";
	}
}
