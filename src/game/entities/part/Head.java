package game.entities.part;

import game.Models;
import game.building.SnapPoint;
import poly.Model;
import util.Vector;

public class Head extends Part {

	public Head(Vector pos, Model model) {
		super(pos, model);

		partType = partTypes.head;

		snapPoint = model.backToPointLinked(1);
		snapPoints.add(new SnapPoint(model.backToPointLinked(2), partTypes.front));
	}

	public static class PassengerHead extends Head {

		public PassengerHead(Vector pos, Model model) {
			super(pos, Models.passengerHead);
			weight = 2;
			lift = 0;
			thrust = 0;
			drag = 5;
		}
	}
}
