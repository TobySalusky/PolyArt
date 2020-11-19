package ui;

import main.Main;
import perspective.Camera;
import poly.ImagePoly;
import poly.Polygon;
import screens.PolyScreen;
import util.FileUtil;
import util.Generator;
import util.Vector;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

public abstract class ActionMenu extends UIContainer { // this is one of those slide down things idk what it's called, perhaps abstract to subclass?

	private boolean delete;

	public ActionMenu(Vector pos, Vector size) { // middle
		this.pos = pos;
		this.size = size;

		createElements();
		moveElements();
	}

	public ActionMenu(Vector pos) { // topLeft
		this.pos = pos;
		createElements();
		size = new Vector(100, 40 * elements.size());
		moveElements();
	}

	protected abstract void createElements();

	private void moveElements() {

		float height = size.y / elements.size();
		for (int i = 0; i < elements.size(); i++) {
			UIElement element = elements.get(i);
			element.resize(new Vector(pos.x, findTopLeft().y + height * (0.5F + i)), new Vector(size.x, height));
		}
	}

	@Override
	public boolean mouseDown(MouseEvent e) {
		delete = true;
		return super.mouseDown(e);
	}

	@Override
	public boolean shouldRemove() {
		return delete;
	}

	@Override
	protected void renderSelf(Graphics g, Camera camera) {}

	public static class MeshMenu extends ActionMenu {

		public MeshMenu(Vector pos) {
			super(pos);
		}

		@Override
		protected void createElements() {
			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genSquare(screen.getCamera().copyPos(), 100);
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = new ImagePoly(FileUtil.fileToImage("C://tmp/image.jpg"), screen.getCamera().copyPos(), 100);
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genEdgePoly(screen.getCamera().copyPos(), 100);
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genScreenRect();
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genNGon(screen.getCamera().copyPos(), 100, 3);
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genNGon(screen.getCamera().copyPos(), 100, 5);
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));

			elements.add(new SimpleFuncButton(() -> {
				PolyScreen screen = ((PolyScreen)Main.screen);
				Polygon square = Generator.genNGon(screen.getCamera().copyPos(), 100, 7);
				square.setColor(screen.getSelectedColor());
				screen.getLayer().getPolygons().add(square);
				screen.getSelectedPolygons().clear();
				screen.getSelectedPolygons().add(square);
				screen.setEditPoly(square);
			}, 0, 0, 0, 0));
		}
	}
}
