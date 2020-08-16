package transformation;

import util.Vector;

import java.util.List;

public class ZeroDivScaleTransform extends ScaleTransform {

	private Transform rev;

	public ZeroDivScaleTransform(float scalarX, float scalarY, Vector around) {
		super(scalarX, scalarY, around);
	}


	@Override
	public void apply(List<Vector> verts) {
		rev = new SetAllTransform(verts);
		System.out.println("div zed!!!");
		super.apply(verts);
	}

	@Override
	public Transform reverse() {
		return rev;
	}
}
