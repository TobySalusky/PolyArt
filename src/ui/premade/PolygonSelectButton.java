package ui.premade;

import main.Main;
import perspective.Camera;
import poly.Polygon;
import screens.PolyScreen;
import ui.SizedButton;
import ui.panels.Panels;
import ui.panels.UIPanel;
import util.Colors;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;

public class PolygonSelectButton extends SizedButton {

	private boolean grab;

	private final Polygon polygon;
	private final Panels.PolySelectPanel panel;

	public PolygonSelectButton(Panels.PolySelectPanel panel, Polygon polygon) {
		resize(Vector.zero, Vector.one);
		this.panel = panel;
		this.polygon = polygon;
	}

	private PolyScreen findScreen() {
		return panel.getScreen();
	}

	@Override
	public boolean mouseDown(MouseEvent e) {
		if (on(Main.screen.mousePos(e))) {
			PolyScreen screen = findScreen();

			screen.setMode(PolyScreen.Mode.object);
			screen.setTool(PolyScreen.Tool.select);

			if (!e.isShiftDown()) {
				screen.getSelectedPolygons().clear();
			}
			screen.getSelectedPolygons().add(polygon);
			screen.setEditPoly(polygon);

			panel.setLayerGrabbed(this);
			grab = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseUp(MouseEvent e) {
		if (grab) {
			panel.setLayerGrabbed(null);
		}
		grab = false;
		return false;
	}

	public void resize(Vector pos, Vector size) {
		this.pos = pos;
		this.size = size;
	}

	@Override
	public void render(Graphics g, Camera camera) {
		Color back = (findScreen().getEditPoly() == polygon) ? Colors.editLayer : (findScreen().getSelectedPolygons().contains(polygon)) ? Colors.selectedLayer : Color.DARK_GRAY;
		g.setColor(back);
		Gizmo.fillCenteredRect(g, pos, size);
		g.setColor(Color.LIGHT_GRAY);
		Gizmo.drawCenteredRect(g, pos, size);

		Vector[] range = polygon.findRange();
		Vector diff = range[1].subbed(range[0]);
		Vector mid = range[0].added(diff.multed(0.5F));

		Vector pSize = size.multed(0.8F);
		float scale = Math.min(pSize.x / diff.x, pSize.y / diff.y);
		Camera fitView = new Camera(mid.x, mid.y, scale);
		fitView.setCenter(pos);
		polygon.cloneGeom().render(g, fitView, false, false); // poorly optimised - does this as to not display modifiers
	}

	public Vector getSize() {
		return size;
	}
}
