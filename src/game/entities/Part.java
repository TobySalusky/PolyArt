package game.entities;

import game.Game;
import poly.Model;
import util.Maths;
import util.Vector;

public class Part extends Entity {

	private static final float gravity = -750F, groundLevel = 900;

	private boolean held = false;

	public Part(Vector pos, Model model) {
		super(pos);
		this.model = model;
	}

	public void clicked(Vector pos) {
		held = true;
	}

	@Override
	public void update(float deltaTime) {
		vel.add(Vector.down.multed(gravity * deltaTime));

		if (!Game.mouseDown) {
			held = false;
		}

		if (held || pos.y >= groundLevel) {
			vel = new Vector();
		}

		if (held) {
			pos.setTo(Game.mousePos);
		}

		pos.y = Math.min(pos.y, groundLevel);

		super.update(deltaTime);
	}
}
