package modifiers;

import perspective.Camera;
import poly.Polygon;

import java.awt.*;

public interface Modifier {

	default void apply(Polygon polygon) {
		System.out.println("DEBUG: " + getClass().toString() + "'s application functionality is yet to be implemented...");
	}

	default void render(Graphics g, Camera camera, Polygon polygon) {} // render extras

	Polygon[] create(Polygon[] polygon);
}
