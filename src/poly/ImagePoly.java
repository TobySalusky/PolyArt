package poly;

import perspective.Camera;
import util.Generator;
import util.Gizmo;
import util.VecRect;
import util.Vector;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePoly extends Polygon {

	private final BufferedImage image;
	private float alpha = 0.5F;

	public ImagePoly(BufferedImage image, Vector mid, float size) {
		super(Color.WHITE);
		this.image = image;
		Vector half = Vector.one.multed(size);
		addPoint(mid.subbed(half));
		addPoint(mid.added(half));
	}

	@Override
	public boolean pointInside(Vector vector) {
		Vector[] range = findRange();
		return vector.between(range[0], range[1]);
	}

	@Override
	public void render(Graphics g, Camera camera) {
		Graphics2D g2d = (Graphics2D)g;
		Composite preComp = g2d.getComposite();
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
		VecRect rect = findRect();
		Gizmo.drawCenteredImage(g, image, camera.toScreen(rect.getPos()), rect.getSize().multed(camera.getScale()));
		g2d.setComposite(preComp);
	}
}
