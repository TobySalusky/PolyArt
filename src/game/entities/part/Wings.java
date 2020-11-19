package game.entities.part;

import game.Models;
import game.building.SnapPoint;
import poly.Model;
import util.Vector;

public class Wings extends Part {

	public Wings(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.wings;

		snapPoint = model.backToPointLinked(1);
	}

	public static class PassengerWings extends Wings {

		public PassengerWings(Vector pos) {
			super(pos, Models.passengerWings);
			weight = 2;
			drag = 2;
			lift = 11;
			thrust = 9;
		}
	}

	public static class JetWings extends Wings {

		public JetWings(Vector pos) {
			super(pos, Models.jetWings);
			weight = 1;
			drag = 1;
			lift = 9;
			thrust = 1;
		}
	}

}
