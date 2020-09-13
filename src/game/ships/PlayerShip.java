package game.ships;

import util.Vector;

public class PlayerShip extends SpaceObject {

	public PlayerShip(Vector pos) {
		super(pos);
	}

	public void handleInput(int xAxis, int yAxis, float deltaTime) {
		Vector norm = new Vector(xAxis, yAxis);
		if (norm.mag() > 0) {
			norm = norm.normed();
		}

		vel.add(norm.multed(300 * deltaTime));
	}
}
