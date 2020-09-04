package ui.panels;

import lambda.Func;
import main.Main;
import perspective.Camera;
import poly.PolyLayer;
import poly.Polygon;
import screens.PolyScreen;
import ui.FuncButton;
import ui.UIElement;
import ui.premade.ColorPickerRGB;
import ui.premade.HueSlider;
import ui.premade.PolygonSelectButton;
import util.Maths;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;


public class Panels {

	public static class PolySelectPanel extends UIPanel { // TODO: handle multiple layers please
		// WARNING: replacing a polygon with no frames in between will cause buttons not to be recreated!

		private final PolyScreen screen;
		private PolygonSelectButton layerGrabbed;

		public PolySelectPanel(boolean horizontal, float bindPos, float width, PolyScreen screen) {
			super(horizontal, bindPos, width);
			this.screen = screen;
		}

		public PolyScreen getScreen() {
			return screen;
		}

		@Override
		protected void onDragChange() {
			float width = Math.max(size.x - 20, size.x * 0.5F), height = Math.min(50, size.y / elements.size());
			float x = findCenter().x;
			for (int i = 0; i < elements.size(); i++) {
				Vector buttonPos = new Vector(x, topLeft.y + height * (i + 0.5F));
				elements.get(i).resize(buttonPos, new Vector(width, height));
			}
		}

		public void setLayerGrabbed(PolygonSelectButton layerGrabbed) {
			this.layerGrabbed = layerGrabbed;
		}

		@Override
		public void mouseAt(Vector pos) {
			super.mouseAt(pos);

			if (layerGrabbed != null) {
				float height = layerGrabbed.getSize().y;

				int index = elements.indexOf(layerGrabbed);
				int newIndex = Math.max(0, Math.min((int)((pos.y - topLeft.y) / height), elements.size() - 1));

				if (index != newIndex) {

					UIElement store = elements.remove(index);
					elements.add(newIndex, store);


					int end = elements.size() - 1;
					List<Polygon> polyList =  screen.getLayers().get(0).getPolygons(); // TODO: CURRENTLY ONLY SUPPORTS ONE LAYER!!!
					Polygon poly = polyList.remove(end - index);
					polyList.add(end - newIndex, poly);

					onDragChange();
				}
			}
		}

		@Override
		public void render(Graphics g, Camera camera) {
			super.render(g, camera);
			if (layerGrabbed != null) {
				layerGrabbed.render(g, camera); // makes sure it's on top
			}
		}

		@Override
		public void update() {
			super.update();

			if (elements.size() != screen.polygonCount()) {
				recreateTabs();
			}
		}

		private void recreateTabs() {
			elements.clear();
			for (PolyLayer layer : screen.getLayers()) {
				for (Polygon polygon : layer.getPolygons()) {
					elements.add(0, new PolygonSelectButton(this, polygon));
				}
			}

			onDragChange();
		}
	}

	public static class ColorPanel extends UIPanel {


		public ColorPanel(boolean horizontal, float bindPos, float width, PolyScreen polyScreen) {
			super(horizontal, bindPos, width);

			ColorPickerRGB color = new ColorPickerRGB(polyScreen);
			elements.add(color);
			elements.add(new HueSlider(color));
		}

		@Override
		protected void onDragChange() {
			UIElement picker = elements.get(0);
			picker.resize(findCenter(), Vector.maxEach(size.subbed(30, 30), Vector.one));
			elements.get(1).resize(findCenter().added(0, picker.getHeight() / 2 + 15), Vector.maxEach(new Vector(size.x - 20, 20), Vector.one));
		}
	}

	public static class ScreenPanel extends MultiPanel {

		public ScreenPanel() {
			super(UIPanel.HORIZONTAL, 0, Main.WIDTH);
		}

		@Override
		protected void renderSelf(Graphics g, Camera camera) { }

		public void setUpPanels() {
			onDragChange();
		}

		@Override
		protected boolean edgeCollision(Vector mouse) {
			return false;
		}
	}

	public static class ApplicationBarPanel extends UIPanel {

		public ApplicationBarPanel() {
			super(UIPanel.VERTICAL, 0, 20);
			//elements.add(new MinimizeButton());
			//elements.add(new SmallifyButton());
			elements.add(new ExitButton());
		}

		@Override
		protected boolean edgeCollision(Vector mouse) {
			return false;
		}

		private static abstract class HoverColorButton extends FuncButton {
			public HoverColorButton(Func func, float x, float y, float width, float height) {
				super(func, x, y, width, height);
			}

			@Override
			protected Color findColor() {

				if (hover) {
					return Color.LIGHT_GRAY;
				}
				return Color.DARK_GRAY;
			}
		}

		private static class ExitButton extends HoverColorButton {

			public ExitButton() {
				super(() -> {System.out.println("EXIT BUTTON PRESSED! exiting...");System.exit(0);}, Main.WIDTH - 25, 10, 50, 20);
			}
		}

		private static class SmallifyButton extends HoverColorButton {
			public SmallifyButton() {
				super(Main::minimize, Main.WIDTH - 75, 10, 50, 20);
			}
		}

		private static class MinimizeButton extends HoverColorButton {
			public MinimizeButton() {
				super(Main::minimize, Main.WIDTH - 125, 10, 50, 20);
			}
		}
	}

}
