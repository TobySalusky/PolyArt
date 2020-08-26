package util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public class Fonts {

	public static final Font textBox;

	static {
		textBox = new Font ("Courier New", Font.PLAIN, 20);
	}

	public static Vector fontSize(String text, Graphics g) {
		return fontSize(text, g.getFont(), g);
	}

	public static Vector fontSize(String text, Font font, Graphics g) {
		Rectangle2D rect = font.getStringBounds(text, ((Graphics2D)g).getFontRenderContext());
		return new Vector((float)rect.getWidth(), (float)rect.getHeight());
	}
}
