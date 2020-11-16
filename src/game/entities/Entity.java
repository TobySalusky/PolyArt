package game.entities;

import game.base.GameObject;
import perspective.Camera;
import poly.Model;
import poly.Polygon;
import util.Generator;
import util.Maths;
import util.Polar;
import util.Vector;

import java.awt.Graphics;

public class Entity implements GameObject {

	protected Vector pos, vel = new Vector();
	protected Model model;
	private Vector lastPos = new Vector();

	public Entity(Vector pos) {
		this.pos = pos.copy();
	}

	@Override
	public void update(float deltaTime) {
		pos.add(vel.multed(deltaTime));
	}

	public void tryClick(Vector pos) {
		if (model.pointCollision(pos)) {
			clicked(pos);
		}
	}

	public void clicked(Vector pos) {

	}

		@Override
	public void render(Graphics g, Camera camera) {

		model.polygons.forEach(p -> p.getVertices().forEach(v -> v.sub(lastPos)));
		model.polygons.forEach(p -> p.getVertices().forEach(v -> v.add(pos)));
		lastPos = pos.copy();

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
