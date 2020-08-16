package transformation;

import util.Vector;

public class RotateTransform extends Transform {

	private final float radians;
	private final Vector around;

	public RotateTransform(float radians, Vector around) {
		this.radians = radians;
		this.around = around;
	}

	@Override
	public void apply(Vector vert) {
		Vector diff = vert.subbed(around);
		diff.rotate(radians);

		Vector to = around.added(diff);
		vert.setTo(to);

	}

	@Override
	public Transform reverse() {
		return new RotateTransform(-radians, around);
	}
}
