package game.entities.part;

import game.building.SnapPoint;
import poly.Model;
import util.Vector;

public class Head extends Part {

	public Head(Vector pos, Model model) {
		super(pos, model);

		partType = partTypes.wings;

		snapPoint = model.backToPointLinked(1);
		snapPoints.add(new SnapPoint(model.backToPointLinked(2), partTypes.front));
	}
}
