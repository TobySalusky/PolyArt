package ui.panels;

import main.Main;
import perspective.Camera;
import poly.Edge;
import ui.UIElement;
import util.Colors;
import util.Gizmo;
import util.Maths;
import util.Vector;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class UIPanel implements UIElement {

	public static final boolean HORIZONTAL = true, VERTICAL = false;

	protected final boolean horizontal;
	protected Vector topLeft, size;
	protected float bindPos;
	protected final int bindSign;

	protected boolean edgeGrabbed = false;

	protected List<UIElement> elements = new ArrayList<>();

	public UIPanel(boolean horizontal, float bindPos, float width) {
		this.horizontal = horizontal;
		this.bindPos = bindPos;
		bindSign = (width >= 0) ? 1 : -1;

		if (horizontal) { // sus
			topLeft = new Vector(bindPos, 0);
			size = new Vector(width, Main.HEIGHT);
		} else {
			topLeft = new Vector(0, bindPos);
			size = new Vector(Main.WIDTH, width);
		}

		topLeft = new Vector(Math.min(topLeft.x, topLeft.x + size.x), Math.min(topLeft.y, topLeft.y + size.y));
		size = size.multed2(Math.signum(size.x), Math.signum(size.y));
	}

	public Vector findCenter() {
		return topLeft.added(size.multed(0.5F));
	}

	@Override
	public void update() {
		for (UIElement element : elements) {
			element.update();
		}
	}

	protected void renderSelf(Graphics g, Camera camera) {
		g.setColor(Colors.darkGrey);
		g.fillRect((int) topLeft.x, (int) topLeft.y, (int) size.x, (int) size.y);

		if (bindSign == 1) {
			renderEdge(g, camera);
		}
	}

	protected void renderEdge(Graphics g, Camera camera) {
		Edge edge = findEdge();
		g.setColor(Colors.panelOutline);
		Gizmo.drawLine(g, edge.getStart(), edge.getEnd());
	}

	@Override
	public void render(Graphics g, Camera camera) {
		renderSelf(g, camera);

		for (UIElement element : elements) {
			element.render(g, camera);
		}
	}

	protected float clamp(float amount) { // deals only in positive values
		float max = horizontal ? (Main.WIDTH - topLeft.x) : (Main.HEIGHT - topLeft.y); // ignores negatively stretching panels
		return Maths.clamp(amount, 15, max - 15);
	}

	@Override
	public boolean mouseDown(MouseEvent e) {

		Vector mousePos = Main.screen.mousePos(e);
		if (edgeCollision(mousePos)) {
			edgeGrabbed = true;
			return true;
		}

		for (UIElement element : elements) {
			element.mouseDown(e);
		}

		if (mousePos.between(topLeft, topLeft.added(size))) {
			onClick(e, mousePos);
			return true;
		}
		return false;
	}

	protected void onClick(MouseEvent e, Vector mousePos) {

	}

	@Override
	public void mouseAt(Vector pos) {
		if (edgeGrabbed) {
			if (horizontal) {
				float to = pos.x - bindPos;
				to = clamp(to * bindSign);

				if (bindSign == 1) {
					size.x = to;
				} else {
					size.x = to;
					topLeft.x = bindPos - size.x;
				}

			} else {

				float to = pos.y - bindPos;
				to = clamp(to * bindSign);

				if (bindSign == 1) {
					size.y = to;
				} else {
					size.y = to;
					topLeft.y = bindPos - size.y;
				}
			}

			onDragChange();
		}

		for (UIElement element : elements) {
			element.mouseAt(pos);
		}
	}

	protected void onDragChange() {

	}

	@Override
	public boolean mouseUp(MouseEvent e) {

		edgeGrabbed = false;

		for (UIElement element : elements) {
			element.mouseUp(e);
		}

		return false;
	}

	protected boolean edgeCollision(Vector mouse) {
		return findEdge().distTo(mouse) < 5F;
	}

	protected final Edge findEdge() {
		if (horizontal) {
			if (bindSign == 1) {
				return new Edge(topLeft.added(size.x, 0), topLeft.added(size));
			} else {
				return new Edge(topLeft, topLeft.added(0, size.y));
			}
		}
		if (bindSign == 1) {
			return new Edge(topLeft.added(0, size.y), topLeft.added(size));
		}
		return new Edge(topLeft, topLeft.added(size.x, 0));
	}
}
