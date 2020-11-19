package game.screens;

import game.Game;
import game.Models;
import game.TextUtil;
import game.building.SnapPoint;
import game.entities.Cloud;
import game.entities.NPC;
import game.entities.part.*;
import game.entities.Entity;
import perspective.Camera;
import poly.Model;
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
	private List<Part> plane = new ArrayList<>();
	public float weight, lift, drag, thrust;
	public boolean simulating;
	public float simulTime;

	public NPC npc;

	public BaseScreen() {
		background = Models.background;

		parts.add(new Part(new Vector(500, 0), Models.cockpit));
		parts.add(new Body(new Vector(500, 0), Models.passengerBody));
		parts.add(new Wings(new Vector(500, 0), Models.passengerWings));
		parts.add(new Tail(new Vector(500, 0), Models.passengerTail));
		parts.add(new Head(new Vector(500, 0), Models.passengerHead));

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

		createMeters(g);
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

		weight = 11;
		thrust = 17;
		drag = 8;
		lift = 6;

		float xForce = (thrust-drag);
		float yForce = (lift-weight);

		g.setColor(new Color((int)(25.5F*Math.abs(xForce)),(255-(int)(25.5F*Math.abs(xForce))),0));
		g.fillRect((int)(x+(width/2)+(25*xForce)),y-5,notchWidth,height+10);

		g.setColor(new Color((int)(25.5F*Math.abs(yForce)),(255-(int)(25.5F*Math.abs(yForce))),0));
		g.fillRect((int)(x+(width/2)+(25*yForce)),y+195,notchWidth,height+10);
	}
}
