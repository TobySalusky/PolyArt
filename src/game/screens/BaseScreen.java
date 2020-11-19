package game.screens;

import game.Game;
import game.Models;
import game.building.SnapPoint;
import game.entities.Cloud;
import game.entities.NPC;
import game.entities.Plane;
import game.entities.SimulateButton;
import game.entities.part.*;
import perspective.Camera;
import poly.Model;
import poly.Polygon;
import util.Gizmo;
import util.Maths;
import util.Vector;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BaseScreen implements GameScreen {

	// objects
	private final Camera camera = new Camera(1920 / 2F, 1080 / 2F, 1);
	private final List<Part> parts = new ArrayList<>();
	private final List<Cloud> clouds = new ArrayList<>();
	private final Model background;

	private final Font font = new Font("Helvetica", Font.BOLD, 38);

	public static List<SnapPoint> snapPoints = new ArrayList<>();
	private SnapPoint bodySnap = new SnapPoint(new Vector(1920/2F, 1080/2F), Part.partTypes.body);

	// simulation
	private List<Part> planeParts = new ArrayList<>();
	public float weight, lift, drag, thrust;
	public static boolean simulating;
	public float simulTime;
	public Vector accel = new Vector();

	public Plane plane;
	public Model planeModel;

	public NPC npc;

	public SimulateButton button = new SimulateButton(new Vector(130, 1000));

	public static boolean triggerSim = false;

	public BaseScreen() {
		background = Models.background;

		parts.add(new Body.PassengerBody(new Vector(500, 0)));

		parts.add(new Head.PassengerHead(new Vector(500, 0)));

		parts.add(new Tail.PassengerTail(new Vector(500, 0)));

		parts.add(new Topper.HelicopterBlades(new Vector(500, 0)));

		parts.add(new Wings.PassengerWings(new Vector(500, 0)));

		parts.add(new Head(new Vector(500, 0), Models.jetHead));
		parts.add(new Tail(new Vector(500, 0), Models.jetTail));
		parts.add(new Body(new Vector(500, 0), Models.jetBody));
		parts.add(new Wings(new Vector(500, 0), Models.jetWings));
		parts.add(new Topper(new Vector(500, 0), Models.jetTopper));

		genClouds();

		npc = genNpc();
	}

	public void genClouds() {
		clouds.clear();
		for (int i = 0; i < 7; i++) {
			clouds.add(new Cloud(Maths.random(1920)));
		}
	}

	public NPC genNpc() {
		int npcNum = Maths.randomInt(5);

		switch (npcNum) {
			case 0:
				return new NPC.Kalpana(new Vector(1720, 950));
			case 1:
				return new NPC.Mae(new Vector(1720, 950));
			case 2:
				return new NPC.Guion(new Vector(1720, 950));
			case 3:
				return new NPC.Leroy(new Vector(1720, 950));
			case 4:
				return new NPC.Sylvia(new Vector(1720, 950));
		}
		return null;
	}

	public void calcWeights() {
		collectParts();

		weight = 0;
		lift = 0;
		drag = 0;
		thrust = 0;

		for (Part part : planeParts) {
			weight += part.weight;
			lift += part.lift;
			drag += part.drag;
			thrust += part.thrust;
		}
	}

	public void collectParts() {
		planeParts.clear();
		for (SnapPoint snapPoint : snapPoints) {
			if (snapPoint.snapped != null) {
				planeParts.add(snapPoint.snapped);
			}
		}
	}

	public void genSnaps() {
		snapPoints.clear();

		snapPoints.add(bodySnap);
		for (int i = 0; i < snapPoints.size(); i++) {
			SnapPoint snapPoint = snapPoints.get(i);

			if (snapPoint.snapped != null) {
				snapPoints.addAll(snapPoint.snapped.snapPoints);
			}
		}
	}

	public void handleClouds() {
		if (clouds.size() < 7) {
			clouds.add(new Cloud(-100));
		}

		for (int i = 0; i < clouds.size(); i++) {
			Cloud cloud = clouds.get(i);
			float x = cloud.getPos().x;
			if (x > 2100) {
				clouds.remove(i);
				i--;
			}
		}
	}

	@Override
	public void update(float deltaTime) {

		genSnaps();

		if (!simulating) {
			calcWeights();
		} else {

			float xForce = thrust - drag;
			float yForce = lift - weight;


			if (simulTime > 3) {
				accel = new Vector(-xForce * 30 - 20, -30 + -yForce * 30);

				float start = 120;
				float velX = plane.getVel().x * -1;
				if (velX < start) {
					accel.y = 80 * ((start - velX) / start);

					System.out.println(velX + " " + plane.getVel().y);
				}
			} else if (simulTime > 2F) {
				accel = new Vector(-150, -100);
			} else if (simulTime > 1) {
				accel = new Vector(-300, 0);
			} else {
				accel = new Vector();
			}

			if (simulTime > 1 && plane.getPos().y > 690) {
				accel.add(Vector.right.multed(100));
			}

			plane.getVel().add(accel.multed(deltaTime));
			plane.getVel().x = Math.min(plane.getVel().x, 0);
			plane.update(deltaTime);

			plane.getPos().y = Math.min(700, plane.getPos().y);

			if (simulTime > 9) {
				stopSim();
			}
		}

		handleClouds();

		parts.forEach(o -> o.update(deltaTime));
		clouds.forEach(o -> o.update(deltaTime));

		npc.update(deltaTime);


		if (simulating) {
			simulTime += deltaTime;
		} else if (triggerSim) {
			triggerSim = false;

			startSim();
		}
	}

	@Override
	public void render(Graphics g) {
		background.render(g, camera);

		clouds.forEach(o -> o.render(g, camera));

		if (!simulating) {
			parts.forEach(o -> o.render(g, camera));

			npc.render(g, camera);

			snapPoints.forEach(o -> o.render(g, camera));

			createMeters(g);

			button.render(g, camera);

			Gizmo.dot(g, Game.mousePos, Color.darkGray);

		} else {
			Models.road.render(g, camera);

			plane.render(g, camera);
		}
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = true;

			if (simulating) return;
			for (int i = parts.size() - 1; i >= 0; i--) {
				if (parts.get(i).tryClick(Game.mousePos)) break;
			}

			npc.tryClick(Game.mousePos);

			button.tryClick(Game.mousePos);
		}
	}

	public void startSim() {
		if (planeParts.size() == 0) return;

		genClouds();

		if (!simulating) {
			List<Polygon> polys = new ArrayList<>();
			planeParts.forEach(p -> p.model.polygons.forEach(o -> polys.add(o.cloneGeom())));

			planeModel = new Model(polys);
			planeModel.recenter();

			plane = new Plane(new Vector(1600, 700), planeModel);
		}

		simulating = true;
		simulTime = 0;
	}

	public void stopSim() {
		genClouds();
		simulating = false;
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = false;
		}
	}

	public void createMeters(Graphics g) {
		int x = 1370;
		int y = 150;
		int width = 500;
		int height = 40;

		int notchWidth = 10;

		g.setColor(new Color(150,150,150));
		g.fillRect(x,y,width,height);
		g.fillRect(x,y+200,width,height);

		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("Thrust",x,y-25);
		g.drawString("Drag", x+width-100, y-25);
		g.drawString("Lift", x, y+175);
		g.drawString("Weight", x+width-125, y+175);

		float xForce = (drag - thrust);
		float yForce = (weight - lift);

		xForce = Maths.clamp(xForce, -10, 10);
		yForce = Maths.clamp(yForce, -10, 10);

		g.setColor(new Color((int)(25.5F*Math.abs(xForce)),(255-(int)(25.5F*Math.abs(xForce))),0));
		g.fillRect((int)(x+(width/2)+(25*xForce)),y-5,notchWidth,height+10);

		g.setColor(new Color((int)(25.5F*Math.abs(yForce)),(255-(int)(25.5F*Math.abs(yForce))),0));
		g.fillRect((int)(x+(width/2)+(25*yForce)),y+195,notchWidth,height+10);
	}
}
