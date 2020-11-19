package game.entities;

import poly.Model;
import util.Vector;

public class Plane extends Entity {

	public Plane(Vector pos, Model model) {
		super(pos);
		this.model = model;
	}
}
