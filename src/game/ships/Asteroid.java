package game.ships;

import poly.Polygon;
import util.Generator;
import util.Maths;
import util.Polar;
import util.Vector;

public class Asteroid extends SpaceObject {

	public Asteroid(Vector pos) {
		super(pos);

		vel = new Polar(Maths.random(5, 30), Maths.randomAngle());
	}

	public Polygon unbakedModel() {
		return Generator.genSquare(Vector.zero, 50);
	}

	@Override
	protected void applyFriction(float deltaTime) {
	}
}
