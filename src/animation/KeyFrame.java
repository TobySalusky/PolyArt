package animation;

import poly.Polygon;

public class KeyFrame {

	private Polygon pose;
	protected float time;

	public KeyFrame(Polygon polygon, float time) {
		copyPose(polygon); // ?
		this.time = time;
	}

	public void copyPose(Polygon polygon) {
		pose = polygon.cloneGeom();
	}

	public Polygon getPose() {
		return pose;
	}

	public float getTime() {
		return time;
	}
}
