package game.entities;

import game.Models;
import util.Maths;
import util.VecRect;
import util.Vector;

public class Cloud extends Entity {

	public Cloud(float x) {
		super(new Vector(x, 100));

		model = Models.clouds[Maths.randomInt(Models.clouds.length)].copy();

		VecRect rect = model.findRect();
		float area = rect.getWidth() * rect.getHeight();

		float mult = area / 20000F;

		vel.x = (200 + 800 * mult * Maths.random(0.8F, 1.2F)) * 0.1F;
		pos.y += 30 / mult * Maths.random(0.8F, 1.2F) + Maths.random(-30, 100);
	}

}
