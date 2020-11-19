package game.entities.part;

import game.building.SnapPoint;
import poly.Model;
import util.Vector;

public class Wings extends Part {

	public Wings(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.wings;

		snapPoint = model.backToPointLinked(1);
	}

}
