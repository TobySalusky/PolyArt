package screens;

import com.cedarsoftware.util.io.JsonWriter;
import jdk.nashorn.internal.ir.debug.JSONWriter;
import main.Main;
import modifiers.MirrorModifier;
import modifiers.SubdivisionModifier;
import perspective.Camera;
import poly.Axis;
import poly.Edge;
import poly.PolyLayer;
import poly.Polygon;
import transformation.*;
import ui.*;
import ui.panels.MultiPanel;
import ui.panels.OpenPanel;
import ui.panels.Panels;
import ui.panels.UIPanel;
import util.*;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.awt.*;

public class PolyScreen implements Screen { // TODO: fix locked scaling - puts off screen (div 0?)

	private final List<PolyLayer> layers = new ArrayList<>();
	private PolyLayer layer;

	private final Camera camera = new Camera(0, 0);

	private final HashSet<Polygon> selectedPolygons = new HashSet<>();
	private Polygon editPoly;

	private Mode mode = Mode.object;
	private EditType editType = EditType.verts;
    private Tool tool = Tool.easyAdd;

    // input
	private Vector lastMousePos = new Vector();
	private boolean leftClickDown, middleClickDown, rightClickDown;
	private boolean shiftZoom;
	private static final Vector shiftZoomPoint = new Vector(Main.WIDTH / 2F, Main.HEIGHT / 2F);

    // non main
	private final List<Vector> selectedVertices = new ArrayList<>();
	private Transform currentTransform;
	private boolean transforming = false;
	private TransformType transformType;
	private Vector transformStart; // world position
	private Vector transformPivot; // world position
	private AxisType lockAxisType;

	private boolean numListen = false;
	private String typedNumString = "";
	private float typedNum;

	// tool specific
	private Vector selectStart, selectEnd;
	private List<Vector> preSelectSelectedVertices;
	private List<Polygon> preSelectSelectedPolygons;

	private Color selectedColor = Color.WHITE;

	// ui
	private final List<UIElement> uiElements = new ArrayList<>();

    public PolyScreen() {
        layer = new PolyLayer();
        layers.add(layer);

        //uiElements.add(new UIPanel(UIPanel.HORIZONTAL, Main.WIDTH, -100));
		Panels.ScreenPanel screen = new Panels.ScreenPanel();
		uiElements.add(screen);

		screen.addPanel(new Panels.ApplicationBarPanel());
		MultiPanel multi = new MultiPanel(UIPanel.VERTICAL, 20, Main.WIDTH - 20);
		screen.addPanel(multi);
		screen.addPanel(new UIPanel(UIPanel.VERTICAL, Main.HEIGHT, -100));

		multi.addPanel(new OpenPanel(UIPanel.HORIZONTAL, 0, Main.WIDTH - 200));
		MultiPanel right = new MultiPanel(UIPanel.HORIZONTAL, Main.WIDTH, -100);

		right.addPanel(new Panels.ColorPanel(UIPanel.VERTICAL, 0, 200, this));
		right.addPanel(new UIPanel(UIPanel.VERTICAL, 200, 300));
		right.addPanel(new Panels.PolySelectPanel(UIPanel.VERTICAL, 500, 100, this));
		multi.addPanel(right);

		multi.reorder(1, 0);
		screen.reorder(2, 1, 0);
		screen.setUpPanels();

		for (Mode mode : Mode.values()) {
			uiElements.add(new ModeButton(mode, this,100 + 60 * mode.ordinal(), 50));
		}

        for (Tool tool : Tool.values()) {
			uiElements.add(new ToolButton(tool, this,50, 150 + 60 * tool.ordinal()));
		}
    }

	public enum Mode {
	    object, edit
    }

    private enum EditType {
    	verts, edges
	}

    public enum Tool {

	    select {
			protected void forceMode(PolyScreen screen) {
				if (screen.editMode() && screen.editPoly == null) {
					screen.mode = Mode.object;
				}
			}
		}, create {
			protected void forceMode(PolyScreen screen) {
				if (screen.objectMode()) {
					screen.mode = Mode.edit;
				}
			}
		}, easyAdd {
			protected void forceMode(PolyScreen screen) {
				if (screen.objectMode()) {
					screen.mode = Mode.edit;
				}
			}
		};

	    protected void forceMode(PolyScreen screen) {}

	    public void update(PolyScreen screen) {
	    	forceMode(screen);
		}
    }

    private enum AxisType {
    	x, y
	}

    private enum TransformType {

    	move {
			@Override
			public Transform genTransform(PolyScreen screen, Vector newPos) {
				Vector diff = newPos.subbed(screen.transformStart);

				if (screen.lockAxisType == AxisType.x) {
					diff.x = 0;
					if (screen.hasTypedNum()) diff.y = screen.typedNum;
				} else if (screen.lockAxisType == AxisType.y) {
					if (screen.hasTypedNum()) diff.x = screen.typedNum;
					diff.y = 0;
				}

				return new MoveTransform(diff);
			}

			@Override
			public void renderGizmos(Graphics g, PolyScreen screen) { // TODO: FLATTEN LOCKED GIZMOS
				Vector from = screen.camera.toScreen(screen.transformStart);
				Vector to = screen.lastMousePos;

				flattenGizmoLine(from, to, screen);

				gizmoLine(g, from, to);
			}
		},

		rotate {
			@Override
			public Transform genTransform(PolyScreen screen, Vector newPos) {
				float startAngle = screen.transformStart.subbed(screen.transformPivot).angle();
				float diff = newPos.subbed(screen.transformPivot).angle() - startAngle;
				if (screen.hasTypedNum()) diff = (float) Math.toRadians(screen.typedNum);
				return new RotateTransform(diff, screen.transformPivot);
			}
		},

		scale {
			@Override
			public Transform genTransform(PolyScreen screen, Vector newPos) {
				float scalarX, scalarY;
				if (screen.lockAxisType == AxisType.x) {
					scalarX = 1;
					scalarY = newPos.subbed(screen.transformPivot).multed2(0, 1).mag() / screen.transformStart.subbed(screen.transformPivot).multed2(0, 1).mag();
					if (screen.hasTypedNum()) scalarY = screen.typedNum;
				} else if (screen.lockAxisType == AxisType.y) {
					scalarX = newPos.subbed(screen.transformPivot).multed2(1, 0).mag() / screen.transformStart.subbed(screen.transformPivot).multed2(1, 0).mag();
					if (screen.hasTypedNum()) scalarX = screen.typedNum;
					scalarY = 1;
				} else {
					scalarX = newPos.subbed(screen.transformPivot).mag() / screen.transformStart.subbed(screen.transformPivot).mag();
					if (screen.hasTypedNum()) scalarX = screen.typedNum;
					scalarY = scalarX;
				}

				if (scalarX == 0 || scalarY == 0) {
					return new ZeroDivScaleTransform(scalarX, scalarY, screen.transformPivot);
				}

				return new ScaleTransform(scalarX, scalarY, screen.transformPivot);
			}

			@Override
			public void renderGizmos(Graphics g, PolyScreen screen) {
				Vector from = screen.camera.toScreen(screen.transformPivot);
				Vector to = screen.lastMousePos;

				flattenGizmoLine(from, to, screen);

				gizmoLine(g, from, to);
			}
		};

    	abstract public Transform genTransform(PolyScreen screen, Vector newPos);

    	public void renderGizmos(Graphics g, PolyScreen screen) {
    		gizmoLine(g, screen.camera.toScreen(screen.transformPivot), screen.lastMousePos);
		}

		public void flattenGizmoLine(Vector from, Vector to, PolyScreen screen) {
			if (screen.lockAxisType == AxisType.x) {
				to.x = from.x;
			} else if (screen.lockAxisType == AxisType.y) {
				to.y = from.y;
			}
		}

		protected void gizmoLine(Graphics g, Vector from, Vector to) {
			g.setColor(Color.BLACK);
			Gizmo.dottedLine(g, from, to);
			g.setColor(Color.ORANGE);
			Gizmo.dot(g, from);
			Gizmo.dot(g, to);
		}
	}

	public void colorSelected(Color color) {
		selectedColor = color;

		if (objectMode()) {
			for (Polygon polygon : selectedPolygons) {
				polygon.setColor(selectedColor);
			}
		}
	}

	public List<Vector> copyVectorList(List<Vector> verts) {

		return new ArrayList<>(verts);
	}

	public List<Polygon> copySelectedPolygons() {
		return new ArrayList<>(selectedPolygons);
	}

	public void leftClickUp(MouseEvent e) {

		selectStart = null;

		for (UIElement element : uiElements) {
			element.mouseUp(e);
		}
	}

	public void leftClickDown(MouseEvent e) {

		Vector worldPos = camera.toWorld(mousePos(e)); // debug line, makes easier, okay..?

		for (UIElement element : uiElements) {
			if (element.mouseDown(e)) {
				return;
			}
		}

		// ALL MODES ======================================================

		if (transforming) {
			endTransform();
			return;
		}

		if (objectMode() || using(Tool.select)) {

			if (editMode()) {
				if (!e.isShiftDown()) {
					selectedVertices.clear();
				}
				preSelectSelectedVertices = copyVectorList(selectedVertices);
			} else {
				if (!e.isShiftDown()) {
					selectedPolygons.clear();
				}
				preSelectSelectedPolygons = copySelectedPolygons();
			}

			selectStart = worldPos;
			selectEnd = worldPos;
		}

		if (objectMode()) { // OBJECT MODE ===============================
			if (!e.isShiftDown()) {
				selectedPolygons.clear();
				editPoly = null;
			}

			Vector vec = camera.toWorld(mousePos(e));
			outer:
			for (int i = layers.size() - 1; i >= 0; i--) {
				PolyLayer layer = layers.get(i);
				for (int j = layer.getPolygons().size() - 1; j >= 0; j--) {
					Polygon polygon = layer.getPolygons().get(j);
					Polygon[] output = polygon.getLastOutput();

					if (output != null) {
						for (Polygon out : output) {
							if (out.pointInside(vec)) {
								editPoly = polygon; // abstract?
								selectedPolygons.add(editPoly);
								break outer;
							}
						}
					} else {
						if (polygon.pointInside(vec)) {
							editPoly = polygon;
							selectedPolygons.add(editPoly);
							break outer;
						}
					}
				}
			}

			return;
		}

		// EDIT MODE ==============================================================
		if (using(Tool.create)) { // TODO: to switch
			if (editPoly == null) {
				editPoly = new Polygon(selectedColor);
				layer.getPolygons().add(editPoly);

				selectedPolygons.clear(); // TODO: ??
				selectedPolygons.add(editPoly);
			}
			editPoly.addPoint(worldPos);

			if (!e.isShiftDown()) {
				selectedVertices.clear();
			}
			selectedVertices.add(worldPos);
		}

		if (using(Tool.easyAdd)) {
			if (editMode()) {

				if (editPoly == null) {
					editPoly = new Polygon(selectedColor);
					selectedPolygons.add(editPoly);
					layer.getPolygons().add(editPoly);
					editPoly.addPoint(worldPos);
					return;
				}

				float minDist = Float.MAX_VALUE;
				Edge min = null;
				for (Edge edge : editPoly.genEdges()) {
					float dist = edge.distTo(worldPos);
					if (dist < minDist) {
						minDist = dist;
						min = edge;
					}
				}

				editPoly.addToEdge(worldPos, min);

				if (!e.isShiftDown()) {
					selectedVertices.clear();
				}
				selectedVertices.add(worldPos);
			}
		}
	}

	@Override
	public void scrolled(int amount) {
		camera.multZoom(camera.toWorld(lastMousePos), 1 + amount / 10F);
	}

	private void shiftZoomGizmo(Graphics g) {
		g.setColor(Gizmo.darkGrey);
		Gizmo.dottedLine(g, shiftZoomPoint, lastMousePos);
		Gizmo.dot(g, shiftZoomPoint, Gizmo.midOrange);
		Gizmo.dot(g, lastMousePos, Gizmo.midOrange);
	}

	private void previewEasyAdd(Graphics g) {
    	Vector worldPos = camera.toWorld(lastMousePos);

		if (editMode() && editPoly != null && !shiftZoom && !transforming) {
			float minDist = Float.MAX_VALUE;
			Edge min = null;
			for (Edge edge : editPoly.genEdges()) {
				float dist = edge.distTo(worldPos);
				if (dist < minDist) {
					minDist = dist;
					min = edge;
				}
			}
			assert min != null;

			g.setColor(Gizmo.nearBlack);
			Gizmo.dottedLine(g, camera.toScreen(min.getStart()), lastMousePos);
			Gizmo.dottedLine(g, camera.toScreen(min.getEnd()), lastMousePos);
			Gizmo.dot(g, lastMousePos);
		}
	}

	public List<PolyLayer> getLayers() {
    	return layers;
	}

	public int polygonCount() {

    	int count = 0;
    	for (PolyLayer layer : layers) {
    		count += layer.getPolygons().size();
		}
    	return count;
	}

	public boolean using(Tool tool) {
    	return this.tool == tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	@Override
    public void mouseDown(MouseEvent e) {

    	switch (e.getButton()) {
			case (MouseEvent.BUTTON1):
				leftClickDown = true;
				leftClickDown(e);
				break;
			case (MouseEvent.BUTTON2):
				middleClickDown = true;

				if (e.isShiftDown()) {
					shiftZoom = true;
				}
				break;
			case (MouseEvent.BUTTON3):
				rightClickDown = true;
				break;
		}
    }

	@Override
	public void mouseUp(MouseEvent e) {

		switch (e.getButton()) {
			case (MouseEvent.BUTTON1):
				leftClickDown = false;

				leftClickUp(e);

				break;
			case (MouseEvent.BUTTON2):
				middleClickDown = false;

				shiftZoom = false;
				break;
			case (MouseEvent.BUTTON3):
				rightClickDown = false;
				break;
		}
	}

	public void findSelectedVertices() {
    	if (objectMode()) {
			selectedVertices.clear();
			for (Polygon poly : selectedPolygons) {
				selectedVertices.addAll(poly.getVertices());
			}
		} else {
    		// TODO:
			if (editType == EditType.edges) {

			}
		}
	}

	public void deletePoly(Polygon poly) {

		for (int i = 0; i < layer.getPolygons().size(); i++) {

			if (layer.getPolygons().get(i) == poly) {
				layer.getPolygons().remove(i);
				break;
			}
		}
    }

    public void deleteAction() {
    	if (objectMode()) {
			Iterator<Polygon> it = selectedPolygons.iterator();

			while (it.hasNext()) {
				Polygon sel = it.next();

				loop:
				for (PolyLayer layer : layers) {
					for (int i = 0; i < layer.getPolygons().size(); i++) {
						Polygon poly = layer.getPolygons().get(i);
						if (sel == poly) {
							layer.getPolygons().remove(i);
							if (poly == editPoly) {
								editPoly = null;
							}
							break loop;
						}
					}
				}

			}
		} else { // edit mode

    		if (editType == EditType.verts && editPoly != null) {
				editPoly.removeAll(selectedVertices);
			}

		}
	}

    public void enterAction() {

    	if (transforming) {
			endTransform();
			return;
		}

    	if (using(Tool.easyAdd)) {
    		tool = Tool.select;
		}

    	if (editPoly != null) {
    		if (selectedVertices.size() > 0) {
    			selectedVertices.clear();
			} else {
				editPoly = null;
				selectedPolygons.clear();
			}
		}
	}

	private void cancelTransform() {
    	currentTransform.remove(selectedVertices);
    	endTransform();
	}

	private void endTransform() {
    	transforming = false;
    	currentTransform = null;
    	transformType = null;
    	transformStart = null;
    	transformPivot = null;

    	lockAxisType = null;

    	numListen = false;
	}

	private void rotStart() {
    	findSelectedVertices();
		transformType = TransformType.rotate;
		transformPivot = findPivot();
		startTransform();
    }

    private void moveStart() {
    	findSelectedVertices();
    	transformType = TransformType.move;
    	transformPivot = findPivot();
    	startTransform();
	}

	private void scaleStart() {
		findSelectedVertices();
		transformType = TransformType.scale;
		transformPivot = findPivot();
		startTransform();
	}

	private Vector findPivot() {
    	return Vertices.average(selectedVertices);
	}

	public void addTypedNum(String added) {
		typedNumString += added;
		if (hasTypedNum()) {
			typedNum = Float.parseFloat(typedNumString);
		}
		updateTransform(lastMousePos);
	}

	public boolean hasTypedNum() {
    	return typedNumString.length() > 0 && (typedNumString.charAt(0) != '-' || typedNumString.length() > 1);
	}

	@Override
	public void numberDown(int num) {
		if (numListen) {
			addTypedNum("" + num);
		}
	}

	@Override
	public void keyDown(KeyEvent e) {

    	switch (e.getKeyCode()) {

			case (KeyEvent.VK_TAB):
				if (mode == Mode.object) {
					mode = Mode.edit;
				} else {
					tool = Tool.select;
					mode = Mode.object;
				}
				break;

			case (KeyEvent.VK_A):
				if (editMode() && editPoly != null) {
					selectedVertices.clear();
					selectedVertices.addAll(copyVectorList(editPoly.getVertices()));
				}
				break;

			case (KeyEvent.VK_B):
				tool = Tool.easyAdd;
				break;

			case (KeyEvent.VK_M):
				editPoly.addModifier(new MirrorModifier(new Axis(Vector.zero, Maths.HalfPI)));
				break;
			case (KeyEvent.VK_N):
				editPoly.addModifier(new SubdivisionModifier(3));
				break;

			case (KeyEvent.VK_Z):
			case (KeyEvent.VK_Y):
				if (transforming) {
					lockAxisType = AxisType.x;
					updateTransform(lastMousePos);
				}
				break;

			case (KeyEvent.VK_X): // TODO: shall X delete too?

				if (transforming) {
					lockAxisType = AxisType.y;
					updateTransform(lastMousePos);
				}
				break;

			case (KeyEvent.VK_DELETE):
				deleteAction();
				break;

			case (KeyEvent.VK_SPACE):
			case (KeyEvent.VK_ENTER):
				enterAction();
				break;

			case (KeyEvent.VK_G):
				moveStart();
				break;

			case (KeyEvent.VK_R):
				rotStart();
				break;

			case (KeyEvent.VK_S):
				scaleStart();
				break;

			/*case (KeyEvent.VK_SPACE):
				if (editMode()) {
					if (editType == EditType.verts) {
						editType = EditType.edges;
					} else {
						editType = EditType.verts;
					}
				}
				break;*/

			case (KeyEvent.VK_PERIOD):
				if (numListen) {
					addTypedNum(".");
				}
				break;

			case (KeyEvent.VK_MINUS):
				if (numListen) {
					addTypedNum("-"); // TODO: (also [arithmetic?])
				}
				break;
		}

	}

	private void startTransform() {
    	transforming = true;
		transformStart = camera.toWorld(lastMousePos);

		numListen = true;
		typedNumString = "";
		typedNum = 0;
	}

	private void updateTransform(Vector mousePos) {
		Vector pos = camera.toWorld(mousePos);

		if (currentTransform != null) {
			currentTransform.remove(selectedVertices);
		}
		currentTransform = transformType.genTransform(this, pos);
		currentTransform.apply(selectedVertices);
	}

	@Override
	public void mouseMove(MouseEvent e) {

		lastMousePos = mousePos(e);

		if (transforming) {
			updateTransform(lastMousePos);
		}
    }

	@Override
	public void mouseDrag(MouseEvent e) {

		Vector delta = mousePos(e).subbed(lastMousePos);
		lastMousePos = mousePos(e);

		if (middleClickDown) { // camera pan and shift-zoom
			if (shiftZoom) { // TODO: make method?

				Vector diff = lastMousePos.subbed(shiftZoomPoint);
				Vector lastDiff = diff.subbed(delta);

				float sensitivity = 0.005F;

				camera.multZoom(camera.copyPos(), 1 + sensitivity * (diff.mag() - lastDiff.mag()));
			} else {
				camera.move(delta.inverse().multed(1 / camera.getScale()));
			}
		}

		if (selectStart != null) {
			selectEnd = camera.toWorld(lastMousePos);
			if (editMode()) {

				if (editPoly == null) {
					return;
				}

				selectedVertices.clear();
				selectedVertices.addAll(preSelectSelectedVertices);

				for (Vector vert : editPoly.getVertices()) {
					if (vert.between(selectStart, selectEnd)) {
						selectedVertices.add(vert);
					}
				}
			} else { // object mode

				editPoly = null;

				selectedPolygons.clear();
				selectedPolygons.addAll(preSelectSelectedPolygons);

				for (PolyLayer layer : layers) {
					poly:
					for (Polygon polygon : layer.getPolygons()) { // fix arbitrary order please

						Polygon[] output = polygon.getLastOutput();

						if (output != null) {
							for (Polygon out : output) {
								if (out.insideRange(selectStart, selectEnd)) {
									selectedPolygons.add(polygon);
									editPoly = polygon;
									continue poly;
								}
							}
						} else {
							if (polygon.insideRange(selectStart, selectEnd)) {
								selectedPolygons.add(polygon);
								editPoly = polygon;
							}
						}
					}
				}
			}

		}
    }

	@Override
	public void update() {

    	tool.update(this);

    	if (editPoly != null && editPoly.getVertices().size() == 0) {
    		selectedPolygons.remove(editPoly);
			deletePoly(editPoly);
			editPoly = null;
		}

		for (UIElement element : uiElements) {
			element.mouseAt(lastMousePos);
			element.update();
		}
	}

	@Override
	public void render(Graphics g) {
		Gizmo.fillScreen(g, Colors.background);

		renderLayers(g);

		if (transformType != null) {
			transformType.renderGizmos(g, this);
		}

		if (using(Tool.easyAdd) && editMode()) {
			previewEasyAdd(g);
		}

		if (selectStart != null) {
			Gizmo.drawSelectRect(g, camera, selectStart, selectEnd);
		}

		if (shiftZoom) {
			shiftZoomGizmo(g);
		}

		// UI ===========================
		if (hasTypedNum()) {
			g.setColor(Color.WHITE);
			g.drawString(typedNumString, Main.WIDTH - 30, 50);
		}

		for (UIElement element : uiElements) {
			element.render(g, camera);
		}
    }

	public void renderLayers(Graphics g) {
	    for (PolyLayer layer : layers) {
	        for (Polygon poly : layer.getPolygons()) {
                poly.render(g, camera, selectedPolygons.contains(poly), editMode() && editPoly == poly);
            }
        }
    }

    public boolean vertSelected(Vector vert) {
    	return selectedVertices.contains(vert);
	}

    public boolean objectMode() {
    	return mode == Mode.object;
	}

	public boolean editMode() {
    	return mode == Mode.edit;
	}



	public HashSet<Polygon> getSelectedPolygons() {
		return selectedPolygons;
	}

	public Polygon getEditPoly() {
		return editPoly;
	}

	public void setEditPoly(Polygon editPoly) {
		this.editPoly = editPoly;
	}
}
