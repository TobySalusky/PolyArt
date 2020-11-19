package game.entities.part;

import game.Models;
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

	public static class PassengerBody extends Body {

		public PassengerBody(Vector pos) {
			super(pos, Models.passengerBody);
			weight = 5;
			drag = 1;
			lift = 0;
			thrust = 0;

		}
	}

	public static class HelicopterBody extends Body {

		public HelicopterBody(Vector pos) {
			super(pos, Models.helicopterBody);

			// todo: sam
			


			snapPoints.clear();
			snapPoint = model.backToPointLinked(1);
			snapPoints.add(new SnapPoint(model.backToPointLinked(2), partTypes.topper));
			snapPoints.add(new SnapPoint(model.backToPointLinked(3), partTypes.tail));
		}
	}


}
