package screens;

import main.Main;
import perspective.Camera;
import poly.PolyLayer;
import poly.Polygon;
import transformation.*;
import util.Colors;
import util.Gizmo;
import util.Vector;
import util.Vertices;

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
    private Tool tool = Tool.create;

    // input
	private Vector lastMousePos;
	private boolean leftClickDown, middleClickDown, rightClickDown;

    // non main
	private final List<Vector> selectedVertices = new ArrayList<>();
	private Transform currentTransform;
	private boolean transforming = false;
	private TransformType transformType;
	private Vector transformStart; // world position
	private Vector transformPivot; // world position
	private Axis lockAxis;

	private boolean numListen = false;
	private String typedNumString = "";
	private float typedNum;

    public PolyScreen() {
        layer = new PolyLayer();
        layers.add(layer);
    }

	private enum Mode {
	    object, edit
    }

    private enum EditType {
    	verts, edges
	}

    private enum Tool {
	    select, create
    }

    private enum Axis {
    	x, y
	}

    private enum TransformType {

    	move {
			@Override
			public Transform genTransform(PolyScreen screen, Vector newPos) {
				Vector diff = newPos.subbed(screen.transformStart);

				if (screen.lockAxis == Axis.x) {
					diff.x = 0;
					if (screen.hasTypedNum()) diff.y = screen.typedNum;
				} else if (screen.lockAxis == Axis.y) {
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
				if (screen.lockAxis == Axis.x) {
					scalarX = 1;
					scalarY = newPos.subbed(screen.transformPivot).multed2(0, 1).mag() / screen.transformStart.subbed(screen.transformPivot).multed2(0, 1).mag();
					if (screen.hasTypedNum()) scalarY = screen.typedNum;
				} else if (screen.lockAxis == Axis.y) {
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
			if (screen.lockAxis == Axis.x) {
				to.x = from.x;
			} else if (screen.lockAxis == Axis.y) {
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

	public void leftClickDown(MouseEvent e) {
		if (transforming) {
			endTransform();
			return;
		}
		if (mode == Mode.object) {
			if (!e.isShiftDown()) {
				selectedPolygons.clear();
				editPoly = null;
			}

			Vector vec = camera.toWorld(mousePos(e));
			for (PolyLayer layer : layers) {
				for (Polygon polygon : layer.getPolygons()) {
					if (polygon.pointInside(vec)) {
						editPoly = polygon;
						selectedPolygons.add(editPoly);
						break;
					}
				}
			}

			return;
		}

		if (tool == Tool.create) {
			if (editPoly == null) {
				editPoly = new Polygon(Color.WHITE);
				layer.getPolygons().add(editPoly);

				selectedPolygons.clear(); // TODO: ??
				selectedPolygons.add(editPoly);
			}
			Vector pos = camera.toWorld(mousePos(e));
			editPoly.addPoint(pos);
		}
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
				break;
			case (MouseEvent.BUTTON2):
				middleClickDown = false;
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
		}
	}

    public void enterAction() {
		editPoly = null;

		endTransform();
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

    	lockAxis = null;

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
					mode = Mode.object;
				}
				break;

			case (KeyEvent.VK_Z):
			case (KeyEvent.VK_Y):
				if (transforming) {
					lockAxis = Axis.x;
					updateTransform(lastMousePos);
				}
				break;

			case (KeyEvent.VK_X):

				if (transforming) {
					lockAxis = Axis.y;
					updateTransform(lastMousePos);
					break;
				}
			case (KeyEvent.VK_DELETE):
				deleteAction();
				break;

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

			case (KeyEvent.VK_SPACE):
				if (editMode()) {
					if (editType == EditType.verts) {
						editType = EditType.edges;
					} else {
						editType = EditType.verts;
					}
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

		Vector delta = (lastMousePos == null) ? new Vector() : mousePos(e).subbed(lastMousePos);
		lastMousePos = mousePos(e);

		if (middleClickDown) {
			camera.move(delta.inverse());
		}
    }

	@Override
	public void update() {

	}

	@Override
	public void render(Graphics g) {
		Gizmo.fillScreen(g, Colors.background);

		renderLayers(g);

		if (transformType != null) {
			transformType.renderGizmos(g, this);
		}

		// TODO: ui elements
		if (hasTypedNum()) {
			g.setColor(Color.WHITE);
			g.drawString(typedNumString, Main.WIDTH - 30, 50);
		}
		g.setColor(Color.ORANGE);
		g.fillRect(10 + ((mode == Mode.edit) ? 30 : 0), 10, 30, 30);
	}

	public void renderLayers(Graphics g) {
	    for (PolyLayer layer : layers) {
	        for (Polygon poly : layer.getPolygons()) {
                poly.render(g, camera, selectedPolygons.contains(poly), editPoly == poly);
            }
        }
    }

    public boolean objectMode() {
    	return mode == Mode.object;
	}

	public boolean editMode() {
    	return mode == Mode.edit;
	}
}
