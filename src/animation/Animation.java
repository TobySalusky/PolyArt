package animation;

import poly.Polygon;

import java.util.ArrayList;
import java.util.List;

public class Animation {

	private float time = 5F;
	private boolean playing = false;
	private final List<PolyAnimation> polyAnims = new ArrayList<>();

	public void update(float deltaTime) {
		if (playing) {
			time += deltaTime;
			time %= 10; // DEBUGGING
			setPoses(time);
		}
	}

	public void key(Polygon polygon) {
		PolyAnimation anim = null;
		for (PolyAnimation pAnim : polyAnims) {
			if (pAnim.polygon == polygon) {
				anim = pAnim;
				break;
			}
		}
		if (anim == null) {
			anim = new PolyAnimation(polygon);
			polyAnims.add(anim);
		}

		anim.insertKey(polygon, time);
	}

	public void togglePlaying() {
		playing = !playing;
	}

	public void setPoses(float time) {
		polyAnims.forEach(p -> p.updateTo(time));
	}

	public List<PolyAnimation> getPolyAnims() {
		return polyAnims;
	}

	public void changeTime(float time) {
		this.time = time;
		setPoses(time);
	}

	public float getTime() {
		return time;
	}
}