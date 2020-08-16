package main;

import screens.PolyScreen;
import screens.Screen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Main extends JPanel {

    public static final int WIDTH, HEIGHT;
    private static final int screenNum = 1;

    static {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        WIDTH = screenSize.width;
        HEIGHT = screenSize.height;
    }

    // utilities
    private static JFrame frame;
    private final BufferedImage image;
    private final Graphics g;
    private final Timer timer;


    private final List<Screen> screenList = new ArrayList<>();
    private Screen screen;

    public Main() {

        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        g = image.getGraphics();

        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));

        screen = new PolyScreen();
        screenList.add(screen);

        /// utility setup
        timer = new Timer(0, new TimerListener());
        timer.start();
        addMouseListener(new Mouse());
        addMouseMotionListener(new MouseMotion());
        addMouseWheelListener(new MouseWheel());
        addKeyListener(new Keyboard());
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        fullscreen();
    }

    private class TimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            screen.update();
            screen.render(g);
            repaint();
        }
    }

    private class MouseWheel implements MouseWheelListener {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

        }
    }

    private class MouseMotion implements MouseMotionListener {

        @Override
        public void mouseDragged(MouseEvent e) {
            screen.mouseDrag(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            screen.mouseMove(e);
        }
    }

    private class Mouse implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            screen.mouseDown(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            screen.mouseUp(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    private class Keyboard implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();

            char keyChar = e.getKeyChar();
            if (keyChar >= '0' && keyChar <= '9') {
                screen.numberDown(keyChar - '0');
            }

            if (keyCode == KeyEvent.VK_ESCAPE) {
                System.out.println("ESCAPED PRESSED! exiting...");
                System.exit(0);
            } else {
                screen.keyDown(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            screen.keyUp(e);
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
    }

    public static void fullscreen() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
    }

    public static void main(String[] args) {

        frame = new JFrame("Art Time.");
        frame.setSize(WIDTH, HEIGHT); //+17 +48
        frame.setLocation(200, 200 + 1080 * (screenNum - 1));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(new Main());
        frame.setVisible(true);
    }
}
