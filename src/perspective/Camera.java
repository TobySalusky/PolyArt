package perspective;

import main.Main;
import util.Vector;

public class Camera {

    private Vector pos;
    private float scale = 1F;

    private static final Vector center = new Vector(Main.WIDTH / 2F, Main.HEIGHT / 2F);

    public Camera(float x, float y) {
        this.pos = new Vector(x, y);
    }

    public void move(Vector diff) {
        pos.add(diff);
    }

    public final Vector toWorld(Vector vector) {
        return new Vector(toWorldX(vector.x), toWorldY(vector.y));
    }

    public final Vector toWorld(float x, float y) {
        return new Vector(toWorldX(x), toWorldY(y));
    }

    public final float toWorldX(float x) {
        return (x - center.x) / scale + getX();
    }

    public final float toWorldY(float y) {
        return (y - center.y) / scale + getY();
    }

    public final Vector toScreen(float x, float y) {
        return new Vector(getRenderX(x), getRenderY(y));
    }

    public final Vector toScreen(Vector vec) {
        return toScreen(vec.x, vec.y);
    }

    /**
     * Returns the rendering X position for an object
     *
     * @param x Object's X position
     * @return Graphics friendly rendering number
     */
    public float getRenderX(float x) {
        return center.x + (x - getX()) * scale;
    }

    /**
     * Returns the rendering Y position for an object
     *
     * @param y Object's Y position
     * @return Graphics friendly rendering number
     */
    public float getRenderY(float y) {
        return center.y + (y - getY()) * scale;
    }

    /**
     * Returns the rendering X position for an object
     * with a specified with in relativity to the center
     *
     * @param x     Object's X position
     * @param width Object's Width
     * @return Graphics friendly rendering number
     */
    public float getRenderX(float x, float width) {
        return center.x + (x - getX() - width / 2) * scale;
    }

    /**
     * Returns the rendering Y position for an object
     * with a specified with in relativity to the center
     *
     * @param y      Object's Y position
     * @param height Object's Height
     * @return Graphics friendly rendering number
     */
    public float getRenderY(float y, float height) {
        return center.y + (y - getY() - height / 2) * scale;
    }


    public float getX() {
        return pos.x;
    }

    public float getY() {
        return pos.y;
    }
}
