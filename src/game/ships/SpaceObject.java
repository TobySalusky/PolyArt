package game.ships;

import game.base.GameObject;
import perspective.Camera;
import poly.Polygon;
import util.Generator;
import util.Maths;
import util.Polar;
import util.Vector;

import java.awt.Graphics;

public class SpaceObject implements GameObject {

	protected Vector pos, vel = new Vector();
	protected float angle;
	protected Polygon model;

	public SpaceObject(Vector pos) {
		this.pos = pos.copy();
	}

	@Override
	public void update(float deltaTime) {
		pos.add(vel.multed(deltaTime));
		float toAngle = vel.angle();
		angle = Maths.correctAngle(angle, toAngle);
		angle += (toAngle - angle) * deltaTime * 5;

		applyFriction(deltaTime);
	}

	protected void applyFriction(float deltaTime) {
		Vector friction = new Polar(vel.mag() * 0.9F * deltaTime, vel.angle());
		vel.sub(friction);
	}

	@Override
	public void render(Graphics g, Camera camera) {
		model = unbakedModel().cloneGeom();
		model.getVertices().forEach(v -> {
			v.rotateAround(angle, Vector.zero);
			v.add(pos);
		});
		model.render(g, camera);
	}

	public Polygon unbakedModel() {
		return Generator.genSquare(Vector.zero, 10);
	}

	public Vector getPos() {
		return pos;
	}

	public Vector getVel() {
		return vel;
	}
}
