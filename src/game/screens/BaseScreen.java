package game.screens;

import game.Driver;
import game.ships.Asteroid;
import game.ships.PlayerShip;
import game.ships.SpaceObject;
import perspective.Camera;
import util.Colors;
import util.Maths;
import util.Vector;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class BaseScreen implements GameScreen {

	// input
	private boolean wDown, aDown, sDown, dDown;

	// objects
	private final Camera camera = new Camera(0, 0, 5);
	private PlayerShip player = new PlayerShip(Vector.zero);
	private List<SpaceObject> spaceObjects = new ArrayList<>();

	public BaseScreen() {
		float spawnRange = 500;
		IntStream.range(0, 10).forEach(i -> spaceObjects.add(new Asteroid(new Vector(Maths.random(-spawnRange, spawnRange), Maths.random(-spawnRange, spawnRange)))));

	}

	@Override
	public void update(float deltaTime) {
		handlePlayerInput(deltaTime);

		player.update(deltaTime);

		spaceObjects.forEach(o -> o.update(deltaTime));

		camera.move(player.getPos().subbed(camera.copyPos()));
	}

	private void handlePlayerInput(float deltaTime) {
		int xAxis = 0, yAxis = 0;

		if (aDown) xAxis--;
		if (dDown) xAxis++;

		if (wDown) yAxis--;
		if (sDown) yAxis++;

		player.handleInput(xAxis, yAxis, deltaTime);
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Colors.background);
		g.fillRect(0, 0, Driver.WIDTH, Driver.HEIGHT);

		spaceObjects.forEach(o -> o.render(g, camera));
		player.render(g, camera);
	}

	@Override
	public void keyDown(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				wDown = true;
				break;
			case KeyEvent.VK_A:
				aDown = true;
				break;
			case KeyEvent.VK_S:
				sDown = true;
				break;
			case KeyEvent.VK_D:
				dDown = true;
				break;
		}
	}

	@Override
	public void keyUp(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				wDown = false;
				break;
			case KeyEvent.VK_A:
				aDown = false;
				break;
			case KeyEvent.VK_S:
				sDown = false;
				break;
			case KeyEvent.VK_D:
				dDown = false;
				break;
		}
	}
}
