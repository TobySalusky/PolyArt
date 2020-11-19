package game.building;

import game.Models;
import game.entities.part.Part;
import perspective.Camera;
import poly.Model;
import util.Gizmo;
import util.Vector;

import java.awt.Color;
import java.awt.Graphics;

public class SnapPoint {

	public Vector point;
	public Part snapped;
	public Part.partTypes type;

	public SnapPoint(Vector point, Part.partTypes type) {
		this.point = point;
		this.type = type;
	}

	public boolean shouldSnap(Part part) {
		return part.getPartType() == type && (part.snapPoint.subbed(point).mag() < 60 || part.model.pointCollision(point));
	}

	public void render(Graphics g, Camera camera) {

		if (snapped == null) {
			Model model = Models.addButton;
			model.recenter();
			Vector off = point.added(Vector.up.multed(20));
			model.polygons.forEach(p -> p.getVertices().forEach(v -> v.add(off)));
			model.render(g, camera);
		}
	}
}
