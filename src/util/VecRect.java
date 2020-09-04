package util;

public class VecRect {

	private final Vector pos, size;

	public VecRect(Vector center, Vector size) {
		this.pos = center;
		this.size = size;
	}

	public Vector getPos() {
		return pos;
	}

	public Vector getSize() {
		return size;
	}

	public float getX() {
		return pos.x;
	}

	public float getY() {
		return pos.y;
	}

	public float getWidth() {
		return size.x;
	}

	public float getHeight() {
		return size.y;
	}
}
