package screens;

import util.Vector;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

public interface Screen {

    void update();
    void render(Graphics g);

    default void numberDown(int num) {

    }

    default void mouseScrollEvent(MouseWheelEvent e) {
        scrolled(e.getWheelRotation());
    }

    default void scrolled(int amount) {

    }

    default void mouseDown(MouseEvent e) {

    }

    default void mouseUp(MouseEvent e) {

    }

    default void mouseMove(MouseEvent e) {

    }

    default void mouseDrag(MouseEvent e) {

    }

    default void keyDown(KeyEvent e) {

    }

    default void keyUp(KeyEvent e) {

    }

    default Vector mousePos(MouseEvent e) {
        return new Vector(e.getX(), e.getY());
    }
}
