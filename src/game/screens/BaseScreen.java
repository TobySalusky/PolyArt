package game.screens;

import game.Game;
import game.Models;
import game.TextUtil;
import game.building.SnapPoint;
import game.entities.Cloud;
import game.entities.NPC;
import game.entities.part.Body;
import game.entities.part.Part;
import game.entities.Entity;
import game.entities.part.Wings;
import perspective.Camera;
import poly.Model;
import util.Gizmo;
import util.Maths;
import util.Vector;

import java.awt.Color;
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

	public static List<SnapPoint> snapPoints = new ArrayList<>();
	private SnapPoint bodySnap = new SnapPoint(new Vector(1920/2F, 1080/2F), Part.partTypes.body);

	// simulation
	private List<Part> plane = new ArrayList<>();
	public float weight, lift, drag, thrust;
	public boolean simulating;
	public float simulTime;

	public NPC npc;

	public BaseScreen() {
		background = Models.background;

		parts.add(new Part(new Vector(500, 0), Models.cockpit));
		parts.add(new Body(new Vector(500, 0), Models.bodyTest));
		parts.add(new Wings(new Vector(500, 0), Models.wingsTest));

		for (int i = 0; i < 7; i++) {
			clouds.add(new Cloud(Maths.random(1920)));
		}

		npc = new NPC.Kalpana(new Vector(1720, 950));
	}

	public void startSimulation() {
		collectParts();

		for (Part part : parts) {
			weight += part.weight;
			lift += part.lift;
			drag += part.drag;
			thrust += part.thrust;
		}
	}

	public void collectParts() {
		plane.clear();
		for (SnapPoint snapPoint : snapPoints) {
			if (snapPoint.snapped != null) {
				plane.add(snapPoint.snapped);
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

	public void handleSnaps() {



	}

	@Override
	public void update(float deltaTime) {

		genSnaps();
		handleSnaps();

		handleClouds();

		parts.forEach(o -> o.update(deltaTime));
		clouds.forEach(o -> o.update(deltaTime));

		npc.update(deltaTime);


		if (simulating) {
			simulTime += deltaTime;
		}
	}

	@Override
	public void render(Graphics g) {
		background.render(g, camera);

		clouds.forEach(o -> o.render(g, camera));
		parts.forEach(o -> o.render(g, camera));
		npc.render(g, camera);


		Gizmo.dot(g, Game.mousePos, Color.darkGray);

		snapPoints.forEach(o -> o.render(g, camera));
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = true;

			for (int i = parts.size() - 1; i >= 0; i--) {
				if (parts.get(i).tryClick(Game.mousePos)) break;
			}

			npc.tryClick(Game.mousePos);
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			System.out.println("mouse " + Game.mousePos);
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = false;
		}
	}
}
