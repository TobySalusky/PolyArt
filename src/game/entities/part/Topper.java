package game.entities.part;

import poly.Model;
import util.Vector;

public class Topper extends Part {

	public Topper(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.topper;

		snapPoint = model.backToPointLinked(1);
	}

}
