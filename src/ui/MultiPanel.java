package ui;

import main.Main;
import perspective.Camera;
import poly.Edge;
import util.Gizmo;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

public class MultiPanel extends UIPanel { // TODO:

	private List<UIPanel> renderOrder;

	public MultiPanel(boolean horizontal, float bindPos, float width) {
		super(horizontal, bindPos, width);
	}

	public void reorder(int... indices) {
		assert indices.length == elements.size();

		UIPanel[] panels = new UIPanel[indices.length];
		for (int i = 0; i < indices.length; i++) {
			panels[indices[i]] = (UIPanel) elements.get(i);
		}
		renderOrder = Arrays.asList(panels);
	}

	public void addPanel(UIPanel panel) {
		assert panel.horizontal != horizontal;
		elements.add(panel);
	}

	private List<UIPanel> panelList() {
		return (List<UIPanel>) ((List<?>) elements);
	}

	@Override
	public void render(Graphics g, Camera camera) {
		renderSelf(g, camera);

		List<UIPanel> panels = (renderOrder == null) ? panelList() : renderOrder;
		for (UIPanel panel : panels) {
			panel.render(g, camera);
		}
	}

	@Override
	public boolean mouseDown(MouseEvent e) { // kinda messy, abstract pls

		if (edgeCollision(Main.screen.mousePos(e))) {
			edgeGrabbed = true;
			return true;
		}

		boolean hit = false;
		for (UIElement element : elements) {
			if (element.mouseDown(e)) {
				hit = true;
			}
		}
		return hit;
	}

	@Override
	protected void renderSelf(Graphics g, Camera camera) {
		if (bindSign == 1) {
			renderEdge(g, camera);
		}
	}


	@Override
	public void update() {
		super.update();

		onDragChange();
	}

	@Override
	protected void onDragChange() {
		super.onDragChange();

		List<UIPanel> panels = panelList();
		for (int i = 0; i < panels.size(); i++) {
			UIPanel panel = panels.get(i);

			if (horizontal) {
				panel.size.x = size.x; // (width / x)
				panel.topLeft.x = topLeft.x;

				if (i == 0) { // (height / y)
					panel.topLeft.y = topLeft.y;
				} else {

					UIPanel last = panels.get(i - 1);
					float storeLast = panel.topLeft.y;
					panel.topLeft.y = last.topLeft.y + last.size.y;
					panel.size.y -= panel.topLeft.y - storeLast;

					if (i == panels.size() - 1) {
						panel.size.y = (topLeft.y + size.y) - panel.topLeft.y;
					}
				}

				if (i < panels.size() - 1) {
					UIPanel next = panels.get(i + 1);
					if (panel.topLeft.y + panel.size.y > next.topLeft.y + next.size.y - 15) {
						panel.size.y = (next.topLeft.y + next.size.y - 15) - panel.topLeft.y;
						//panel.topLeft.y = next.topLeft.y - 15;
					}
					//panel.size.y = Math.max(panel.clamp(panel.size.y), next.topLeft.y - 15 - panel.topLeft.y);
				}

				if (panel.bindSign == 1) {
					panel.bindPos = panel.topLeft.y;
				} else {
					panel.bindPos = panel.topLeft.y + panel.size.y;
				}


			} else { // VERTICAL
				panel.size.y = size.y; // (height / y)
				panel.topLeft.y = topLeft.y;

				if (i == 0) { // (width / x)
					panel.topLeft.x = topLeft.x;
				} else {

					UIPanel last = panels.get(i - 1);
					panel.topLeft.x = last.topLeft.x + last.size.x;

					if (i == panels.size() - 1) {
						panel.size.x = Main.WIDTH - panel.topLeft.x;
					}
				}

				if (panel.bindSign == 1) {
					panel.bindPos = panel.topLeft.x;
				} else {
					panel.bindPos = panel.topLeft.x + panel.size.x;
				}
			}
		}

//		for (int i = panels.size() - 1; i >= 0; i--) {
//			UIPanel panel = panels.get(i);
//			if (horizontal) {
//				if (i < panels.size() - 1) {
//					//UIPanel next = panels.get(i + 1);
//					//if (panel.topLeft.y + panel.size.y > next.topLeft.y) {
//						//panel.size.y = next.topLeft.y - panel.topLeft.y - 5;
//					//}
//					//panel.size.y = Math.max(panel.clamp(panel.size.y), next.topLeft.y - 15 - panel.topLeft.y);
//				}
//			}
//		}

		for (UIPanel panel : panels) { // sus
			panel.onDragChange();
		}
	}
}
