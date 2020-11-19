package game.entities.part;

import game.Models;
import poly.Model;
import util.Vector;

public class Topper extends Part {

	public Topper(Vector pos, Model model) {
		super(pos, model);
		partType = partTypes.topper;

		snapPoint = model.backToPointLinked(1);
	}

	public static class HelicopterBlades extends Topper {

		public HelicopterBlades(Vector pos) {
			super(pos, Models.helicopterBlades);

			clickAssist = true;

			weight = 1F;
			lift = 7;
			thrust = 3;
			drag = 1;
		}
	}

	public static class JetTopper extends Topper {

		public JetTopper(Vector pos) {
			super(pos, Models.jetTopper);

			weight = 1;
			lift = 0;
			thrust = 0;
			drag = 1;
		}
	}
}
