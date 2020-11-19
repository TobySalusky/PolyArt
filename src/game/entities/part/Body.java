package game.entities.part;

import game.building.SnapPoint;
import poly.Model;
import util.Vector;

public class Body extends Part {

	public Body(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.body;

		snapPoint = model.backToPointLinked(1);
		snapPoints.add(new SnapPoint(model.backToPointLinked(2), partTypes.topper));
		snapPoints.add(new SnapPoint(model.backToPointLinked(3), partTypes.tail));
		snapPoints.add(new SnapPoint(model.backToPointLinked(4), partTypes.wings));
		snapPoints.add(new SnapPoint(model.backToPointLinked(5), partTypes.head));
	}


}
