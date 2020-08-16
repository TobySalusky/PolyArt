package transformation;

import util.Vector;

public class MoveTransform extends Transform {

	private final Vector diff;

	public MoveTransform(Vector diff) {
		this.diff = diff;
	}

	@Override
	public void apply(Vector vert) {
		vert.add(diff);
	}

	@Override
	public Transform reverse() {
		return new MoveTransform(diff.inverse());
	}
}
