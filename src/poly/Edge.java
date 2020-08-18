package poly;

import util.Maths;
import util.Vector;

public class Edge {

    private final Vector start, end;

    public Edge(Vector start, Vector end) {
        this.start = start;
        this.end = end;
    }

    public float distTo(Vector point) {

        Vector low, high;
        if (start.y < end.y) {
            low = start;
            high = end;
        } else {
            low = end;
            high = start;
        }

        Vector diff = high.subbed(low);
        float changeAngle = -diff.angle();

        Vector highRot = high.copy();
        highRot.rotateAround(changeAngle, low);

        Vector pointRot = point.copy();
        pointRot.rotateAround(changeAngle, low);

        Vector closest = new Vector(pointRot.x, highRot.y);
        closest.x = Maths.clamp(closest.x, Math.min(low.x, highRot.x), Math.max(low.x, highRot.x));

        return pointRot.subbed(closest).mag();
    }

    public boolean eitherIs(Vector point) {
        return  (start == point || end == point);
    }

    public boolean yInRange(float y) {
        return ((y >= start.y && y <= end.y) || (y >= end.y && y <= start.y));
    }

    public float xAtY(float y) {
        Vector low, high;

        if (start.y < end.y) {
            low = start;
            high = end;
        } else {
            low = end;
            high = start;
        }

        float progress = (y - low.y) / (high.y - low.y);

        return low.x + (high.x - low.x) * progress;
    }

    public Vector getStart() {
        return start;
    }

    public Vector getEnd() {
        return end;
    }
}
