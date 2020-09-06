package animation;

import poly.Polygon;
import util.Vector;

import java.util.ArrayList;
import java.util.List;

public class PolyAnimation {

	protected final Polygon polygon;

	private final List<KeyFrame> frames = new ArrayList<>(2);

	private int last;

	public PolyAnimation(Polygon polygon) {
		this.polygon = polygon;
	}

	public void updateTo(float time) {
		if (time <= frames.get(0).time || frames.size() == 1) {
			setPoseTo(frames.get(0));
		} else if (time >= frames.get(frames.size() - 1).time) {
			setPoseTo(frames.get(frames.size() - 1));
		} else {
			if (time < frames.get(last).time || time >= frames.get(last + 1).time) {
				findIndex(time);
			}
			KeyFrame from = frames.get(last), to = frames.get(last + 1);
			tweenBetween(to, from, (time - from.time) / (to.time - from.time));
		}
	}

	public void insertKey(Polygon polygon, float time) {

		KeyFrame newFrame = new KeyFrame(polygon, time);

		if (frames.size() == 0 || time < frames.get(0).time) {
			frames.add(0, newFrame);
		}

		for (int i = 0; i < frames.size(); i++) {
			float frameTime = frames.get(i).time;
			if (time == frameTime) {
				frames.set(i, newFrame);
			} else if (time > frameTime) {
				frames.add(i + 1, newFrame);
			}
		}
	}

	public void findIndex(float time) {
		for (int i = 0; i < frames.size(); i++) {
			if (time >= frames.get(i).time && time < frames.get(i + 1).time) {
				last = i;
				return;
			}
		}
	}

	public void setPoseTo(KeyFrame frame) {
		List<Vector> verts = polygon.getVertices(), toVerts = frame.getPose().getVertices();
		for (int i = 0; i < verts.size(); i++) {
			verts.get(i).setTo(toVerts.get(i));
		}
	}

	public void tweenBetween(KeyFrame f1, KeyFrame f2, float percent) { // LINEAR

		List<Vector> verts = polygon.getVertices(), verts1 = f1.getPose().getVertices(), verts2 = f2.getPose().getVertices();
		for (int i = 0; i < verts.size(); i++) {
			verts.get(i).setTo(verts1.get(i).multed(percent).added(verts2.get(i).multed(1 - percent)));
		}
	}

	public List<KeyFrame> getFrames() {
		return frames;
	}
}
