package ui;

import main.Main;
import perspective.Camera;
import util.Vector;

import java.awt.*;
import java.awt.event.MouseEvent;

public interface UIElement {

	void render(Graphics g, Camera camera);

	default boolean mouseDown(MouseEvent e) {
		return mouseDown(Main.screen.mousePos(e));
	}

	default boolean mouseUp(MouseEvent e) {
		return mouseUp(Main.screen.mousePos(e));
	}

	default void mouseAt(Vector pos) {

	}

	default void update() {

	}

	default boolean mouseDown(Vector pos) {
		return false;
	}

	default boolean mouseUp(Vector pos) {
		return false;
	}

}
