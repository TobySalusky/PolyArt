package game.entities.part;

import poly.Model;
import util.Vector;

public class Front extends Part {

	public Front(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.front;

		snapPoint = model.backToPointLinked(1);
	}

}
