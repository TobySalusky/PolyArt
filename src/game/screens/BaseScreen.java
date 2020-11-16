package game.screens;

import game.Game;
import game.Models;
import game.entities.Cloud;
import game.entities.Part;
import game.entities.Entity;
import perspective.Camera;
import poly.Model;
import util.Gizmo;
import util.Maths;
import util.Vector;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class BaseScreen implements GameScreen {

	// objects
	private final Camera camera = new Camera(1920 / 2F, 1080 / 2F, 1);
	private final List<Entity> entities = new ArrayList<>();
	private final List<Cloud> clouds = new ArrayList<>();
	private final Model background;

	public BaseScreen() {
		background = Models.background;

		entities.add(new Part(new Vector(500, 0), Models.cockpit));

		for (int i = 0; i < 7; i++) {
			clouds.add(new Cloud(Maths.random(1920)));
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

		handleClouds();

		entities.forEach(o -> o.update(deltaTime));
		clouds.forEach(o -> o.update(deltaTime));


	}

	@Override
	public void render(Graphics g) {
		background.render(g, camera);

		clouds.forEach(o -> o.render(g, camera));
		entities.forEach(o -> o.render(g, camera));


		Gizmo.dot(g, Game.mousePos);
	}

	@Override
	public void mouseDown(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = true;

			entities.forEach(ent -> ent.tryClick(Game.mousePos));
		}
	}

	@Override
	public void mouseUp(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			Game.mouseDown = false;
		}
	}
}
