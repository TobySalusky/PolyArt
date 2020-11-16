package screens;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import main.Main;
import modifiers.MirrorModifier;
import modifiers.Modifier;
import modifiers.SolidifyEdges;
import modifiers.SubdivisionModifier;
import perspective.Camera;
import poly.*;
import poly.Polygon;
import transformation.*;
import ui.*;
import ui.panels.*;
import ui.premade.*;
import util.*;
import util.Vector;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.stream.IntStream;

public class PolyScreen implements Screen { // TODO: fix locked scaling - puts off screen (div 0?)

	private List<PolyLayer> layers = new ArrayList<>();
	private PolyLayer layer;

	private final Camera camera = new Camera(0, 0);

	private final HashSet<Polygon> selectedPolygons = new HashSet<>();
	private Polygon editPoly;

	private Mode mode = Mode.object;
	private EditType editType = EditType.verts;
    private Tool tool = Tool.select;

    // input
	private Vector lastMousePos = new Vector();
	private boolean leftClickDown, middleClickDown, rightClickDown;
	private boolean shiftZoom;
	private static final Vector shiftZoomPoint = new Vector(Main.WIDTH / 2F, Main.HEIGHT / 2F);

    // non main
	private final List<Vector> selectedVertices = new ArrayList<>();
	private final List<Edge> selectedEdges = new ArrayList<>();
	private Transform currentTransform;
	private boolean transforming = false;
	private TransformType transformType;
	private Vector transformStart; // world position
	private Vector transformPivot; // world position
	private Axis lockAxis;

	private static final float selectionRange = 10F;

	private boolean numListen = false;
	private String typedNumString = "";
	private float typedNum;

	// tool specific
	private Vector selectStart, selectEnd;
	private List<Vector> preSelectSelectedVertices;
	private List<Edge> preSelectSelectedEdges;
	private List<Polygon> preSelectSelectedPolygons;

	private List<Axis> snappingAxes;
	private boolean shiftHeld;

	private Color selectedColor = Color.WHITE;

	// ui
	private final List<UIElement> uiElements = new ArrayList<>();
	private AnimationPanel animationPanel;

	// settings
	private boolean findPivotByMass = true, antialiasRender = true;
	private static final Point targetResolution = new Point(1920, 1080); // new Point(3840, 2160); // 4K

    public PolyScreen() {
        layer = new PolyLayer();
        layers.add(layer);

		Panels.ScreenPanel screen = new Panels.ScreenPanel();
		uiElements.add(screen);

		screen.addPanel(new Panels.ApplicationBarPanel());
		MultiPanel multi = new MultiPanel(UIPanel.VERTICAL, Main.HEIGHT, -200);
		screen.addPanel(multi);

		MultiPanel openMulti = new MultiPanel(UIPanel.HORIZONTAL, 0, Main.WIDTH - 200);
		multi.addPanel(openMulti);
		openMulti.addPanel(new OpenPanel(UIPanel.VERTICAL, 0, Main.HEIGHT - 10));
		animationPanel = new AnimationPanel(UIPanel.VERTICAL, Main.HEIGHT, -10);
		openMulti.addPanel(animationPanel);


		MultiPanel right = new MultiPanel(UIPanel.HORIZONTAL, Main.WIDTH, -100);

		right.addPanel(new Panels.ColorPanel(UIPanel.VERTICAL, 0, 200, this));
		right.addPanel(new ModifierPanel(UIPanel.VERTICAL, 200, 300, this));
		right.addPanel(new Panels.PolySelectPanel(UIPanel.VERTICAL, 500, -100, this));
		multi.addPanel(right);

		screen.reorder(1, 0);
		multi.reorder(1, 0);
		openMulti.reorder(1, 0);
		screen.setUpPanels();

		for (Mode mode : Mode.values()) {
			uiElements.add(new ModeButton(mode, this,100 + 60 * mode.ordinal(), 50));
		}

		for (EditType editType : EditType.values()) {
			uiElements.add(new EditTypeButton(editType, this,100 + 60 * editType.ordinal(), 100));
		}

        for (Tool tool : Tool.values()) {
			uiElements.add(new ToolButton(tool, this,50, 150 + 60 * tool.ordinal()));
		}
    }

	public enum Mode {
	    object, edit
    }

	public enum EditType {
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
				} else {
					screen.editType = EditType.verts;
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

				if (screen.lockAxis != null) {
					float dot = screen.lockAxis.normVec().dot(diff.normed());
					diff = screen.lockAxis.normVec().multed(dot * diff.mag());
				}

				if (screen.snappingMode()) { // snapping
					if (screen.snappingAxes == null) {
						screen.genSnappingAxes();
					}
					Vector snapOff = screen.snapOffset(new MoveTransform(diff));
					if (snapOff != null) {
						diff.sub(snapOff);
					}
				}

				if (screen.hasTypedNum()) diff = screen.lockAxis.normVec().multed(screen.typedNum);

				return new MoveTransform(diff);
			}

			@Override
			public void renderGizmos(Graphics g, PolyScreen screen) { // TODO: FLATTEN LOCKED GIZMOS
				Vector from = screen.camera.toScreen(screen.transformStart);
				Vector to = screen.lastMousePos;

				flattenGizmoLine(from, to, screen);

				gizmoLine(g, from, to);

				if (screen.snappingMode() && screen.snappingAxes != null) {
					for (Axis axis : screen.snappingAxes) {
						axis.render(g, screen.camera);
					}
				}
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
			public Transform genTransform(PolyScreen screen, Vector newPos) { // TODO: make work with axes other than up and right
				Vector scalar;

				Vector norm = (screen.lockAxis != null) ? screen.lockAxis.normVec() : Vector.one;

				float scale = newPos.subbed(screen.transformPivot).multed2(norm).mag() /
						screen.transformStart.subbed(screen.transformPivot).multed2(norm).mag();

				if (screen.hasTypedNum()) {
					scale = screen.typedNum;
				}

				scalar = Vector.one.multed(scale);
				if (screen.lockAxis != null) {
					scalar.mult2(norm);
					scalar.add(norm.flippedXY());
				}

				if (scalar.x == 0 || scalar.y == 0) {
					return new ZeroDivScaleTransform(scalar.x, scalar.y, screen.transformPivot);
				}

				return new ScaleTransform(scalar.x, scalar.y, screen.transformPivot);
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
    		if (screen.lockAxis == null) return;
			Vector diff = to.subbed(from);
			float dot = screen.lockAxis.normVec().dot(diff.normed());
			diff = screen.lockAxis.normVec().multed(dot * diff.mag());
			to.setTo(from.added(diff));
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

	public List<Edge> copyEdgeList(List<Edge> edges) {
    	return new ArrayList<>(edges);
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

		if (objectMode() || using(Tool.select)) { // combine with thing below pls

			if (!e.isShiftDown()) {
				selectedPolygons.clear();
			}
			preSelectSelectedPolygons = copySelectedPolygons();

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
		if (using(Tool.select)) { // TODO -> switch
			if (editType == EditType.verts) {
				if (!e.isShiftDown()) {
					selectedVertices.clear();
				}
				preSelectSelectedVertices = copyVectorList(selectedVertices);
			} else { // EDGES
				if (!e.isShiftDown()) {
					selectedEdges.clear();
				}
				preSelectSelectedEdges = copyEdgeList(selectedEdges);
			}

			assert editPoly != null;
			if (editType == EditType.verts) {
				//TODO: vert selection
			} else { // EDGES
				float min = Float.MAX_VALUE;
				Edge minEdge = null;
				for (Edge edge : editPoly.getStoredEdges()) {
					float dist = edge.distTo(worldPos) * camera.getScale(); // keeps screen distance consistent
					if (dist < min) {
						min = dist;
						minEdge = edge;
					}
				}
				if (min <= selectionRange) {
					selectedEdges.add(minEdge);
				}
			}
		} else if (using(Tool.create)) {
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
		} else if (using(Tool.easyAdd)) {
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
		g.setColor(Colors.darkGrey);
		Gizmo.dottedLine(g, shiftZoomPoint, lastMousePos);
		Gizmo.dot(g, shiftZoomPoint, Colors.midOrange);
		Gizmo.dot(g, lastMousePos, Colors.midOrange);
	}

	private boolean snappingMode() {
    	return (transforming && transformType == TransformType.move && shiftHeld);
	}

	private Vector snapOffset(Transform transform) { // TODO: disallow multi-axis movement when axis-locked

    	if (snappingAxes != null) {
    		Vector minOff = null;
    		for (Axis axis : snappingAxes) { // make parallel stream?
				for (Vector vector : selectedVertices) {
					Vector transformed = vector.copy();
					transform.apply(transformed);
					Vector off = axis.offsetFrom(transformed);
					float mag = off.mag();
					if (mag < 50 / camera.getScale()) {
						if (minOff == null || mag < minOff.mag()) {
							minOff = off;
						}
					}
				}
			}
    		return minOff;
		}

    	return null;
	}

	private void genSnappingAxes() {
    	snappingAxes = new ArrayList<>();
    	for (Vector vert : editPoly.getVertices()) {
			if (!selectedVertices.contains(vert)) { //  D:<
				snappingAxes.add(new Axis(vert, 0));
				snappingAxes.add(new Axis(vert, Maths.HalfPI));
			}
		}

    	for (Modifier modifier : editPoly.getModifiers()) {
    		Axis[] modAxes = modifier.snappingAxes(editPoly);
    		if (modAxes != null) {
				snappingAxes.addAll(Arrays.asList(modAxes));
			}
		}
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

			g.setColor(Colors.nearBlack);
			Gizmo.dottedLine(g, camera.toScreen(min.getStart()), lastMousePos);
			Gizmo.dottedLine(g, camera.toScreen(min.getEnd()), lastMousePos);
			Gizmo.dot(g, lastMousePos);
		}
	}

	public List<PolyLayer> getLayers() {
    	return layers;
	}

	public PolyLayer getLayer() {
		return layer;
	}

	public Camera getCamera() {
		return camera;
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

				if (transforming) {
					cancelTransform();
					return;
				}
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
				HashSet<Vector> verts = new HashSet<>();
				selectedEdges.forEach(edge -> {
					verts.add(edge.getStart());
					verts.add(edge.getEnd());
				});
				selectedVertices.clear();
				selectedVertices.addAll(verts);
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
							break loop;
						}
					}
				}
			}

			selectedPolygons.clear();
			editPoly = null;
		} else { // edit mode

    		if (editPoly != null) {
    			findSelectedVertices();
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
    	if (currentTransform != null) {
			currentTransform.remove(selectedVertices);
		}
    	endTransform();
	}

	private void endTransform() {
    	transforming = false;
    	currentTransform = null;
    	transformType = null;
    	transformStart = null;
    	transformPivot = null;

    	lockAxis = null;

    	numListen = false;

    	snappingAxes = null;
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
    	if (findPivotByMass) {
			return Vertices.average(selectedVertices);
		}
		return Vertices.center(selectedVertices);
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
				cancelTransform();

				if (!e.isShiftDown()) {
					if (mode == Mode.object) {
						mode = Mode.edit;
					} else {
						tool = Tool.select;
						mode = Mode.object;
					}
				} else {
					editType = EditType.values()[(editType.ordinal() + 1) % EditType.values().length];
					tool = Tool.select;
				}
				break;

			case (KeyEvent.VK_A):
				if (e.isShiftDown()) {
					uiElements.add(new ActionMenu.MeshMenu(lastMousePos));
				} else {
					if (editMode() && editPoly != null) {
						selectedVertices.clear();
						selectedVertices.addAll(copyVectorList(editPoly.getVertices()));
					}
				}
				break;

			case (KeyEvent.VK_U):

				if (e.isShiftDown()) {

					if (selectedPolygons.size() == 0) {
						System.out.println("couldn't output model because no polygons were selected");
						break;
					}

					ArrayList<Polygon> modelPolys = new ArrayList<>();
					for (Polygon poly : layer.getPolygons()) {
						if (selectedPolygons.contains(poly)) {
							modelPolys.add(poly);
						}
					}
					Model model = new Model(modelPolys);
					System.out.println(JsonWriter.objectToJson(model));

				} else {
					System.out.println(JsonWriter.objectToJson(editPoly));
				}
				break;

			case (KeyEvent.VK_I):
				if (editType == EditType.edges) {
					if (editPoly != null) {
						Edge[] extruded = editPoly.extrude(selectedEdges);
						selectedEdges.clear();
						selectedEdges.addAll(Arrays.asList(extruded));
						scaleStart();
					}
				}
				break;

			case (KeyEvent.VK_E):
				if (objectMode() || editType == EditType.verts) {
					tool = Tool.easyAdd;
				}

				if (editType == EditType.edges) {
					if (editPoly != null) {
						Edge[] extruded = editPoly.extrude(selectedEdges);
						selectedEdges.clear();
						selectedEdges.addAll(Arrays.asList(extruded));
						moveStart();
						lockAxis = selectedEdges.get(0).perpendicular();
					}
				}
				break;

			case (KeyEvent.VK_D):

				if (e.isShiftDown()) {
					if (objectMode()) {
						List<Polygon> toCopy = copySelectedPolygons();
						selectedPolygons.clear();

						for (Polygon polygon : toCopy) {
							Polygon clone = polygon.unlinkedFullClone();
							layer.getPolygons().add(clone);
							selectedPolygons.add(clone);

							if (polygon == editPoly) {
								editPoly = clone;
							}
							moveStart();
						}

					}
				}

				break;

			case KeyEvent.VK_M:
				selectedPolygons.forEach(poly -> poly.addModifier(new MirrorModifier(new Axis(Vector.zero, Maths.HalfPI))));
				break;
			case KeyEvent.VK_N:
				selectedPolygons.forEach(poly -> poly.addModifier(new SubdivisionModifier(10)));
				break;
			case KeyEvent.VK_B:
				selectedPolygons.forEach(poly -> poly.addModifier(new SolidifyEdges()));
				break;

			case (KeyEvent.VK_Z):
			case (KeyEvent.VK_Y):
				if (transforming) {
					lockAxis = Axis.up;
					updateTransform(lastMousePos);
				}
				break;

			case (KeyEvent.VK_X): // TODO: perhaps add confirmation

				if (transforming) {
					lockAxis = Axis.right;
					updateTransform(lastMousePos);
					break;
				}
			case (KeyEvent.VK_DELETE):
				deleteAction();
				break;

			case KeyEvent.VK_K:
				if (editPoly != null)
					animationPanel.animation.key(editPoly);
				break;

			case (KeyEvent.VK_SPACE):
				if (animationPanel.inFocus()) {
					animationPanel.animation.togglePlaying();
					break;
				}
			case (KeyEvent.VK_ENTER):
				enterAction();
				break;

			case (KeyEvent.VK_SHIFT):
				shiftHeld = true;
				break;

			case (KeyEvent.VK_G):
				moveStart();
				break;

			case (KeyEvent.VK_R):
				rotStart();
				break;

			case (KeyEvent.VK_S):
				if (e.isShiftDown() && e.isControlDown()) {
					read();
					break;
				}
				if (e.isControlDown()) {
					save();
					break;
				}
				scaleStart();
				break;

			case KeyEvent.VK_P:
				if (selectStart != null) {
					renderImage(new VecRect(selectStart.added(selectEnd).multed(0.5F), selectEnd.subbed(selectStart)));
				}
				break;

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

	public void renderImage(VecRect rect) { // assumes resolution
    	float width = rect.getWidth(), height = rect.getHeight();
		Point resolution = (width / 16 > height / 9) ? new Point(targetResolution.x, (int)(height / width * targetResolution.x)) : new Point((int)(width / height * targetResolution.y), targetResolution.y); // TODO: fix
		renderImage(rect, resolution);
	}

	public void renderImage(VecRect rect, Point resolution) {
		BufferedImage image = new BufferedImage(resolution.x, resolution.y, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();

		if (antialiasRender) ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Camera cam = new Camera(rect.getPos().x, rect.getPos().y, 1);
		cam.setCenter(new Vector(resolution.x / 2F, resolution.y / 2F));
		cam.setScale(resolution.x / rect.getSize().x);

		int erase = new Color(0, 0, 0, 0).getRGB();
		IntStream.range(0, resolution.x).forEach(x -> IntStream.range(0, resolution.y).forEach(y -> image.setRGB(x, y, erase)));

		for (PolyLayer layer : layers) {
			for (Polygon poly : layer.getPolygons()) {
				poly.render(g, cam);
			}
		}

		FileUtil.imageToPNG(image);
	}

	@Override
	public void keyUp(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_SHIFT:
				shiftHeld = false;
				break;
		}
	}

	private void save() {
		FileUtil.writeTextFile("C:/tmp/data.txt", JsonWriter.objectToJson(layers));
	}

	private void read() {
		String path = "C:/tmp/data.txt";
		List<PolyLayer> newLayers = (List<PolyLayer>) JsonReader.jsonToJava(FileUtil.singleLineFileContents(path));
		if (newLayers == null) {
			errorPopup("NO SUCH FILE FOUND");
			return;
		}
		layers = newLayers;
		layer = layers.get(0);
		selectedPolygons.clear();
		selectedVertices.clear();
		editPoly = null;
	}

	public void errorPopup(String errorText) {
    	uiElements.add(new ErrorPopUp(errorText));
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
				if (editPoly == null) return;

				if (editType == EditType.verts) {
					selectedVertices.clear();
					selectedVertices.addAll(preSelectSelectedVertices);

					for (Vector vert : editPoly.getVertices()) {
						if (vert.between(selectStart, selectEnd)) {
							selectedVertices.add(vert);
						}
					}
				} else { // Edge mode
					selectedEdges.clear();
					selectedEdges.addAll(preSelectSelectedEdges);

					for (Edge edge : editPoly.getStoredEdges()) {
						if (edge.getStart().between(selectStart, selectEnd) && edge.getEnd().between(selectStart, selectEnd)) {
							selectedEdges.add(edge);
						}
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
	public void update(float deltaTime) {

    	tool.update(this);

    	if (editPoly != null && editPoly.getVertices().size() == 0) {
    		selectedPolygons.remove(editPoly);
			deletePoly(editPoly);
			editPoly = null;
		}

		for (int i = 0; i < uiElements.size(); i++) {
			UIElement element = uiElements.get(i);
			if (element.shouldRemove()) {
				uiElements.remove(i);
				i--;
				continue;
			}
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
		for (UIElement element : uiElements) {
			element.render(g, camera);
		}
		if (hasTypedNum()) {
			g.setColor(Color.WHITE);
			g.drawString(typedNumString, Main.WIDTH - 400, 50);
		}

		if (selectedVertices.size() > 0) {
			g.setColor(Color.WHITE);
			g.drawString("verts held: " + selectedVertices.size(), Main.WIDTH - 500, 100);
		}

    }

	public void renderLayers(Graphics g) {
	    for (PolyLayer layer : layers) {
	        for (Polygon poly : layer.getPolygons()) {
                poly.render(g, camera);
            }
        }

	    if (objectMode()) {
			for (Polygon polygon : selectedPolygons) {
				polygon.renderEdges(g, camera, e -> Color.ORANGE);
			}
		} else { // EDIT MODE
	    	if (editPoly == null) return;

	    	if (editType == EditType.verts) {
				editPoly.renderEdges(g, camera, e -> Color.ORANGE);
				editPoly.renderVerts(g, camera, p -> selectedVertices.contains(p) ? Colors.midOrange : Colors.nearBlack);
			} else {
				editPoly.renderEdges(g, camera, e -> selectedEdges.contains(e) ? Colors.midOrange : Colors.nearBlack);
			}
	    	editPoly.renderModifierGizmos(g, camera);
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

	public void setEditType(EditType editType) {
		this.editType = editType;
	}

	public EditType getEditType() {
		return editType;
	}

	public Color getSelectedColor() {
		return selectedColor;
	}
}
