package ui.premade;

import perspective.Camera;
import ui.SizedButton;
import util.Colors;
import util.Maths;
import util.Vector;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class HueSlider extends SizedButton {

	private BufferedImage image;
	private boolean clicked = false;
	private final ColorPickerRGB picker;
	private float huePercent = 0F;

	public HueSlider(ColorPickerRGB picker) {
		this.picker = picker;
		resize(Vector.zero, Vector.one);
		genImage();
	}

	public void resize(Vector pos, Vector size) {
		super.resize(pos, size);
		genImage();
	}

	private void genImage() {
		int pixels = Math.max(1, (int) pos.x);
		image = new BufferedImage(pixels, 1, BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < pixels; i++) {
			image.setRGB(i, 0, Colors.fromHSV((float) i / pixels * 360, 1, 1).getRGB());
		}
	}

	@Override
	protected void hitAction(Vector mousePos) {
		clicked = true;
	}

	@Override
	public boolean mouseUp(MouseEvent e) {
		clicked = false;
		return false;
	}

	@Override
	public void mouseAt(Vector pos) {
		super.mouseAt(pos);

		if (clicked) {
			toMouse(pos.x);
			updateHue();
		}
	}

	private void toMouse(float x) {
		huePercent = Maths.clamp((x - findTopLeft().x) / size.x, 0, 1);
	}

	private void updateHue() {
		picker.hueChange(Colors.fromHSV(huePercent * 360, 1, 1));
	}

	@Override
	public void render(Graphics g, Camera camera) {
		g.drawImage(image, (int) (pos.x - size.x / 2), (int) (pos.y - size.y / 2), (int) size.x, (int) size.y, null);
		// TODO: slider
	}
}
