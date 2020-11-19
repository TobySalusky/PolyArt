package game.entities.part;

import poly.Model;
import util.Vector;

public class Tail extends Part {

	public Tail(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.tail;

		snapPoint = model.backToPointLinked(1);
	}

}
