package game;

import util.Vector;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;

public class TextUtil {

	public static int drawWrappedText(Graphics g, Color color, String text, float width, Vector tl) {
		g.setColor(color);
		return drawWrappedText(g, text, width, tl);
	}

		public static int drawWrappedText(Graphics g, String text, float width, Vector tl) {
		FontMetrics fMetrics = g.getFontMetrics();

		int wordHeight = fMetrics.getHeight();
		String[] words = text.split(" ");
		int i = 0;

		int x = (int) tl.x;
		int y = (int) tl.y;

		y += wordHeight;

		String line = "";
		while (i < words.length) {
			boolean newLine = false;
			String pre = line;
			line += " " + words[i];

			if (fMetrics.stringWidth(line) > width) {
				line = pre;
				newLine = true;
			}

			if (newLine) {
				g.drawString(line, x, y);
				line = words[i];
				y += wordHeight;
			}

			i++;
		}
		g.drawString(line, x, y);

		return y - (int)tl.y;
	}

}
