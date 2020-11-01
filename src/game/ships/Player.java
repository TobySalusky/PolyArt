package game.ships;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import poly.Polygon;
import transformation.RotateTransform;
import transformation.ScaleTransform;
import util.Maths;
import util.Vector;

public class Player extends Entity {


	private static final Polygon bodyPoly;

	static {
		Polygon poly = (Polygon) JsonReader.jsonToJava("{\"@type\":\"poly.Polygon\",\"vertices\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"util.Vector\",\"x\":4.5776367E-5,\"y\":-333.76813},{\"@type\":\"util.Vector\",\"x\":215.85081,\"y\":-190.97382},{\"@type\":\"util.Vector\",\"x\":252.55084,\"y\":-36.631138},{\"@type\":\"util.Vector\",\"x\":0.0,\"y\":24.231888},{\"@type\":\"util.Vector\",\"x\":-252.55084,\"y\":-36.631126},{\"@type\":\"util.Vector\",\"x\":-215.85077,\"y\":-190.97389}]},\"color\":{\"@id\":1,\"value\":-14596,\"frgbvalue\":null,\"fvalue\":null,\"falpha\":0.0,\"cs\":null},\"modifiers\":{\"@type\":\"java.util.ArrayList\"},\"lastOutput\":[{\"vertices\":{\"@type\":\"java.util.ArrayList\",\"@items\":[{\"@type\":\"util.Vector\",\"x\":4.5776367E-5,\"y\":-333.76813},{\"@type\":\"util.Vector\",\"x\":215.85081,\"y\":-190.97382},{\"@type\":\"util.Vector\",\"x\":252.55084,\"y\":-36.631138},{\"@type\":\"util.Vector\",\"x\":0.0,\"y\":24.231888},{\"@type\":\"util.Vector\",\"x\":-252.55084,\"y\":-36.631126},{\"@type\":\"util.Vector\",\"x\":-215.85077,\"y\":-190.97389}]},\"color\":{\"@ref\":1},\"modifiers\":{\"@type\":\"java.util.ArrayList\"},\"lastOutput\":null,\"storedEdges\":null}],\"storedEdges\":null}\n");
		bodyPoly = new RotateTransform(Maths.HalfPI, Vector.zero).appliedTo(new ScaleTransform(0.1F, 0.1F, Vector.zero).appliedTo(poly));
	}

	public Player(Vector pos) {
		super(pos);
	}

	public void handleInput(int xAxis, int yAxis, float deltaTime) {
		Vector norm = new Vector(xAxis, yAxis);
		if (norm.mag() > 0) {
			norm = norm.normed();
		}

		vel.add(norm.multed(300 * deltaTime));
	}

	@Override
	public Polygon unbakedModel() {
		return bodyPoly;
	}


}
