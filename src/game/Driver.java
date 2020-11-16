package game;

import game.screens.BaseScreen;
import game.screens.GameScreen;
import screens.PolyScreen;
import screens.Screen;
import ui.TextBox;
import util.Vector;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class Driver extends JPanel {

	public static final int WIDTH, HEIGHT; // sizes may be off for a secondary monitor TODO: fix
	private static final int SCREEN_WIDTH, SCREEN_HEIGHT;
	private static final int screenNum = 1;

	public static final Vector SCREEN_CENTER;

	static {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		SCREEN_WIDTH = screenSize.width;
		SCREEN_HEIGHT = screenSize.height;

		Rectangle r = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		WIDTH = r.width;
		HEIGHT = r.height;

		SCREEN_CENTER = new Vector(WIDTH / 2F, HEIGHT / 2F);
	}

	// utilities
	private static JFrame frame;
	private final BufferedImage image;
	private final Graphics g;
	private final Timer timer;

	public static float deltaTime = 0F;

	private static final List<GameScreen> screenList = new ArrayList<>();
	public static GameScreen screen;

	private long lastMiliTime = System.currentTimeMillis();

	public static TextBox typingIn;

	public Driver() {

		image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
		g = image.getGraphics();

		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(3));

		screen = new BaseScreen();
		screenList.add(screen);

		/// utility setup
		timer = new Timer(0, new TimerListener());
		timer.start();
		addMouseListener(new Mouse());
		addMouseMotionListener(new MouseMotion());
		//addMouseWheelListener(new MouseWheel());
		addKeyListener(new Keyboard());
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);

		fullscreen();
	}

	public static PolyScreen debugScreen() {
		return (PolyScreen) screen;
	}

	private class TimerListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			long current = System.currentTimeMillis();

			deltaTime = (current - lastMiliTime) / 1000F;
			screen.update(deltaTime);
			screen.render(g);
			repaint();

			lastMiliTime = current;
		}
	}

	private static class MouseWheel implements MouseWheelListener {
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			screen.mouseScrollEvent(e);
		}
	}

	private static class MouseMotion implements MouseMotionListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			screen.mouseDrag(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			screen.mouseMove(e);
		}
	}

	private static class Mouse implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {

		}

		@Override
		public void mousePressed(MouseEvent e) {

			if (typingIn != null) {
				if (!typingIn.on(screen.mousePos(e))) {
					typingIn.clickOff();
					return;
				}
			}

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

	private static class Keyboard implements KeyListener {

		@Override
		public void keyTyped(KeyEvent e) {

		}

		@Override
		public void keyPressed(KeyEvent e) {

			if (typingIn != null) {
				typingIn.keyPressed(e);
				return;
			}

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
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
	}

	public static void minimize() {
		frame.setState(Frame.ICONIFIED);
	}

	public static void main(String[] args) {

		frame = new JFrame("Art Time.");
		frame.setSize(WIDTH, HEIGHT); //+17 +48
		frame.setLocation(0, SCREEN_HEIGHT * (screenNum - 1));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new Driver());
		frame.setVisible(true);
	}
}
