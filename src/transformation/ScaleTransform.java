package transformation;

import util.Vector;

public class ScaleTransform extends Transform {

	private final float scalarX, scalarY;
	private final Vector around;

	//public ScaleTransform() {}

	public ScaleTransform(float scalarX, float scalarY, Vector around) {
		this.scalarX = scalarX;
		this.scalarY = scalarY;
		this.around = around;
	}

	@Override
	public void apply(Vector vert) {
		Vector to = around.added(vert.subbed(around).multed2(scalarX, scalarY));
		vert.setTo(to);
	}

	@Override
	public Transform reverse() {
		return new ScaleTransform(1 / scalarX, 1 / scalarY, around);
	}
}
