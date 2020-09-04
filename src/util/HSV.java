package util;

public class HSV {

	// 0 < hue < 360
	// S and V between 0 and 1
	private final float hue, saturation, value;

	public HSV(float hue, float saturation, float value) {
		this.hue = hue;
		this.saturation = saturation;
		this.value = value;
	}

	public float getHue() {
		return hue;
	}

	public float getSaturation() {
		return saturation;
	}

	public float getValue() {
		return value;
	}
}
