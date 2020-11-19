package game.entities.part;

import game.Models;
import poly.Model;
import util.Vector;

public class Tail extends Part {

	public Tail(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.tail;

		snapPoint = model.backToPointLinked(1);
	}

	public static class PassengerTail extends Tail {

		public PassengerTail(Vector pos) {
			super(pos, Models.passengerTail);
			drag = 1;
			lift = 0;
			weight = 2;
			thrust = 0;
		}
	}

	public static class JetTail extends Tail {

		public JetTail(Vector pos) {
			super(pos, Models.jetTail);
			drag = 2;
			lift = 0;
			weight = 3;
			thrust = 4;
		}
	}

}
