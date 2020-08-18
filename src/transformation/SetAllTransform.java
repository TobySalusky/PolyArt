package transformation;

import util.Vector;

import java.util.ArrayList;
import java.util.List;

public class SetAllTransform extends Transform {

	private final List<Vector> vertSave = new ArrayList<>();

	public SetAllTransform(List<Vector> verts) {
		for (Vector vector : verts) {
			vertSave.add(vector.copy());
		}
	}

	@Override
	public void apply(List<Vector> verts) {
		assert verts.size() == vertSave.size();
		for (int i = 0; i < verts.size(); i++) {
			verts.get(i).setTo(vertSave.get(i));
		}
	}

	@Override
	public void apply(Vector vert) {
		System.out.println("SetAllTransform: should not be used independently");
	}

	@Override
	public Transform reverse() {
		return null;
	}
}
