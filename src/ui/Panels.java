package ui;

import main.Main;
import perspective.Camera;
import util.Vector;

import java.awt.*;

public class Panels {

	public static class ScreenPanel extends MultiPanel {

		public ScreenPanel() {
			super(UIPanel.HORIZONTAL, 0, Main.WIDTH);
		}

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
			elements.add(new ExitButton());
		}

		@Override
		protected boolean edgeCollision(Vector mouse) {
			return false;
		}

		private static class ExitButton extends FuncButton {

			public ExitButton() {
				super(() -> {System.out.println("EXIT BUTTON PRESSED! exiting...");System.exit(0);}, Main.WIDTH - 25, 10, 50, 20);
			}

			@Override
			protected Color findColor() {

				if (hover) {
					return Color.LIGHT_GRAY;
				}
				return Color.DARK_GRAY;
			}
		}
	}

}
